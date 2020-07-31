package com.krrtvl.court;

import com.krrtvl.court.domain.Visits;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @GET("/visits/json/id")
    Call<Visits> getVisitsById(@Query("id") Long id);
    @GET("/visits/json/date")
    Call<List<Visits>> getVisitsByDate(@Query("date") String date);
    @GET("/visits/json/page")
    Call<List<Visits>> getVisitsPage(@Query("page") Integer page, @Query("pagesize") Integer pagesize);
    @GET("/editvisits/json/put")
    Call<Visits> putVisits(@Query("id") String id, @Query("date") String date, @Query("btime") String btime, @Query("etime") String etime, @Query("name") String name);
    @GET("/editvisits/json/delete/{id}")
    Call<Long> deleteVisits(@Path("id") Long id);
}
