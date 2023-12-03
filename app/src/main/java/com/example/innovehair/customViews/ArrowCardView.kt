package com.example.innovehair.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.example.innovehair.databinding.CustomArrowCardviewBinding

class ArrowCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val binding: CustomArrowCardviewBinding =
        CustomArrowCardviewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setIcon(iconResId: Int) {
        binding.arrowIcon.setImageResource(iconResId)
    }
}