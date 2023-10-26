package com.gws.ui_core.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.gws.local_models.models.Ussd
import com.gws.ussd.ui_core.R
import com.gws.ussd.ui_core.databinding.SampleUssdViewBinding

class UssdView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val binding = SampleUssdViewBinding.inflate(LayoutInflater.from(context), this)

    fun bind(data: MovieViewData) {

        val (ussd) = data


        if(ussd.etat=="1"){
            binding.addQtyBtn.setImageResource(R.drawable.ic_circle_success)
        }else{
            binding.addQtyBtn.setImageResource(R.drawable.ic_circle_pending)
        }

        binding.movieTitle.text = ussd.num +" "+ussd.ussd
        binding.ussdDate.text = ussd.date.toString()

    }

    data class MovieViewData(val ussd: Ussd)
}
