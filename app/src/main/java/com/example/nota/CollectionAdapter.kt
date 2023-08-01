package com.example.nota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.nota.BindingAdapter.loadImage

class CollectionAdapter(private var collectionList: MutableList<CollectionData>) :
    RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder>() {

    // 아이템 클릭 리스너 인터페이스
    interface OnItemClickListener {
        fun onItemClick(collectionData: CollectionData)
    }

    // 클릭 리스너 변수
    private var onItemClickListener: OnItemClickListener? = null

    // 클릭 리스너 설정 메서드
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }


    // View Holder 클래스
    class CollectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ViewHolder의 뷰들 선언
        val iv_list_collection_img: ImageView = itemView.findViewById(R.id.iv_list_collection_img)
        val tv_list_collection_title: TextView = itemView.findViewById(R.id.tv_list_collection_title)
        val rb_list_collection: AppCompatRatingBar = itemView.findViewById(R.id.rb_list_collection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        // ViewHolder 생성 및 레이아웃 inflate
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_collection, parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        // 데이터 바인딩
        val collectionData = collectionList[position]
        holder.tv_list_collection_title.text = collectionData.title
        holder.rb_list_collection.rating = collectionData.rating.toFloat()
        loadImage(holder.iv_list_collection_img, collectionData.title, collectionData.email)

        // 아이템을 클릭했을 때의 리스너 설정
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(collectionData)
        }
    }

    override fun getItemCount(): Int {
        // 아이템 개수 반환
        return collectionList.size
    }
}
