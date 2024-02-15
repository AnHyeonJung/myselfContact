package com.myself.myselfContact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.myself.myselfContact.model.ContentEntity
import com.myself.myselfContact.repository.ContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val contentRepository: ContentRepository
) : ViewModel(){

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
        phoneNumber.value = item.phoneNumber
        photoId.value = item.photoId
    }

    val contentList= contentRepository.loadList()
        .stateIn(
            initialValue = emptyList(),
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope
        )

    val contentOne= item?.let {
        contentRepository.loadItem(it.id)
        .stateIn(
            initialValue = item,
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope
        )
    }

//    fun selectItem(item:ContentEntity){
//        viewModelScope.launch(Dispatchers.IO) {
//            contentRepository.loadItem(item.id)
//        }
//    }

    fun updateItem(item: ContentEntity){
        viewModelScope.launch(Dispatchers.IO) {
            contentRepository.modify(item)
        }
    }

    fun deleteItem(item: ContentEntity){
        viewModelScope.launch(Dispatchers.IO) {
            contentRepository.delete(item)
        }
    }
}