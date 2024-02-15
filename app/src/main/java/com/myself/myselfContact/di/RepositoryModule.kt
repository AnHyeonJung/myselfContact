package com.myself.myselfContact.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import com.myself.myselfContact.data.dao.ContentDao
import com.myself.myselfContact.repository.ContentRepository
import com.myself.myselfContact.repository.ContentRepositoryImpl

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    @ViewModelScoped
    fun providesContentRepository(contentDao: ContentDao) : ContentRepository =
        ContentRepositoryImpl(contentDao)
}