package com.demo.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.sinvoicedemo.R;

public class RequestPaymentActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout rowContainerLinearLayout;
    private ScrollView productListScrollView;
    private LinearLayout productDetailsLinearLayout;
    private EditText quantityEditText;
    private EditText unitPriceEditText;
    private TextView grandTotalTextView;

    private int quantity = 0;
    private double unitPrice = 0;
    private double grandTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_payment);

        rowContainerLinearLayout = (LinearLayout) findViewById(R.id.row_container_liner_layout);
        productListScrollView = (ScrollView) findViewById(R.id.productListScrollView);
        productDetailsLinearLayout = (LinearLayout) findViewById(R.id.product_details_linear_layout);
        productDetailsLinearLayout.setOnClickListener(this);

        Button proceedButton = (Button) findViewById(R.id.button_proceed);
        proceedButton.setOnClickListener(this);

        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        unitPriceEditText = (EditText) findViewById(R.id.unit_price_edit_text);
        grandTotalTextView = (TextView) findViewById(R.id.grand_total_text_view);

        quantityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    quantity = Integer.parseInt(charSequence.toString());
                    grandTotal = quantity * unitPrice;
                    grandTotalTextView.setText("" + grandTotal);
                }catch (Exception e){
                    quantity = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        unitPriceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    unitPrice = Integer.parseInt(charSequence.toString());
                    grandTotal = quantity * unitPrice;
                    grandTotalTextView.setText("" + grandTotal);
                }catch (Exception e){
                    quantity = 0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    private void addView(){
        rowContainerLinearLayout.addView(getLayoutInflater().inflate(R.layout.row, null));
        findViewById(R.id.productListScrollView).post(new Runnable() {
            @Override
            public void run() {
                productListScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.product_details_linear_layout:
//                addView();
                break;
            case R.id.button_proceed:
                Intent intent = new Intent(this, CustomerDetailsActivity.class);
                intent.putExtra(Constants.GRAND_TOTAL, grandTotal);
                intent.putExtra(Constants.QUANTITY, quantity);
                startActivity(intent);
            default:
                break;
        }
    }
}
