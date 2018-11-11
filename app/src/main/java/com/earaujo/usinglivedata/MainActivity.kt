package com.earaujo.usinglivedata

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.earaujo.usinglivedata.rest.ApiUtil
import com.earaujo.usinglivedata.rest.model.Status.*
import com.earaujo.usinglivedata.rest.model.reddit.Child
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var adapter: RedditAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private val searchRequest = MediatorLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        installSearch()
        btn_search.setOnClickListener(this)
    }

    private var searchTransform = Transformations.switchMap(searchRequest) {
        ApiUtil.searchReddit(it, 20)
    }

    private fun performSearch(search: String) {
        this.searchRequest.value = search
    }

    private fun installSearch() {
        searchTransform.observe(this, Observer { searchResult ->
            when (searchResult!!.status) {
                LOADING -> {
                    tv_title.visibility = View.VISIBLE
                    tv_title.text = "Loading..."
                }
                SUCCESS -> {
                    setupRecyclerView(searchResult.data?.data?.children!!)
                    tv_title.visibility = View.GONE
                }
                ERROR -> {
                    tv_title.visibility = View.GONE
                }
            }
        })
    }

    private fun setupRecyclerView(items: List<Child>) {
        adapter = RedditAdapter(this, items)
        linearLayoutManager = LinearLayoutManager(this)
        rv_reddit.layoutManager = linearLayoutManager
        rv_reddit.itemAnimator = DefaultItemAnimator()
        rv_reddit.adapter = adapter
        rv_reddit.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_search -> {
                performSearch(et_search.text.toString())
            }
        }
    }
}
