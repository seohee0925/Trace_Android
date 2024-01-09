package com.example.trace_android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.trace_android.model.Post



class PostDetailsDialogFragment : DialogFragment() {
    private var post: Post? = null

    fun setMarkerDetails(post: Post) {
        this.post = post
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val params = window?.attributes
        params?.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params?.y = 16 // 16dp의 마진
        window?.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.postViewContents).text = post?.content

        val imageView = view.findViewById<ImageView>(R.id.postViewImage)
        post?.let {
            if (!it.image.isNullOrEmpty() || !it.imageExtra.isNullOrEmpty()) {
                val combinedImageString = it.image + (it.imageExtra ?: "")
                val imageBitmap = base64ToBitmap(combinedImageString)
                imageView.setImageBitmap(imageBitmap)
                imageView.visibility = View.VISIBLE
            }
        }

        // 취소 버튼 클릭 리스너 설정
        view.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            dismiss() // 프래그먼트 닫기
        }
    }

    //base64 to image
    private fun base64ToBitmap(base64Str: String): Bitmap {
        val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 커스텀 다이얼로그 레이아웃 설정
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

}
