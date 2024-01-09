package com.example.trace_android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        val contentTextView: TextView = v.findViewById(R.id.content)
        val placeTextView: TextView = v.findViewById(R.id.place)
        val dateTextView: TextView = v.findViewById(R.id.date)
        val imageView: ImageView = v.findViewById(R.id.contentImage)

        fun bind(item: PostData) {
            contentTextView.text = item.content
            placeTextView.text = item.place
            dateTextView.text = item.date

            // base64 문자열을 디코딩하여 이미지뷰에 설정
            val decodedImageBytes: ByteArray = android.util.Base64.decode(item.combinedImage, android.util.Base64.DEFAULT)
            val decodedBitmap: Bitmap? = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)

            imageView.setImageBitmap(decodedBitmap)
        }
    }
}