package com.example.innovehair.view.customerService.serviceHistoryList.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.innovehair.R
import com.example.innovehair.databinding.ItemServiceHistoryBinding
import com.example.innovehair.service.models.respondeModels.CustomerServiceResponse
import com.example.innovehair.view.customerService.createCustomerService.CustomerServiceType
import com.example.innovehair.view.customerService.serviceHistoryList.CustomerServiceData
import java.text.SimpleDateFormat
import java.util.*

class ServiceHistoryListAdapter(
    private val list: MutableList<CustomerServiceData>,
    private val itemCLickListener: (CustomerServiceData) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ServiceHistoryListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_service_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemData = this.list[position]
        (holder as ServiceHistoryListViewHolder).bind(itemData)

        holder.itemView.setOnClickListener {
            itemCLickListener.invoke(itemData)
        }
    }

    override fun getItemCount(): Int {
        return this.list.size
    }
}

class ServiceHistoryListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemServiceHistoryBinding.bind(itemView)

    fun bind(model: CustomerServiceData) {
        binding.clientName.text = model.client?.name ?: "---"
        binding.prothesisType.text = model.prothesisType?.name
        if (model.customerServiceResponse?.customerServiceType == CustomerServiceType.SCHEDULED_APPLICATION.name) {
            binding.date.text = formatDate(model.customerServiceResponse.scheduleApplicationDate)
            binding.customerServiceType.text = "Aplicação"
            binding.customerServiceType.setTextColor(Color.parseColor("#CECE5A"))
        } else {
            binding.date.text = formatDate(model.customerServiceResponse?.scheduleServiceDate)
            binding.customerServiceType.text = "Atendimento"
            binding.customerServiceType.setTextColor(Color.parseColor("#E9AA5A"))
        }
    }

    private fun formatDate(date: Date?) : String {
        if (date != null) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return dateFormat.format(date)
        }
        return "---"
    }
}