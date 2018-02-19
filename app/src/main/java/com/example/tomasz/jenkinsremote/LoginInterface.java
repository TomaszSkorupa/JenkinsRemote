package com.example.tomasz.jenkinsremote;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface LoginInterface {


    @POST("api/json?&tree=jobs[name,color,lastBuild[timestamp,number,result]]")
    Call<Jenkins> getAllProjects(@Header("Authorization") String authorization);

    @POST("/job/{project}/api/json?&tree=builds[number,timestamp,number,result]")
    Call<Project> getBuilds(@Header("Authorization") String authorization,
                            @Path("project") String project);


    @POST("/job/{project}/{build_number}/consoleText")
    Call<String> getBuildConsole(@Header("Authorization") String authorization,
                                 @Path("project") String project,
                                 @Path("build_number") String buildNumber);


    @POST("/job/{project}/build")
    Call<String> triggerBuild(@Header("Authorization") String authorization,
                              @Path("project") String project);



}
