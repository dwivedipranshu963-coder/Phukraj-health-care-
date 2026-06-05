package com.phukrajhealthindia.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.json.JSONArray;
import org.json.JSONObject;

public class OrderHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Orders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayout container = findViewById(R.id.orders_container);
        TextView tvEmpty = findViewById(R.id.tv_empty);

        SharedPreferences prefs = getSharedPreferences("phukraj_prefs", MODE_PRIVATE);
        String ordersJson = prefs.getString("orders", "[]");
        String userName   = prefs.getString("user_name", "");
        String userPhone  = prefs.getString("user_phone", "");

        TextView tvProfile = findViewById(R.id.tv_profile);
        tvProfile.setText("👤 " + userName + " · 📱 " + userPhone);

        try {
            JSONArray orders = new JSONArray(ordersJson);
            if (orders.length() == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                for (int i = orders.length() - 1; i >= 0; i--) {
                    addOrderCard(container, orders.getJSONObject(i), i + 1);
                }
            }
        } catch (Exception e) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void addOrderCard(LinearLayout container, JSONObject order, int num) {
        View card = getLayoutInflater().inflate(R.layout.item_order, container, false);
        TextView tvNum     = card.findViewById(R.id.tv_order_num);
        TextView tvProduct = card.findViewById(R.id.tv_product);
        TextView tvDate    = card.findViewById(R.id.tv_date);
        TextView tvStatus  = card.findViewById(R.id.tv_status);

        tvNum.setText("#" + num);
        tvProduct.setText(order.optString("product", "Order"));
        tvDate.setText("📅 " + order.optString("date", ""));
        tvStatus.setText("✅ Sent to Praveen Kumar Sir");

        container.addView(card);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
