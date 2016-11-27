package com.demo.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sinvoicedemo.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerDetailsActivity extends AppCompatActivity implements View.OnClickListener {


    Object grandTotal;
    Object quantity;

    EditText customerNameEditText;
    EditText customerPhNoEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_deatils);

        Button sendRequestButton = (Button) findViewById(R.id.send_request_button);
        sendRequestButton.setOnClickListener(this);

        TextView amountTextView = (TextView) findViewById(R.id.amount_text_view);
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);

        grandTotal = getIntent().getExtras().get(Constants.GRAND_TOTAL);
        quantity = getIntent().getExtras().get(Constants.QUANTITY);

        amountTextView.setText(null == grandTotal ? "0" : grandTotal.toString());
        quantityTextView.setText(null == quantity ? "0" : quantity.toString());

        customerNameEditText = (EditText) findViewById(R.id.customer_name_edit_text);
        customerPhNoEditText = (EditText) findViewById(R.id.customer_phone_no_edit_text);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send_request_button:
                String data = transmitPaymentDetailsToUser();
                Intent intent = new Intent(this, RequestSentActivity.class);
                intent.putExtra("merchant_data", data);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private String transmitPaymentDetailsToUser() {
        // prepare JSON data
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.GRAND_TOTAL, grandTotal.toString());
            jsonObject.put(Constants.QUANTITY, quantity.toString());
            jsonObject.put(Constants.CUSTOMER_NAME, customerNameEditText.getText().toString());
            jsonObject.put(Constants.CUSTOMER_PH_NO, customerPhNoEditText.getText().toString());
            return jsonObject.toString();
        } catch (JSONException e) {

            e.printStackTrace();

            return "";
        }


    }


}
