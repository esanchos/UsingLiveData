package com.earaujo.usinglivedata.rest;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import com.earaujo.usinglivedata.rest.model.Resource;
import com.earaujo.usinglivedata.rest.model.SearchModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiUtil {

    public static LiveData<Resource<SearchModel>> searchReddit(@NonNull final String search,
                                                               @NonNull final int limit) {

        final MutableLiveData<Resource<SearchModel>> result = new MutableLiveData<>();

        result.postValue(Resource.loading((SearchModel) null));

        Retrofit retrofit = ApiClient.getClient();
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        Call<SearchModel> call = apiService.search(search, limit);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(@NonNull Call<SearchModel> call,
                                   @NonNull Response<SearchModel> response) {
                if ((response.code() >= 200) && (response.code() <= 299)) {
                    if (response.body() != null) {
                        result.postValue(Resource.success(response.body()));
                    } else {
                        result.postValue(Resource.error("Erro desconhecido", (SearchModel) null));
                    }
                } else {
                    result.postValue(Resource.error("Erro desconhecido", (SearchModel) null));
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchModel> call, @NonNull Throwable t) {
                result.postValue(Resource.error("No Internet Connection", (SearchModel) null));
            }
        });

        return result;
    }
}
