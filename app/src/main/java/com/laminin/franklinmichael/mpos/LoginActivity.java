package com.laminin.franklinmichael.mpos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        usernameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                if(usernameEditText.getText().toString().length() > 1 && passwordEditText.getText().length() > 1) {
                    Intent intent = new Intent(this, InformationActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Please Enter username and password", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
