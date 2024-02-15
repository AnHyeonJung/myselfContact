package com.myself.myselfContact.repository

import com.myself.myselfContact.data.dao.ContentDao
import com.myself.myselfContact.model.ContentEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(private val contentDao: ContentDao) :
    ContentRepository {

    override fun loadList() = contentDao.selectAll()

    override fun loadItem(idNum: Int): Flow<ContentEntity> {
        return contentDao.selectOne(idNum)
    }


    override suspend fun insert(item: ContentEntity) {
        contentDao.insert(item)
    }

    override suspend fun modify(item: ContentEntity) {
        contentDao.insert(item)
    }

    override suspend fun delete(item: ContentEntity) {
        contentDao.delete(item)
    }
}