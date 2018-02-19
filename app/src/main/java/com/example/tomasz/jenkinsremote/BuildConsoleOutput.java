package com.example.tomasz.jenkinsremote;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BuildConsoleOutput extends Activity {

    SharedPreferences sharedPreferences;
    TextView textViewOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.build_console_activity);

        getActionBar().setTitle("JenkinsRemote/Build/ConsoleOutput");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        textViewOutput = findViewById(R.id.console_output);


        getConsoleOutput();
    }

    private void getConsoleOutput() {

        LoginInterface serverApi = RetrofIt.getJenkins(this);


        String projectName = getIntent().getStringExtra(ConstantsAndKeys.PROJECT_NAME);
        String buildNumber = getIntent().getStringExtra(ConstantsAndKeys.BUILD_NUMBER);


        String authorization = sharedPreferences.getString(ConstantsAndKeys.AUTHORIZATION, "");


        Call<String> request = serverApi.getBuildConsole(authorization, projectName, buildNumber);


        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        textViewOutput.setText(response.body());
                    }

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
