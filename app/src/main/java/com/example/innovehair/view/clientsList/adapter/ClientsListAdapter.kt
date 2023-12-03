package com.example.innovehair.view.clientsList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.innovehair.R
import com.example.innovehair.databinding.ItemClientBinding
import com.example.innovehair.service.models.respondeModels.ClientResponse

class ClientsListAdapter(
    private val list: MutableList<ClientResponse>,
    private val itemCLickListener: (ClientResponse) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ClientsListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = this.list[position]
        (holder as ClientsListViewHolder).bind(itemData)

        holder.itemView.setOnClickListener {
            itemCLickListener.invoke(itemData)
        }
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}

class ClientsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemClientBinding.bind(itemView)

    fun bind(model: ClientResponse) {
        binding.clientName.text = model.name
        binding.clientEmail.text = model.email
    }
}