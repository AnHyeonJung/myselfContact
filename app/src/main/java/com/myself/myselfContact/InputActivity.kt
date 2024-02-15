package com.myself.myselfContact

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.myself.myselfContact.databinding.ActivityInputBinding
import com.myself.myselfContact.model.ContentEntity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputBinding

    //inputViewModel 추가
    private val viewModel : InputViewModel by viewModels()

    // Pre contract
    private var preContractStartActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                val cursor = contentResolver.query(
                    it.data!!.data!!, arrayOf<String>(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                    ),
                    null,
                    null,
                    null
                )

                if(cursor!!.moveToFirst()){
                    binding.nameEdit.setText(cursor.getString(0))
                    binding.phoneNumberEdit.setText(cursor.getString(1))
//                    binding.profileImage.setImageDrawable(cursor.getString(2))
                    val bitmap: Bitmap? = queryContactImage(cursor.getString(2).toInt())
                    binding.profileImage.setImageBitmap(bitmap)

                    //임시방편으로 이렇게 세팅.... 이미지는 양방향 데이터 통신이 안되는거같은데 어떻게 해야할지 모르겠음
                    viewModel.setImageData(cursor.getString(2).toInt())
                }
            }
        }

//    @BindingAdapter("imageFromUrl")
//    fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
//        if (!imageUrl.isNullOrEmpty()) {
//            Glide.with(view.context)
//                .load(imageUrl)
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into(view)
//        }
//    }

    private fun queryContactImage(imageDataRow: Int): Bitmap? {
        val c = contentResolver.query(
            ContactsContract.Data.CONTENT_URI, arrayOf(
                ContactsContract.CommonDataKinds.Photo.PHOTO
            ), ContactsContract.Data._ID + "=?", arrayOf(
                Integer.toString(imageDataRow)
            ), null
        )
        var imageBytes: ByteArray? = null
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0)
            }
            c.close()
        }
        return if (imageBytes != null) {
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } else {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputBinding.inflate(layoutInflater).apply {
            setContentView(root)
            lifecycleOwner = this@InputActivity //lifecycle을 쓰니까 lifecycleOwner 이라고하는데 맞아?
            viewModel = this@InputActivity.viewModel
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //수정사항일경우
        (intent.getSerializableExtra(ITEM) as? ContentEntity)?.let {
            viewModel.initData(it)
            if(it.photoId == null){
                binding.profileImage.setImageResource(R.drawable.splash)
            }else{
                val bitmap: Bitmap? = queryContactImage(it.photoId!!)
                binding.profileImage.setImageBitmap(bitmap)
            }
        }

        viewModel.doneEvent.observe(this){
            Toast.makeText(this,"완료",Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.clickEvent.observe(this){
            //전화번호 가져오기
            Log.d("TEST","????")
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            preContractStartActivityResult.launch(intent)
        }

        //권한 확인
        val status = ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")
        if(status == PackageManager.PERMISSION_GRANTED){
            Log.d("TEST","permission granted")
        }else{
            //퍼미션 요청 다이얼 로그 표시
            ActivityCompat.requestPermissions(this, arrayOf<String>("android.permission.READ_CONTACTS"),100)
            Log.d("TEST","permission granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Log.d("TEST","permission granted")
        }else{
            Log.d("TEST","permission denied")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    //이미 채워져 있는 item에 대하여 체크하고 수정 사항일 경우 Extra로 받아오기 위해
    companion object{
        private const val ITEM = "item"

        fun start(context: Context, item: ContentEntity?= null){
            Intent(context, InputActivity::class.java).apply{
                putExtra(ITEM, item)
            }.run {
                context.startActivity(this)
            }
        }
    }
}