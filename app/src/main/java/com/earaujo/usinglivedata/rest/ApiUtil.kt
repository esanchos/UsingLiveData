package com.earaujo.usinglivedata.rest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.earaujo.usinglivedata.rest.model.reddit.SearchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiUtil {

    fun searchReddit(
        search: String,
        limit: Int
    ): LiveData<SearchModel> {

        val result = MutableLiveData<SearchModel>()

        val retrofit = ApiClient.client
        val apiService = retrofit.create(ApiInterface::class.java)
        val call = apiService.search(search, limit)
        call.enqueue(object : Callback<SearchModel> {
            override fun onResponse(
                call: Call<SearchModel>,
                response: Response<SearchModel>
            ) {
                result.postValue(response.body()!!)
            }

            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                result.postValue(null)
            }
        })

        return result
    }
}
