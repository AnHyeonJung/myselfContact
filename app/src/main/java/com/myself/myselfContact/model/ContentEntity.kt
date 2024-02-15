package com.myself.myselfContact.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Content")
data class ContentEntity(
    @PrimaryKey(true)
    val id: Int = 0,

    @ColumnInfo
    var photoId: Int? = null,

    @ColumnInfo
    var content: String? = null,

    @ColumnInfo
    var phoneNumber: String,

    @ColumnInfo
    var name: String,

    @ColumnInfo
    var memo: String? = null,

    @ColumnInfo
    var isDone: Boolean = false,

    ) : Serializable
