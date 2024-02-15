package com.myself.myselfContact.repository

import com.myself.myselfContact.model.ContentEntity
import kotlinx.coroutines.flow.Flow

interface ContentRepository {

    fun loadList() : Flow<List<ContentEntity>>
    fun loadItem(idNum : Int) : Flow<ContentEntity>

    suspend fun insert(item : ContentEntity)
    suspend fun modify(item : ContentEntity)
    suspend fun delete(item : ContentEntity)
}