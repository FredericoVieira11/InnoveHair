package com.example.innovehair.view.customerService.serviceHistoryList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.innovehair.databinding.ItemDropdownBinding
import com.example.innovehair.service.models.CapillaryProsthesis

class CapillaryProthesisAdapter(
    mContext: Context,
    prostheses: List<CapillaryProsthesis>
) : ArrayAdapter<CapillaryProsthesis>(mContext, android.R.layout.simple_dropdown_item_1line) {

    private val uniqueProthesis = mutableSetOf<CapillaryProsthesis>()

    init {
        removeDuplicates(prostheses)
        addAll(uniqueProthesis.toList())
    }

    private fun removeDuplicates(prostheses: List<CapillaryProsthesis>) {
        for (prothesis in prostheses) {
            uniqueProthesis.add(prothesis)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemDropdownBinding = if (convertView == null) {
            ItemDropdownBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemDropdownBinding.bind(convertView)
        }

        val prothesis = getItem(position)
        binding.customerServiceType.text = prothesis?.name ?: ""

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
                return (resultValue as? CapillaryProsthesis)?.name ?: ""
            }
        }
    }
}
