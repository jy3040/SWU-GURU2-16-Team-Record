package com.example.nota

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.ContentProviderCompat.requireContext
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
    lateinit var tv_collection_detail_opt1_title1:TextView
    lateinit var tv_collection_detail_opt1_detail1:TextView
    lateinit var tv_collection_detail_opt1_title2:TextView
    lateinit var tv_collection_detail_opt1_detail2:TextView
    lateinit var tv_collection_detail_opt1_title3:TextView
    lateinit var tv_collection_detail_opt1_detail3:TextView
    lateinit var rv_collections_list:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        iv_collections_detail_thumbnail = findViewById(R.id.iv_collections_detail_thumbnail)
        tv_collection_detail_title =findViewById(R.id.tv_collection_detail_title)
        tv_collection_detail_date = findViewById(R.id.tv_collection_detail_date)
        rb_collection_detail = findViewById(R.id.rb_collection_detail)
        tv_collection_detail_record = findViewById(R.id.tv_collection_detail_record)
        tv_collection_detail_opt1_title1 = findViewById(R.id.tv_collection_detail_opt1_title1)
        tv_collection_detail_opt1_detail1 = findViewById(R.id.tv_collection_detail_opt1_detail1)
        tv_collection_detail_opt1_title2 = findViewById(R.id.tv_collection_detail_opt1_title2)
        tv_collection_detail_opt1_detail2 = findViewById(R.id.tv_collection_detail_opt1_detail2)
        tv_collection_detail_opt1_title3 = findViewById(R.id.tv_collection_detail_opt1_title3)
        tv_collection_detail_opt1_detail3 = findViewById(R.id.tv_collection_detail_opt1_detail3)
        rv_collections_list = findViewById(R.id.rv_collections_list)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.tb_collection_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        val collectionData = intent.getSerializableExtra("collectionData") as? CollectionData

        // 데이터가 null이 아니면 해당 데이터를 사용하여 UI 업데이트 등을 수행
        if (collectionData != null) {
            BindingAdapter.loadImage(iv_collections_detail_thumbnail, collectionData.title, collectionData.email)
            tv_collection_detail_title.text = collectionData.title
            tv_collection_detail_date.text=collectionData.Y.toString()+collectionData.M.toString()+collectionData.D.toString()
            rb_collection_detail.rating = collectionData.rating.toFloat()
            tv_collection_detail_record.text = collectionData.content
            if(collectionData.optiontitle !=null) {
                tv_collection_detail_opt1_title1.text = collectionData.optiontitle
                tv_collection_detail_opt1_detail1.text = collectionData.optioncontent
            }else{
                tv_collection_detail_opt1_title1.visibility = View.GONE
                tv_collection_detail_opt1_detail1.visibility = View.GONE
            }
            if(collectionData.optiontitle2 !=null) {
                tv_collection_detail_opt1_title2.text = collectionData.optiontitle2
                tv_collection_detail_opt1_detail2.text = collectionData.optioncontent2
            }else{
                tv_collection_detail_opt1_title2.visibility = View.GONE
                tv_collection_detail_opt1_detail2.visibility = View.GONE
            }
            if(collectionData.optiontitle3 !=null) {
                tv_collection_detail_opt1_title3.text = collectionData.optiontitle3
                tv_collection_detail_opt1_detail3.text = collectionData.optioncontent3
            }else{
                tv_collection_detail_opt1_title3.visibility = View.GONE
                tv_collection_detail_opt1_detail3.visibility = View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_collection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val collectionData = intent.getSerializableExtra("collectionData") as? CollectionData
        return when (item.itemId) {
            R.id.action_edit -> {
                // "수정하기" 메뉴 클릭 시 처리할 로직 작성
                // 예를 들어 수정 화면으로 이동하는 등의 동작 수행
                val intent = Intent(this, WriteCollectionActivity::class.java)
                intent.putExtra("collectionData",collectionData)
                startActivity(intent)
                true
            }
            R.id.action_delete -> {
                // "삭제하기" 메뉴 클릭 시 처리할 로직 작성
                // 예를 들어 데이터 삭제 등의 동작 수행
                val db = FirebaseFirestore.getInstance()
                val email = collectionData?.email
                val title = collectionData?.title
                val documentPath = "$email"+"_collection/$title"

                db.document(documentPath)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "데이터 삭제 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "데이터 삭제 실패", Toast.LENGTH_SHORT).show()
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}