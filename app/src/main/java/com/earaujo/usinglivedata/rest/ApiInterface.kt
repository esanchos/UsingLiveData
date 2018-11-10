package com.earaujo.usinglivedata.rest

import com.earaujo.usinglivedata.rest.model.reddit.SearchModel
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @GET("/search.json")
    fun search(
        @Query("q") search: String,
        @Query("limit") limit: Int
    ): Call<SearchModel>
}
