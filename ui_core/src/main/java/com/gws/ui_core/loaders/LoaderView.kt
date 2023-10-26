package com.gws.ui_core.loaders

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.gws.ussd.ui_core.R
import com.gws.ussd.ui_core.databinding.SampleLoaderViewBinding


class LoaderView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val binding: SampleLoaderViewBinding =
        SampleLoaderViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        gravity = Gravity.CENTER_HORIZONTAL
    }

    fun setIndeterminate(isIndeterminate: Boolean) {
        binding.loader.isIndeterminate = isIndeterminate
    }
}
