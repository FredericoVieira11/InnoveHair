package com.example.innovehair.view.search.searchForCollaborators.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.innovehair.service.models.respondeModels.ClientResponse
import com.example.innovehair.service.models.respondeModels.CollaboratorResponse

class SearchCollaboratorsAdapter(
    private val list: MutableList<CollaboratorResponse>,
    private val context: Context,
    private val itemClickListener: (CollaboratorResponse) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}