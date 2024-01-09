package com.example.trace_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.example.trace_android.model.PostRequest
import com.example.trace_android.retrofit.RetrofitService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import android.util.Base64
import com.example.trace_android.retrofit.GeocodingRepository
import java.util.Calendar
import java.util.Date


/**
 * 바텀 시트 다이얼로그 프래그먼트로, 사용자 입력을 받기 위한 UI를 제공합니다.
 */
class InputBottomSheetFragment : BottomSheetDialogFragment() {

    private var userLocation: LatLng? = null
    private lateinit var editTextUserInput: EditText // 멤버 변수로 선언
    private lateinit var buttonContainer: LinearLayout
    private var selectedImageBase64: String? = null // 선택된 이미지의 Base64 인코딩된 문자열을 저장할 변수
    private var originalContainerY: Float = 0f // 컨테이너의 원래 Y 좌표, 키보드 여닫음에 따라 버튼 위치를 바꾸기 위해 사용
    private val geocodingRepository = GeocodingRepository(RetrofitService.geocodingService) //지오코딩을 위한 선언

    private var userAddress: String? = null
    private var userEmail: String? = null
    private var imageBase64: String? = null
    private var imageExtraBase64: String? = null
    private var date:Date? = null

    // 위치 데이터를 설정하는 메서드
    fun setLocation(location: LatLng) {
        userLocation = location
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val address = geocodingRepository.getFormattedAddress(location)
                userAddress = address
                address?.let {
                    view?.findViewById<TextView>(R.id.textViewAddress)?.text = it
                } ?: run {
                    Toast.makeText(context, "주소를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 프래그먼트의 뷰가 생성될 때 호출됩니다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 바텀 시트의 레이아웃을 인플레이트합니다.
        val view = inflater.inflate(R.layout.fragment_input_bottom_sheet, container, false)

        editTextUserInput = view.findViewById<EditText>(R.id.editTextUserInput)

        userEmail = activity?.intent?.getStringExtra("user_email")

        val sharedPreferences = activity?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        editor?.putString("userEmail", userEmail)
        editor?.apply()

        // '제출' 버튼 리스너
        val buttonSubmit = view.findViewById<Button>(R.id.buttonSubmit)
        buttonSubmit.setOnClickListener {
            val content = editTextUserInput.text.toString()
            val email = userEmail // 사용자 이메일 설정

            // 이미지 Base64 인코딩 및 분할
            val (imageBase64, imageExtraBase64) = selectedImageBase64?.let {
                encodeBitmapToBase64(loadBitmapFromUri(Uri.parse(it)))
            } ?: Pair("", null)


            val postRequest = PostRequest(
                content,
                userLocation?.latitude ?: 0.0,
                userLocation?.longitude ?: 0.0,
                email ?: "",
                imageBase64 ?: "",
                imageExtraBase64,
                userAddress ?: "",
                date ?: Date()
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitService.apiService.createPost(postRequest) // 수정된 접근 방식
                    withContext(Dispatchers.Main) {
                        // 성공 처리:  예를 들어 토스트 메시지 표시
                        Toast.makeText(context, "Succefully uploaded", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // 오류 처리: 예를 들어 오류 메시지 표시
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // '취소' 버튼을 찾아 클릭 리스너를 설정합니다. 버튼 클릭 시 바텀 시트를 닫습니다.
        val cancelButton = view.findViewById<Button>(R.id.buttonCancel)
        cancelButton.setOnClickListener {
            dismiss() // 바텀 시트를 닫습니다.
        }

        // 현재 좌표를 TextView에 표시합니다.
        val textViewCurrentLocation = view.findViewById<TextView>(R.id.textViewCurrentLocation)
        userLocation?.let { location ->
            textViewCurrentLocation.text =
                getString(R.string.current_location_format, location.latitude, location.longitude)
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
                val fixedPeekHeight =
                    (15 * resources.displayMetrics.density).toInt() // directly using calculated value
                behavior.peekHeight = screenHeight - fixedPeekHeight
                parent.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }


    // 프래그먼트의 뷰가 생성되고 난 후 호출됩니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 버튼 컨테이너 인스턴스 초기화
        buttonContainer = view.findViewById(R.id.buttonContainer)
        originalContainerY = buttonContainer.y // 컨테이너의 원래 Y 좌표 저장

        val behavior = BottomSheetBehavior.from(view.parent as View)
        behavior.isDraggable = false  // 바텀 시트 드래그 비활성화

        // 키보드 상태 감지
        val rootView = activity?.findViewById<View>(android.R.id.content)
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            // 키보드가 활성화되었다고 간주
            if (keypadHeight > screenHeight * 0.15) {
                adjustBottomSheetPosition(keypadHeight)
            } else { // 키보드가 숨겨짐
                resetBottomSheetPosition()
            }
        }

        // '사진 추가' 버튼 클릭 이벤트 처리
        val buttonAddPhoto = view.findViewById<Button>(R.id.buttonAddPhoto)
        buttonAddPhoto.setOnClickListener {
            checkStoragePermission() // 권한 확인 및 갤러리 열기
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 권한 확인 및 갤러리를 여는 함수
    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted. Request for permission.
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        } else {
            // Permission has already been granted. Open gallery.
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted. Open gallery.
                    openGallery()
                } else {
                    // Permission denied. Handle the feature's non-availability.
                    Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 101
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            view?.findViewById<ImageView>(R.id.imageViewSelectedPhoto)?.apply {
                visibility = View.VISIBLE
                setImageURI(selectedImageUri)
                // Uri를 문자열로 저장
                selectedImageBase64 = selectedImageUri.toString()

                selectedImageUri?.let { uri ->
                    val bitmap = loadBitmapFromUri(uri)
                    val (base64, extraBase64) = encodeBitmapToBase64(bitmap)
                    imageBase64 = base64
                    imageExtraBase64 = extraBase64
                }
            }
        }
    }


    // 갤러리를 여는 함수
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImage.launch(intent)
    }

    // Uri에서 Bitmap을 로드하는 함수
    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }

    // Bitmap을 Base64 문자열로 인코딩하는 함수
    private fun encodeBitmapToBase64(bitmap: Bitmap): Pair<String, String?> {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // 이미지 데이터를 절반으로 분할
        val midpoint = base64String.length / 2
        val firstHalf = base64String.substring(0, midpoint)
        val secondHalf = base64String.substring(midpoint)

        return Pair(firstHalf, secondHalf.takeIf { it.isNotEmpty() })
    }


    private fun adjustBottomSheetPosition(keyboardHeight: Int) {
        // 바텀시트의 위치를 키보드 위로 조정
        dialog?.window?.let { window ->
            val params = window.attributes
            params.y = keyboardHeight  // 키보드의 높이만큼 Y 좌표를 이동
            window.attributes = params
        }
    }

    private fun resetBottomSheetPosition() {
        // 바텀시트의 위치를 원래대로 복원
        dialog?.window?.let { window ->
            val params = window.attributes
            params.y = 0  // Y 좌표를 원래대로 복원
            window.attributes = params
        }
    }

}

