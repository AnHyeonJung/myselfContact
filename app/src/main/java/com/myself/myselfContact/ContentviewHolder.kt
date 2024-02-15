package com.myself.myselfContact

import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.myself.myselfContact.databinding.ItemContentBinding
import com.myself.myselfContact.model.ContentEntity

class ContentViewHolder(
    private val binding: ItemContentBinding,  //ViewDataBinding
    private val handler : MainActivity.Handler
):RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ContentEntity){
        binding.item = item
        binding.handler = handler
        binding.favoriteCheckBox.paintFlags = if(item.isDone){
            Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            0
        }
    }
}