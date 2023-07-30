package com.example.nota

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class CollectionDetailActivity : AppCompatActivity() {

    lateinit var iv_collections_detail_thumbnail:ImageView
    lateinit var tv_collection_detail_title:TextView
    lateinit var tv_collection_detail_date:TextView
    lateinit var rb_collection_detail:AppCompatRatingBar
    lateinit var tv_collection_detail_record:TextView
    lateinit var tv_collection_detail_opt1_title:TextView
    lateinit var tv_collection_detail_opt1_detail:TextView
    lateinit var rv_collections_list:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        iv_collections_detail_thumbnail = findViewById(R.id.iv_collections_detail_thumbnail)
        tv_collection_detail_title =findViewById(R.id.tv_collection_detail_title)
        tv_collection_detail_date = findViewById(R.id.tv_collection_detail_date)
        rb_collection_detail = findViewById(R.id.rb_collection_detail)
        tv_collection_detail_record = findViewById(R.id.tv_collection_detail_record)
        tv_collection_detail_opt1_title = findViewById(R.id.tv_collection_detail_opt1_title)
        tv_collection_detail_opt1_detail = findViewById(R.id.tv_collection_detail_opt1_detail)
        rv_collections_list = findViewById(R.id.rv_collections_list)


        val collectionData = intent.getSerializableExtra("collectionData") as? CollectionData

        // 데이터가 null이 아니면 해당 데이터를 사용하여 UI 업데이트 등을 수행
        if (collectionData != null) {
            BindingAdapter.loadImage(iv_collections_detail_thumbnail, collectionData.title, collectionData.email)
            tv_collection_detail_title.text = collectionData.title
            tv_collection_detail_date.text=collectionData.Y.toString()+collectionData.M.toString()+collectionData.D.toString()
            rb_collection_detail.rating = collectionData.rating.toFloat()
            tv_collection_detail_record.text = collectionData.content
            if(collectionData.optiontitle !=null) {
                tv_collection_detail_opt1_title.text = collectionData.optiontitle
                tv_collection_detail_opt1_detail.text = collectionData.optioncontent
            }else{
                tv_collection_detail_opt1_title.visibility = View.GONE
                tv_collection_detail_opt1_detail.visibility = View.GONE
            }
            var Count = collectionData.imageUrls.size
            var email = collectionData.email
            var title = collectionData.title
            //
            BindingAdapter.loadImage(rv_collections_list, title, email, Count)

        } else {
            // 데이터가 null인 경우에 대한 처리
        }
    }
}