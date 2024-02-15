package com.myself.myselfContact

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.myself.myselfContact.model.ContentEntity
import com.myself.myselfContact.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor(
private val contentRepository: ContentRepository
) : ViewModel(){

    private val _doneEvent = MutableLiveData<Unit>()
    val doneEvent : LiveData<Unit> = _doneEvent

    private val _clickEvent = MutableLiveData<Unit>()
    val clickEvent : LiveData<Unit> = _clickEvent

    var content = MutableLiveData<String?>()
    var memo = MutableLiveData<String?>()


    var photoId = MutableLiveData<Int?>()
    var phoneNumber = MutableLiveData<String>()
    var name = MutableLiveData<String>()

    //수정용
    var item : ContentEntity? = null
    fun initData(item : ContentEntity){
        this.item = item
        content.value = item.content
        memo.value = item.memo
        name.value = item.name
        photoId.value = item.photoId
        phoneNumber.value = item.phoneNumber
    }

    fun setImageData(imageData: Int){
        photoId.value = imageData
    }

    fun insertData(){
        phoneNumber.value?.let { phoneNumber ->
            name.value?.let { name ->
                viewModelScope.launch(Dispatchers.IO) {
                    contentRepository.insert(
                        item?.copy(
                            name = name,
                            phoneNumber = phoneNumber,
                            content = content.value,
                            memo = memo.value,
                            photoId = photoId.value,
                        ) ?: ContentEntity(
                            name = name,
                            phoneNumber = phoneNumber,
                            content = content.value,
                            memo = memo.value,
                            photoId = photoId.value,
                        )
                    )
                    _doneEvent.postValue(Unit)
                }
            }
        }
    }

    fun clickData(){
        _clickEvent.postValue(Unit)
    }
}