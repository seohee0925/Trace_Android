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
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.gms.maps.model.LatLng

/**
 * 바텀 시트 다이얼로그 프래그먼트로, 사용자 입력을 받기 위한 UI를 제공합니다.
 */
class InputBottomSheetFragment : BottomSheetDialogFragment() {

    private var userLocation: LatLng? = null

    // 위치 데이터를 설정하는 메서드
    fun setLocation(location: LatLng) {
        userLocation = location

        // 바텀시트가 이미 시작되었다면 TextView를 즉시 업데이트합니다.
        view?.findViewById<TextView>(R.id.textViewCurrentLocation)?.text =
            getString(R.string.current_location_format, location.latitude, location.longitude)
    }

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

        // 현재 좌표를 TextView에 표시합니다.
        val textViewCurrentLocation = view.findViewById<TextView>(R.id.textViewCurrentLocation)
        userLocation?.let { location ->
            textViewCurrentLocation.text = getString(R.string.current_location_format, location.latitude, location.longitude)
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
                val fixedPeekHeight = (15 * resources.displayMetrics.density).toInt() // directly using calculated value
                behavior.peekHeight = screenHeight - fixedPeekHeight
                parent.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }


    // 프래그먼트의 뷰가 생성되고 난 후 호출됩니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // EditText에 포커스를 주고 키보드를 엽니다.
        val editTextUserInput = view.findViewById<EditText>(R.id.editTextUserInput)
        editTextUserInput.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        // 현재 좌표를 표시하는 TextView를 설정합니다.
        val textViewCurrentLocation = view.findViewById<TextView>(R.id.textViewCurrentLocation)
        userLocation?.let { location ->
            val locationText = "현재좌표: ${location.latitude}, ${location.longitude}"
            textViewCurrentLocation.text = locationText
        }
    }
}