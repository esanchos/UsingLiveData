package com.earaujo.usinglivedata.rest;

import com.earaujo.usinglivedata.rest.model.SearchModel;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiInterface {

    @GET("/search.json")
    Call<SearchModel> search(
            @Query("q") String search,
            @Query("limit") int limit
    );
}
