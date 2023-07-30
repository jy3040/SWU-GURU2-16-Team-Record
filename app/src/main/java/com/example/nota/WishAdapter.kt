package com.example.nota

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

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
        private val ibWishMenu: ImageButton = itemView.findViewById(R.id.ib_wish_menu)

        fun bind(wishData: WishData) {
            tvTitle.text = wishData.title
            tvContent.text = wishData.content

            ibWishMenu.setOnClickListener {
                val position = bindingAdapterPosition // 이 부분을 수정하지 않고 그대로 사용해도 무방합니다.
                if (position != RecyclerView.NO_POSITION) {
                    val wishData = wishList[position]

                    // 팝업 메뉴를 띄우기 위해 PopupMenu 객체를 생성합니다.
                    val popupMenu = PopupMenu(itemView.context, ibWishMenu)
                    popupMenu.inflate(R.menu.toolbar_collection) // 메뉴 리소스 파일을 inflate 합니다.

                    // 팝업 메뉴의 메뉴 아이템 클릭 이벤트 처리
                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_edit -> {
                                // "수정" 메뉴 클릭 시 처리할 로직 작성
                                val intent = Intent(itemView.context, WriteWishActivity::class.java)
                                intent.putExtra("wishData",wishData)
                                itemView.context.startActivity(intent)
                                true
                            }
                            R.id.action_delete -> {
                                // "삭제하기" 메뉴 클릭 시 처리할 로직 작성
                                // 예를 들어 데이터 삭제 등의 동작 수행
                                val db = FirebaseFirestore.getInstance()
                                val email = wishData.email
                                val title = wishData.title
                                val documentPath = "$email"+"_wish/$title"

                                db.document(documentPath)
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(itemView.context, "데이터 삭제 성공", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(itemView.context, MainActivity::class.java)
                                        itemView.context.startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(itemView.context, "데이터 삭제 실패", Toast.LENGTH_SHORT).show()
                                    }
                                true
                            }
                            else -> false
                        }
                    }

                    // 팝업 메뉴를 보여줍니다.
                    popupMenu.show()
                }
            }
        }
    }
}