package ru.shirobokov.reanimation.presentation.helplist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.shirobokov.reanimation.R

class HelpListAdapter : RecyclerView.Adapter<HelpListHolder>() {

    var data: List<String> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HelpListHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.li_help, parent, false)
        )

    override fun onBindViewHolder(holder: HelpListHolder, position: Int) = holder.bind(data[position])
    override fun getItemCount() = data.size
}