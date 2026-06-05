package com.phukrajhealthindia.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private static final String WEBSITE_URL = "https://phukrajhealth.github.io/Phukraj-health-care-/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Phukrajhealthindia");
        }

        SharedPreferences prefs = getSharedPreferences("phukraj_prefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");
        TextView tvWelcome = findViewById(R.id.tv_welcome);
        tvWelcome.setText("🌿 Welcome, " + userName);

        progressBar   = findViewById(R.id.progress_bar);
        swipeRefresh  = findViewById(R.id.swipe_refresh);
        webView       = findViewById(R.id.webview);

        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.gold, null));
        swipeRefresh.setOnRefreshListener(() -> webView.reload());

        setupWebView();
        webView.loadUrl(WEBSITE_URL);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(android.view.View.VISIBLE);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(android.view.View.GONE);
                swipeRefresh.setRefreshing(false);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("https://wa.me") || url.startsWith("whatsapp://")) {
                    recordOrder(url);
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                if (url.startsWith("tel:")) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    return true;
                }
                if (url.startsWith("mailto:")) {
                    startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url)));
                    return true;
                }
                return false;
            }
        });
    }

    private void recordOrder(String waUrl) {
        try {
            String product = "Product Order";
            if (waUrl.contains("text=")) {
                String text = Uri.decode(waUrl.split("text=")[1]);
                if (text.contains("Product:")) {
                    product = text.split("Product:")[1].split("\\n")[0].trim();
                } else if (text.contains("CART ORDER")) {
                    product = "Cart Order";
                }
            }
            SharedPreferences prefs = getSharedPreferences("phukraj_prefs", MODE_PRIVATE);
            String ordersJson = prefs.getString("orders", "[]");
            JSONArray orders = new JSONArray(ordersJson);
            JSONObject order = new JSONObject();
            String date = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
            order.put("product", product);
            order.put("date", date);
            orders.put(order);
            prefs.edit().putString("orders", orders.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (d, w) -> {
                    getSharedPreferences("phukraj_prefs", MODE_PRIVATE).edit().clear().apply();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
