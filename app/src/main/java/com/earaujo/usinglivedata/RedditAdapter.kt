package com.earaujo.usinglivedata

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.earaujo.usinglivedata.rest.model.reddit.Child
import kotlinx.android.synthetic.main.item_list_reddit.view.*

class RedditAdapter(
    private val activity: Activity,
    private val items: List<Child>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return getViewHolder(parent, inflater)
    }

    private fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v1 = inflater.inflate(R.layout.item_list_reddit, parent, false)
        viewHolder = RedditVH(v1)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val brandVH = holder as RedditVH
        brandVH.bind(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class RedditVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var iv_reddit = itemView.iv_reddit
        private var tv_title = itemView.tv_title

        fun bind(position: Int) {
            val item = items[position]
            setData(item)
        }

        private fun setData(item: Child) {
            tv_title.text = item.data.title
            Glide
                .with(activity)
                .load(item.data.thumbnail)
                .into(iv_reddit)
        }
    }
}
