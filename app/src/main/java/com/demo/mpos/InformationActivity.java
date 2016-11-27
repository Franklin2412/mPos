package com.demo.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sinvoicedemo.R;

public class InformationActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Button requestPaymentButton = (Button) findViewById(R.id.request_payment_button);
        requestPaymentButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.request_payment_button:
                Intent intent = new Intent(this, RequestPaymentActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
