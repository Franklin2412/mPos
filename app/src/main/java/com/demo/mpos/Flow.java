package com.demo.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.sinvoicedemo.R;

/**
 * Created by shoaib on 11/27/16.
 */

public class Flow extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_selection);
    }

    public void userFlow(View userFlowButton){
        Intent intent = new Intent(this, com.example.sinvoicedemo.MainActivity.class);
        startActivity(intent);
    }

    public void merchantFlow(View merchantFlowButton){

        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);

    }
}
