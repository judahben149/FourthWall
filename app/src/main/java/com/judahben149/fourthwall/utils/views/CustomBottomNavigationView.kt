package com.judahben149.fourthwall.utils.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomBottomNavigationView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BottomNavigationView(context, attrs, defStyleAttr) {

    private var selectedIconScale = 1.2f
    private var unselectedIconScale = 1.0f

    init {
        setOnItemSelectedListener { item ->
            updateIconSize(item.itemId)
            true
        }
    }

    private fun updateIconSize(selectedItemId: Int) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val itemView = findViewById<BottomNavigationItemView>(item.itemId)
            val iconView = itemView.findViewById<ImageView>(com.google.android.material.R.id.icon)
            
            if (item.itemId == selectedItemId) {
                iconView.scaleX = selectedIconScale
                iconView.scaleY = selectedIconScale
            } else {
                iconView.scaleX = unselectedIconScale
                iconView.scaleY = unselectedIconScale
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        updateIconSize(selectedItemId)
    }
}