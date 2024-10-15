package com.judahben149.fourthwall.utils.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.chip.Chip
import androidx.core.content.ContextCompat
import com.judahben149.fourthwall.R

class CustomIconChip @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = com.google.android.material.R.attr.chipStyle
) : Chip(context, attrs, defStyleAttr) {

    private var iconDrawable: Drawable? = null
    private var iconSize: Int = 0
    private val defaultPadding: Int = context.resources.getDimensionPixelSize(R.dimen.chip_default_padding)

    init {
        chipStartPadding = defaultPadding.toFloat()
        textStartPadding = defaultPadding.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        iconSize = (h * 0.6f).toInt() // Icon size is 60% of the chip height
        adjustPadding()
    }

    override fun onDraw(canvas: Canvas) {
        // Draw the icon before calling super.onDraw()
        iconDrawable?.let { drawable ->
            val left = chipStartPadding.toInt()
            val top = (height - iconSize) / 2
            drawable.setBounds(left, top, left + iconSize, top + iconSize)
            drawable.draw(canvas)
        }
        super.onDraw(canvas)
    }

    fun setIconResource(resourceId: Int) {
        iconDrawable = ContextCompat.getDrawable(context, resourceId)
        adjustPadding()
        invalidate()
    }

    private fun adjustPadding() {
        val iconPadding = if (iconDrawable != null) iconSize + defaultPadding else 0
        chipStartPadding = defaultPadding.toFloat()
        textStartPadding = (defaultPadding + iconPadding - 12).toFloat()
        invalidate()
    }
}