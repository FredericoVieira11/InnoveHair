package com.example.innovehair.view.customerService.serviceHistoryList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.innovehair.databinding.ItemDropdownBinding
import com.example.innovehair.service.models.respondeModels.UserResponse

class CollaboratorAdapter(
    mContext: Context,
    collaborators: List<UserResponse>
) : ArrayAdapter<UserResponse>(mContext, android.R.layout.simple_dropdown_item_1line) {

    private val uniqueCollaborators = mutableSetOf<UserResponse>()

    init {
        removeDuplicates(collaborators)
        addAll(uniqueCollaborators.toList())
    }

    private fun removeDuplicates(collaborators: List<UserResponse>) {
        for (collaborator in collaborators) {
            uniqueCollaborators.add(collaborator)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemDropdownBinding = if (convertView == null) {
            ItemDropdownBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemDropdownBinding.bind(convertView)
        }

        val collaborator = getItem(position)
        binding.customerServiceType.text = collaborator?.name ?: ""

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
                return (resultValue as? UserResponse)?.name ?: ""
            }
        }
    }
}