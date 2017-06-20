package com.shake.hdsdk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import action.hdsdk.com.sdk.HDSDK;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_init = (Button) findViewById(R.id.btn_init);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        Button btn_order = (Button) findViewById(R.id.btn_order);
        Button btn_user = (Button) findViewById(R.id.btn_user);

        btn_init.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_order.setOnClickListener(this);
        btn_user.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_init:
                HDSDK.initialize(getApplicationContext());
                break;
            case R.id.btn_login:
                break;
            case R.id.btn_order:
                break;
            case R.id.btn_user:
                break;

        }
    }
}
