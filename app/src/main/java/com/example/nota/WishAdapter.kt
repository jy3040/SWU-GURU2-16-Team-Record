package com.example.nota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WishAdapter(private val wishList: List<WishData>) :
    RecyclerView.Adapter<WishAdapter.WishViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_wish, parent, false)
        return WishViewHolder(view)
    }

    override fun onBindViewHolder(holder: WishViewHolder, position: Int) {
        val wishData = wishList[position]
        holder.bind(wishData)
    }

    override fun getItemCount(): Int {
        return wishList.size
    }

    inner class WishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_wish_title)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_wish_overview)

        fun bind(wishData: WishData) {
            tvTitle.text = wishData.title
            tvContent.text = wishData.content
        }
    }
}