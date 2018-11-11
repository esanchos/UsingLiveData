package com.earaujo.usinglivedata

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.earaujo.usinglivedata.rest.ApiUtil
import com.earaujo.usinglivedata.rest.model.Resource
import com.earaujo.usinglivedata.rest.model.Status.*
import com.earaujo.usinglivedata.rest.model.reddit.Child
import com.earaujo.usinglivedata.rest.model.reddit.SearchModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var adapter: RedditAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        btn_search.setOnClickListener(this)
    }

    private fun performSearch(search: String) {
        val liveData = ApiUtil.searchReddit(search, 20)
        liveData.observe(this, object : Observer<Resource<SearchModel>> {
            override fun onChanged(searchResult: Resource<SearchModel>?) {
                when (searchResult!!.status) {
                    LOADING -> {
                        et_title.visibility = View.VISIBLE
                        et_title.text = "Loading..."
                    }
                    SUCCESS -> {
                        setupRecyclerView(searchResult.data?.data?.children!!)
                        et_title.visibility = View.GONE
                        liveData.removeObserver(this)
                    }
                    ERROR -> {
                        et_title.visibility = View.GONE
                        liveData.removeObserver(this)
                    }
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
