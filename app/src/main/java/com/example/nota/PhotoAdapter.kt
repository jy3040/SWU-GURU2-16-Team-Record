import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nota.R

class PhotoAdapter(private val photoUrls: List<String>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    // 뷰 홀더를 생성하고 레이아웃을 연결하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_grid_list, parent, false)
        return PhotoViewHolder(view)
    }

    // 각 뷰 홀더에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val imageUrl = photoUrls[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .centerCrop()
            .into(holder.ivPhoto)
    }

    // 뷰 홀더 개수를 반환하는 함수
    override fun getItemCount(): Int {
        return photoUrls.size
    }

    // 뷰 홀더 클래스를 정의하는 내부 클래스
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPhoto: ImageView = itemView.findViewById(R.id.iv_collection_album_image)
    }
}
