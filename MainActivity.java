package com.example.hashh.souschef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    static String Username;

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_pass);

    }

    public void login(View view){
        String login_name = username.getText().toString();
        String login_pass = password.getText().toString();
        String method = "login";

        username.setText("");
        password.setText("");

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, login_name, login_pass);
        /*if(isLogin==true) {
            startActivity(new Intent(this, SearchIngredients.class));
        }*/

    }

    public void register(View view){
        Register.finish = false;
        startActivity(new Intent(this, Register.class));
    }
}
