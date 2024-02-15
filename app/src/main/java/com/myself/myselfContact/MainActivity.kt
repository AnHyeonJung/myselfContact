package com.myself.myselfContact

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.myself.myselfContact.databinding.ActivityMainBinding
import com.myself.myselfContact.model.ContentEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel : MainViewModel by viewModels()

    private val adapter by lazy { ListAdapter(Handler()) }

//    override fun onRestart() {
//        super.onRestart()
//        Log.d("TEST","onRestart")
//    }

    override fun onStart() {
        super.onStart()
        Log.d("TEST","onStart")
        binding.group1.visibility = View.GONE;
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.hide()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            view = this@MainActivity
            recyclerView.adapter = adapter
//            val decoration = DividerItemDecoration(this@MainActivity, LinearLayout.VERTICAL)
//            recyclerView.addItemDecoration(decoration)
        }

        lifecycleScope.launch{
            viewModel.contentList
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    binding.emptyTextView.isVisible = it.isEmpty()
                    binding.recyclerView.isVisible = it.isNotEmpty()
                    adapter.submitList(it)
                }
        }


        // 1. 툴바 사용 설정
        setSupportActionBar(binding.myToolbar)
        supportActionBar?.setTitle("")
        supportActionBar?.hide()  //hide 초기화
    }

    fun onClickAdd(){
        Log.d("TEST","?!!!")
        InputActivity.start(this)
    }

    fun call(){
        Log.d("TEST","call")
        val callNum = binding.phoneNumberText.text
        val tel = "tel:$callNum"
        startActivity(Intent("android.intent.action.DIAL", Uri.parse(tel)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        binding.group1.visibility = View.GONE;
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.hide()
        return true;
    }

    // 4.툴바 메뉴 버튼이 클릭 됐을 때 콜백
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // 클릭된 메뉴 아이템의 아이디 마다 when 구절로 클릭시 동작을 설정한다.
        when(item.itemId){
            R.id.action_edit ->{ // 메뉴 버튼
                //현재 item id 값 저장

                InputActivity.start(this@MainActivity, viewModel.item)
            }
            R.id.action_delete ->{ // 검색 버튼
                viewModel.item?.let { viewModel.deleteItem(it) }
                Toast.makeText(this@MainActivity,"삭제완료",Toast.LENGTH_SHORT).show()
                binding.group1.visibility = View.GONE;
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.hide()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class Handler{
        fun onClickItem(item: ContentEntity){
            binding.group1.visibility = View.VISIBLE;
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.show()

            //선택부분 세팅
            viewModel.initData(item)

            //임시 방편
            binding.nameText.text = item.name
            binding.phoneNumberText.text = item.phoneNumber
            binding.contentText.text = item.content
            binding.memoText.text = item.memo
            val bitmap: Bitmap? = item.photoId?.let { queryContactImage(it) }
            if(item.photoId == null){
                binding.profileImage.setImageResource(R.drawable.splash)
            }else{
                binding.profileImage.setImageBitmap(bitmap)
            }

            //나중에 제대로 구현하기
//            lifecycleScope.launch{
//                viewModel.contentOne
//                    ?.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                    ?.collectLatest {
//                        binding.nameText.text = it?.name
//                        binding.phoneNumberText.text = it?.phoneNumber
//                    }
//            }
        }

        fun queryContactImage(imageDataRow: Int): Bitmap? {
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


        fun onLongClickItem(item: ContentEntity):Boolean{
            viewModel.deleteItem(item)
            Toast.makeText(this@MainActivity,"삭제완료",Toast.LENGTH_SHORT).show()
            return false
        }

        fun onCheckedItem(item: ContentEntity, checked:Boolean){
            viewModel.updateItem(item.copy(isDone = checked))
        }
    }
}