package com.example.innovehair.view.customerService.serviceHistoryList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.innovehair.R
import com.example.innovehair.databinding.ItemDropdownBinding
import com.example.innovehair.service.models.respondeModels.ClientResponse

class ClientAdapter(
    mContext: Context,
    clients: List<ClientResponse>
) : ArrayAdapter<ClientResponse>(mContext, android.R.layout.simple_dropdown_item_1line) {

    private val uniqueClients = mutableSetOf<ClientResponse>()

    init {
        removeDuplicates(clients)
        addAll(uniqueClients.toList())
    }

    private fun removeDuplicates(clients: List<ClientResponse>) {
        for (client in clients) {
            uniqueClients.add(client)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemDropdownBinding = if (convertView == null) {
            ItemDropdownBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemDropdownBinding.bind(convertView)
        }

        val client = getItem(position)
        binding.customerServiceType.text = client?.name ?: ""

        return binding.root
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                TODO("Not yet implemented")
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                TODO("Not yet implemented")
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return (resultValue as? ClientResponse)?.name ?: ""
            }
        }
    }
}