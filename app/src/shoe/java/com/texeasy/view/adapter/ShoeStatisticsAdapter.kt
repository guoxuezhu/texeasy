package com.texeasy.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.texeasy.R
import com.texeasy.repository.entity.DoorInfo

/**
 * 柜内鞋子统计
 */
class ShoeStatisticsAdapter(var list: MutableList<DoorInfo>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ShoeStatisticsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_shoe_info_view, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ShoeStatisticsViewHolder) {
            holder.tvType.text = list[position].linenInfo?.linenName
            holder.tvCount.text = "${list[position].linenInfo?.linenCount} 双"
            if (list[position].linenInfo?.linenCount == 0) {
                holder.tvType.setTextColor(
                    ContextCompat.getColor(
                        holder.tvType.context,
                        R.color.gray
                    )
                )
                holder.tvCount.setTextColor(
                    ContextCompat.getColor(
                        holder.tvType.context,
                        R.color.gray
                    )
                )
            } else {
                holder.tvType.setTextColor(
                    ContextCompat.getColor(
                        holder.tvType.context,
                        R.color.white
                    )
                )
                holder.tvCount.setTextColor(
                    ContextCompat.getColor(
                        holder.tvType.context,
                        R.color.white
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal class ShoeStatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvType: TextView = itemView.findViewById(R.id.tv_type)
        var tvCount: TextView = itemView.findViewById(R.id.tv_count)
    }

    fun refreshData(newList: List<DoorInfo>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}