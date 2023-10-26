package com.gws.ussd.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gws.local_models.models.Ussd
import com.gws.ussd.ui_core.databinding.ItemUssdBinding
import com.gws.ui_core.component.UssdView

class UssdListAdapter :
    RecyclerView.Adapter<UssdListAdapter.MovieItemViewHolder>() {

    private var UssdList = arrayListOf<Ussd>()
    inner class MovieItemViewHolder(private val movieItemView: ItemUssdBinding) :
        RecyclerView.ViewHolder(movieItemView.root) {
        fun bind(data: Ussd) {
            movieItemView.root.bind(UssdView.MovieViewData(data))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieItemViewHolder {
        return MovieItemViewHolder(
            ItemUssdBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MovieItemViewHolder, position: Int) {
        holder.bind(UssdList.distinctBy { it.id }[position])
    }

    override fun getItemCount(): Int {
        return UssdList.distinctBy { it.id }.size
    }

    fun setUssdList(Ussd: List<Ussd>) {
        clearUssdList()
        UssdList.addAll(Ussd)
        notifyDataSetChanged()
    }

    fun clearUssdList() {
        this.UssdList.clear()
    }
}
