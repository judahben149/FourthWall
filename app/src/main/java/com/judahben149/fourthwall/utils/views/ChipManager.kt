package com.judahben149.fourthwall.utils.views

import android.content.Context
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.databinding.ItemChipBinding

class ChipManager(private val context: Context) {
    private var selectedChip: Chip? = null

    fun populateChipGroup(chipGroup: ChipGroup, items: List<Pair<Int, String>>, onChipClicked: (Int) -> Unit) {
        chipGroup.removeAllViews()

        for (item in items) {
            val chipBinding = ItemChipBinding.inflate(LayoutInflater.from(context), chipGroup, false)
            val chip = chipBinding.root

            chip.text = item.second
            chip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_background_color)
            chip.setOnClickListener {
                handleChipClick(chip, item.first, onChipClicked)
            }

            chipGroup.addView(chip)
        }
    }

    private fun handleChipClick(chip: Chip, chipIndex: Int, onChipClicked: (Int) -> Unit) {
        selectedChip?.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_background_color)
        chip.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.chip_selected_background_color)
        selectedChip = chip
        onChipClicked(chipIndex)
    }
}