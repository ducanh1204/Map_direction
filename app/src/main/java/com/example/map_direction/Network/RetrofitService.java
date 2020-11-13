package com.example.map_direction.Network;


import com.example.map_direction.gson.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("maps/api/directions/json")
    Call<Example> getHttp(@Query("origin") String origin,
                           @Query("destination") String destination,
                           @Query("waypoints") String waypoints,
                           @Query("key") String key);

}
