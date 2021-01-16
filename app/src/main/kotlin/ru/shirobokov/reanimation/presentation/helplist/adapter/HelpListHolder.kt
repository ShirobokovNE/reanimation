package ru.shirobokov.reanimation.presentation.helplist.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.shirobokov.reanimation.databinding.LiHelpBinding

class HelpListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding: LiHelpBinding by viewBinding()

    fun bind(data: String) {
        binding.helpText.text = data
    }
}