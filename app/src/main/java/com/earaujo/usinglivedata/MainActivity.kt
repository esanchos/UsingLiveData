package com.earaujo.usinglivedata

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.earaujo.usinglivedata.rest.ApiUtil
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

    var searchTransform = Transformations.switchMap(searchRequest) {
        ApiUtil.searchReddit(it, 20)
    }

    var titleObserver = Transformations.map(searchTransform) {
        it.data?.children?.get(0)?.data?.title
    }

    fun performSearch(search: String) {
        this.searchRequest.value = search
    }

    val twoRequests = zipLiveData(
        ApiUtil.searchReddit("ferrari", 1),
        ApiUtil.searchReddit("fiat", 1)
    )

    fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
        return MediatorLiveData<Pair<A, B>>().apply {
            var lastA: A? = null
            var lastB: B? = null

            fun update() {
                val localLastA = lastA
                val localLastB = lastB
                if (localLastA != null && localLastB != null)
                    this.value = Pair(localLastA, localLastB)
            }

            addSource(a) {
                lastA = it
                update()
            }
            addSource(b) {
                lastB = it
                update()
            }
        }
    }

    fun installSearch() {
        /*searchTransform.observe(this, Observer { searchResult ->
            if (searchResult != null) {
                setupRecyclerView(searchResult.data?.children!!)
            }
        })*/

        twoRequests.observe(this, Observer { twoSearchResult ->
            val finalList = mutableListOf<Child>()
            twoSearchResult?.first?.data?.children?.get(0)?.let { finalList.add(it) }
            twoSearchResult?.second?.data?.children?.get(0)?.let { finalList.add(it) }
            setupRecyclerView(finalList)
        })

        /*titleObserver.observe(this, Observer { title ->
            if (title != null) {
                tv_title.text = title
                tv_title.visibility = View.VISIBLE
            }
        })*/
    }

    private fun setupRecyclerView(items: List<Child>) {
        adapter = RedditAdapter(this, items)
        linearLayoutManager = LinearLayoutManager(this)
        rv_reddit.layoutManager = linearLayoutManager
        rv_reddit.itemAnimator = DefaultItemAnimator()
        rv_reddit.adapter = adapter;
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
