package com.example.innovehair.view.capillaryProsthesis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.innovehair.R
import com.example.innovehair.databinding.ItemCapillaryProthesisBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import kotlinx.android.synthetic.main.item_capillary_prothesis.view.*
import java.text.DecimalFormat

class CapillaryProsthesisAdapter(
    private val list: MutableList<CapillaryProsthesis>,
    private val itemClickListener: CapillaryProsthesisOnClickListener
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CapillaryProsthesisViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_capillary_prothesis, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = this.list[position]
        (holder as CapillaryProsthesisViewHolder).bind(itemData)

        holder.itemView.btnEdit.setOnClickListener {
            itemClickListener.onEditClick(itemData)
        }

        holder.itemView.btnDelete.setOnClickListener {
            itemClickListener.onRemoveClick(itemData)
        }
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}

class CapillaryProsthesisViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val binding = ItemCapillaryProthesisBinding.bind(itemView)

    fun bind(model: CapillaryProsthesis) {
        binding.prothesisTxt.text = model.name
        binding.priceTxt.text = "${currencyFormat(model.price ?: 0.0)} €"
    }

    private fun currencyFormat(amount: Double): String {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(amount)
    }
}

interface CapillaryProsthesisOnClickListener {
    fun onEditClick(item: CapillaryProsthesis)
    fun onRemoveClick(item: CapillaryProsthesis)
}