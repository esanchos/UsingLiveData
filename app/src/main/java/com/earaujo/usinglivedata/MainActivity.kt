package com.earaujo.usinglivedata

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.earaujo.usinglivedata.rest.model.Status.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel

    protected inline fun <VM : ViewModel> viewModelFactory(crossinline f: () -> VM) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(aClass: Class<T>): T = f() as T
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(
            this,
            viewModelFactory { MainViewModel(this.application) }
        ).get(MainViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        installListener()
        btn_search.setOnClickListener(this)
    }

    fun installListener() {
        viewModel.searchTransform.observe(this, Observer { searchResult ->
            when (searchResult!!.status) {
                LOADING -> {
                    et_title.text = "Loading..."
                }
                SUCCESS -> {
                    et_title.text = searchResult.data?.data?.children?.get(0)?.data?.title
                }
                ERROR -> {
                    et_title.text = searchResult.message
                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_search -> {
                viewModel.getProducts(et_search.text.toString())
            }
        }
    }
}
