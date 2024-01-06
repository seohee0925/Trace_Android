package com.example.trace_android

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 바텀 시트 다이얼로그 프래그먼트로, 사용자 입력을 받기 위한 UI를 제공합니다.
 */
class InputBottomSheetFragment : BottomSheetDialogFragment() {

    // 프래그먼트의 뷰가 생성될 때 호출됩니다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바텀 시트의 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_input_bottom_sheet, container, false)

        // '취소' 버튼을 찾아 클릭 리스너를 설정합니다. 버튼 클릭 시 바텀 시트를 닫습니다.
        val cancelButton = view.findViewById<Button>(R.id.buttonCancel)
        cancelButton.setOnClickListener {
            dismiss() // 바텀 시트를 닫습니다.
        }

        return view
    }

    // 프래그먼트의 뷰가 시작될 때 호출됩니다.
    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        view?.post {
            val parent = requireView().parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            if (behavior is BottomSheetBehavior<*>) {
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                behavior.peekHeight = screenHeight - dpToPx(15) // 화면 높이에서 15dp만큼 빼기
                parent.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    // dp를 픽셀 단위로 변환하는 유틸리티 함수
    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    // 프래그먼트의 뷰가 생성되고 난 후 호출됩니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 텍스트 입력을 위한 EditText를 찾고 포커스를 설정합니다.
        val editTextUserInput = view.findViewById<EditText>(R.id.editTextUserInput)
        editTextUserInput.requestFocus()

        // 소프트 키보드가 자동으로 나타나도록 설정합니다.
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

}