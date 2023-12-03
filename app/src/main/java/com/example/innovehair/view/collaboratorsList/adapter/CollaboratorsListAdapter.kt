package com.example.innovehair.view.collaboratorsList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.innovehair.R
import com.example.innovehair.databinding.ItemCollaboratorBinding
import com.example.innovehair.view.collaboratorsList.CollaboratorData

class CollaboratorsListAdapter(
    private val list: MutableList<CollaboratorData>,
    private val context: Context,
    private val itemClickListener: (CollaboratorData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CollaboratorsListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_collaborator, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = this.list[position]
        (holder as CollaboratorsListViewHolder).bind(itemData, this.context)

        holder.itemView.setOnClickListener {
            itemClickListener.invoke(itemData)
        }
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}

class CollaboratorsListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val binding = ItemCollaboratorBinding.bind(itemView)

    fun bind(collaboratorData: CollaboratorData, context: Context) {
        Glide.with(context)
            .load(collaboratorData.uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(R.drawable.ic_profile_default)
            .placeholder(R.drawable.ic_profile_default)
            .skipMemoryCache(true)
            .into(binding.userImgProfile)
        binding.userName.text = collaboratorData.user.name
        binding.userEmail.text = collaboratorData.user.email
    }
}