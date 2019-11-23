package com.example.shubchintak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    private TextView mNotiData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
String dataMessage=getIntent().getStringExtra("from_user");
        mNotiData=(TextView)findViewById(R.id.notify_text);
        mNotiData.setText(dataMessage+" has low cardiovascular fitness");
    }
}
