package com.example.innovehair.view.customerService.createCustomerService.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.innovehair.R
import com.example.innovehair.databinding.ItemCustomerServiceCapillaryProthesisBinding
import com.example.innovehair.service.models.CapillaryProsthesis
import kotlinx.android.synthetic.main.item_customer_service_capillary_prothesis.view.*
import java.text.DecimalFormat

class CustomerServiceCapillaryProthesisAdapter(
    private val list: MutableList<CapillaryProsthesis>,
    private val initialSelectedItemId: String?,
    private val itemClickListener: (CapillaryProsthesis) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var selectedItemPosition = -1

    init {
        if (initialSelectedItemId != null) {
            for ((index, item) in list.withIndex()) {
                if (item.id == initialSelectedItemId) {
                    selectedItemPosition = index
                    break
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CustomerServiceCapillaryProthesisViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_customer_service_capillary_prothesis, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = list[position]
        val isSelected = position == selectedItemPosition
        (holder as CustomerServiceCapillaryProthesisViewHolder).bind(itemData)

        // Adiciona um evento de clique para marcar/desmarcar o item
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(itemData)
            val clickedPosition = holder.adapterPosition // Obtém a posição do item clicado

            if (selectedItemPosition != clickedPosition) {
                // Apenas atualiza visualmente se o item clicado não for o mesmo já selecionado
                selectedItemPosition = clickedPosition
                notifyDataSetChanged() // Notifica o adaptador sobre a mudança no conjunto de dados
            } // Notifica o adaptador sobre a mudança no conjunto de dados
        }

        holder.itemView.prothesisCV.setBackgroundColor(
            Color.parseColor(if (isSelected) "#E9AA5A" else "#0A3D58")
        )

        holder.itemView.prothesisName.setTextColor(
            Color.parseColor(if (isSelected) "#081A2C" else "#FFFFFF")
        )

        holder.itemView.price.setTextColor(
            Color.parseColor(if (isSelected) "#081A2C" else "#E9AA5A")
        )

        holder.itemView.checkbox.isChecked = isSelected
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}

class CustomerServiceCapillaryProthesisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemCustomerServiceCapillaryProthesisBinding.bind(itemView)

    fun bind(model: CapillaryProsthesis) {
        binding.prothesisName.text = model.name
        binding.price.text = "${currencyFormat(model.price ?: 0.0)} €"
    }

    private fun currencyFormat(amount: Double): String {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(amount)
    }
}