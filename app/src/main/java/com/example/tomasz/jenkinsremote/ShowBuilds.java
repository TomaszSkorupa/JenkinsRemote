package com.example.tomasz.jenkinsremote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShowBuilds extends Activity {


    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new GetAllBuilds().execute();
            handler.postDelayed(runnable, 5000);
        }
    };
    ListView listViewBuilds;
    SharedPreferences sharedPreferences;
    ArrayList<Jenkins.Build> listOfBuilds = new ArrayList<>();
    SwipeRefreshLayout sw_refresh;
    ArrayAdapter<Jenkins.Build> adapterBuilds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main_layout);

        getActionBar().setTitle("JenkinsRemote/Builds ");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listViewBuilds = findViewById(R.id.list_view_main);

        new GetAllBuilds().execute();

        sw_refresh = findViewById(R.id.swiperefresh);

        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new GetAllBuilds().execute();

                adapterBuilds.notifyDataSetChanged();
                sw_refresh.setRefreshing(false);
            }
        });

        refresh();

        setListener();

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    private void setListener() {
        listViewBuilds.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ShowBuilds.this, BuildConsoleOutput.class);

                intent.putExtra(ConstantsAndKeys.PROJECT_NAME, getIntent().getStringExtra(ConstantsAndKeys.PROJECT_NAME));

                intent.putExtra(ConstantsAndKeys.BUILD_NUMBER, listOfBuilds.get(i).getNumber());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.build_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.trigger_build:
                triggerNewBuild();
                break;
        }

        return true;
    }

    private void triggerNewBuild() {

        LoginInterface serverApi = RetrofIt.getJenkins(this);

        String projectName = getIntent().getStringExtra(ConstantsAndKeys.PROJECT_NAME);

        String authorization = sharedPreferences.getString(ConstantsAndKeys.AUTHORIZATION, "");

        Call<String> request = serverApi.triggerBuild(authorization, projectName);

        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        Toast.makeText(ShowBuilds.this, "Build triggered", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                t.printStackTrace();
            }
        });
    }

    public void onBackPressed() {

        Intent backPressedIntent = new Intent(ShowBuilds.this, ShowProjects.class);
        handler.removeCallbacksAndMessages(null);
        ShowBuilds.this.finishAffinity();
        startActivity(backPressedIntent);

    }

    public void refresh() {

        handler.postDelayed(runnable, 5000);
    }

    public void logoutClick(MenuItem item) {

        sharedPreferences.edit().remove(ConstantsAndKeys.LOGIN).remove(ConstantsAndKeys.PASSWORD).apply();

        Toast.makeText(ShowBuilds.this, "Logging Out", Toast.LENGTH_LONG).show();

        Intent logout = new Intent(ShowBuilds.this, MainActivity.class);
        ShowBuilds.this.finishAffinity();
        startActivity(logout);


    }

    private class GetAllBuilds extends AsyncTask<Object, Object, Void> {
        @Override
        protected Void doInBackground(Object... params) {

            LoginInterface serverApi = RetrofIt.getJenkins(getApplicationContext());

            String projectName = getIntent().getStringExtra(ConstantsAndKeys.PROJECT_NAME);

            String authorization = sharedPreferences.getString(ConstantsAndKeys.AUTHORIZATION, "");

            Call<Project> request = serverApi.getBuilds(authorization, projectName);


            request.enqueue(new Callback<Project>() {
                @Override
                public void onResponse(Call<Project> call, Response<Project> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            listOfBuilds = response.body().getBuilds();

                            adapterBuilds = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_custom_list_1, listOfBuilds);

                            listViewBuilds.setAdapter(adapterBuilds);

                            if (listOfBuilds.isEmpty()) {


                                Toast.makeText(ShowBuilds.this, "The List Of Builds Is Empty For This Project", Toast.LENGTH_LONG).show();

                            }

                            System.out.println("Number of threads" + Thread.activeCount());
                        }
                    }

                }

                @Override
                public void onFailure(Call<Project> call, Throwable t) {

                    t.printStackTrace();
                }
            });
            return null;
        }
    }


}


//            while (true) {
//
//                try {
//                    getAllBuilds();
//                    Thread.sleep(5000);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }















