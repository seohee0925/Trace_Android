package com.example.trace_android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trace_android.API.ApiService
import com.example.trace_android.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

class PostAdapter(private val items: ArrayList<PostData>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.date_order_item, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.apply {
            bind(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        val contentTextView: TextView = v.findViewById(R.id.content)
        val placeTextView: TextView = v.findViewById(R.id.place)
        val dateTextView: TextView = v.findViewById(R.id.date)
        val imageView: ImageView = v.findViewById(R.id.contentImage)
        val deleteBtn: ImageButton = v.findViewById(R.id.delete_btn)

        init {
            deleteBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val postId = items[position].id // 포스트의 ID 가져오기

                    val apiService = RetrofitService.retrofit.create(ApiService::class.java)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = apiService.deletePost(postId)
                            if (response.isSuccessful) {
                                withContext(Dispatchers.Main) {
                                    items.removeAt(position)
                                    notifyItemRemoved(position)
                                }
                            } else {
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }

        fun bind(item: PostData) {
            contentTextView.text = item.content
            placeTextView.text = item.place

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(item.date)
            dateTextView.text = formattedDate

            if (item.combinedImage.isNotEmpty() && item.combinedImage != "null") {
                // 이미지가 있는 경우: 이미지를 디코딩하여 표시하고 높이를 200dp로 설정
                val decodedImageBytes: ByteArray = android.util.Base64.decode(item.combinedImage, android.util.Base64.DEFAULT)
                val decodedBitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)
                imageView.setImageBitmap(decodedBitmap)
                imageView.layoutParams.height = dpToPx(200, imageView.context)
                imageView.visibility = View.VISIBLE
            } else {
                // 이미지가 없는 경우: ImageView 숨기기 및 높이를 wrap_content로 설정
                imageView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                imageView.visibility = View.GONE
            }
        }
        // dp를 픽셀로 변환하는 함수
        private fun dpToPx(dp: Int, context: Context): Int {
            return (dp * context.resources.displayMetrics.density).toInt()
        }
    }
}