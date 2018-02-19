package com.example.tomasz.jenkinsremote;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends Activity {

    public final static String jenkinsUrl = ("http://192.168.1.3:8080/");
    SharedPreferences sharedPreferences;
    EditText login;
    EditText password;

    public static String getBase64String(String value) {
        try {
            return Base64.encodeToString(value.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login_editText);
        password = findViewById(R.id.password_editText);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        login.setText(sharedPreferences.getString(ConstantsAndKeys.LOGIN, ""));
        password.setText(sharedPreferences.getString(ConstantsAndKeys.PASSWORD, ""));

        String loginSP = sharedPreferences.getString(ConstantsAndKeys.LOGIN, "");
        String passwordSP = sharedPreferences.getString(ConstantsAndKeys.PASSWORD, "");

        if (!loginSP.isEmpty() && !passwordSP.isEmpty()) {
            connectToJenkins();
        }


    }

    public void connectToJenkins() {

//        String jenkinsUrl = sharedPreferences.getString(ConstantsAndKeys.URL_FROM_MENU, "");


        if (!jenkinsUrl.isEmpty()) {


            final String authorization = "Basic " + getBase64String(String.format("%s:%s",
                    login.getText().toString().trim(), password.getText().toString().trim()));


            LoginInterface loginInterface = RetrofIt.getJenkins(getApplicationContext());

            retrofit2.Call<Jenkins> request = loginInterface.getAllProjects(authorization);

            request.enqueue(new Callback<Jenkins>() {
                @Override
                public void onResponse(retrofit2.Call<Jenkins> call, Response<Jenkins> response) {

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Intent intent = new Intent(MainActivity.this, ShowProjects.class);

                            sharedPreferences.edit().putString(ConstantsAndKeys.AUTHORIZATION, authorization).apply();
                            sharedPreferences.edit().putString(ConstantsAndKeys.LOGIN, login.getText().toString().trim()).apply();
                            sharedPreferences.edit().putString(ConstantsAndKeys.PASSWORD, password.getText().toString().trim()).apply();

                            startActivity(intent);
                            finishAffinity();
                        }

                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect Login or Password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Jenkins> call, Throwable t) {

                    t.printStackTrace();
                    Toast.makeText(MainActivity.this, "Network connection not available, sorry", Toast.LENGTH_SHORT).show();


                }
            });
        } else {
            Toast.makeText(this, "The URL adress is empty", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void onClickLogIntoJenkins(View view) {

        connectToJenkins();

    }
}



