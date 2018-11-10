package com.earaujo.usinglivedata.rest

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.earaujo.usinglivedata.rest.model.Resource
import com.earaujo.usinglivedata.rest.model.reddit.SearchModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

object ApiUtil {

    fun searchReddit(
        search: String,
        limit: Int
    ): LiveData<Resource<SearchModel>> {

        val result = MutableLiveData<Resource<SearchModel>>()

        result.postValue(Resource.loading(null as SearchModel?))

        val retrofit = ApiClient.client
        val apiService = retrofit.create(ApiInterface::class.java)
        val call = apiService.search(search, limit)
        call.enqueue(object : Callback<SearchModel> {
            override fun onResponse(
                call: Call<SearchModel>,
                response: Response<SearchModel>
            ) {
                if (response.code() >= 200 && response.code() <= 299) {
                    if (response.body() != null) {
                        result.postValue(Resource.success(response.body()!!))
                    } else {
                        result.postValue(Resource.error("Erro desconhecido", null as SearchModel?))
                    }
                } else {
                    result.postValue(Resource.error("Erro desconhecido", null as SearchModel?))
                }
            }

            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                result.postValue(Resource.error("No Internet Connection", null as SearchModel?))
            }
        })

        return result
    }
}
