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
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

import static com.example.tomasz.jenkinsremote.MainActivity.jenkinsUrl;


public class ShowProjects extends Activity {


    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new GetAllProjects().execute();
            handler.postDelayed(runnable, 5000);
        }
    };
    ProgressBar progressBar;
    String authorization;
    SwipeRefreshLayout sw_refresh;
    SharedPreferences sharedPreferences;
    ListView listViewProjects;
    private ArrayList<Project> listOfProjects = new ArrayList<>();

    @Override
    protected void onPause() {
        super.onPause();
        ShowProjects.this.finishAffinity();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_main_layout);

        getActionBar().setTitle("JenkinsRemote/Projects");


        listViewProjects = findViewById(R.id.list_view_main);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        authorization = sharedPreferences.getString(ConstantsAndKeys.AUTHORIZATION, "");

        new GetAllProjects().execute();

        sw_refresh = findViewById(R.id.swiperefresh);

        sw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new GetAllProjects().execute();
                sw_refresh.setRefreshing(false);
            }
        });

        addListener();

        refresh();

    }

    private void addListener() {
        listViewProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(ShowProjects.this, ShowBuilds.class);
                intent.putExtra(ConstantsAndKeys.PROJECT_NAME, listOfProjects.get(i).getName());
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preference_activity_projects, menu);
        return true;
    }

    public void logoutClick(MenuItem item) {

        sharedPreferences.edit().remove(ConstantsAndKeys.LOGIN).remove(ConstantsAndKeys.PASSWORD).apply();

        Toast.makeText(ShowProjects.this, "Logging Out", Toast.LENGTH_LONG).show();

        //new RefreshThreadLoop().cancel(true);
        Intent logout = new Intent(ShowProjects.this, MainActivity.class);
        ShowProjects.this.finishAffinity();
        startActivity(logout);


    }

    public void onBackPressed() {

        System.exit(0);

    }

    private void refresh() {

        handler.postDelayed(runnable, 5000);
    }

    private class GetAllProjects extends AsyncTask<Object, Object, Void> {
        @Override
        protected Void doInBackground(Object... params) {


            if (!jenkinsUrl.isEmpty()) {


                final LoginInterface loginInterface = RetrofIt.getJenkins(getApplicationContext());

                retrofit2.Call<Jenkins> request = loginInterface.getAllProjects(authorization);

                request.enqueue(new Callback<Jenkins>() {
                    @Override
                    public void onResponse(retrofit2.Call<Jenkins> call, Response<Jenkins> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {

                                listOfProjects = response.body().getJenkinsProjects();

                                ArrayAdapter adapterProjects = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_custom_list_1, listOfProjects);

                                listViewProjects.setAdapter(adapterProjects);

                                System.out.println("Current number of threads" + Thread.activeCount());


                            }
                        }

                    }


                    @Override
                    public void onFailure(retrofit2.Call<Jenkins> call, Throwable t) {

                        t.printStackTrace();

                    }
                });
            }
            return null;
        }
    }


}








