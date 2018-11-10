package com.earaujo.usinglivedata

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Transformations
import com.earaujo.usinglivedata.rest.ApiUtil

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val perPage: Int = 2
    private val searchRequest = MediatorLiveData<String>()

    var searchTransform = Transformations.switchMap(searchRequest) {
        ApiUtil.searchReddit(it, perPage)
    }

    fun getProducts(search: String) {
        this.searchRequest.value = search
    }
}