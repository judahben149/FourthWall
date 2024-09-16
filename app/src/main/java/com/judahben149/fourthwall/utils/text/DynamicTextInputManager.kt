package com.judahben149.fourthwall.utils.text

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.text.InputType
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.judahben149.fourthwall.R
import com.judahben149.fourthwall.utils.views.disable
import com.judahben149.fourthwall.utils.views.enable

class DynamicTextInputManager(private val context: Context, private val resources: Resources) {

    private val textInputLayouts = mutableListOf<Pair<String, TextInputLayout>>()
    private lateinit var submitButton: MaterialButton

    fun createTextInputs(
        container: ViewGroup,
        configs: List<TextInputConfig>,
        submitButton: MaterialButton,
        onSubmitClicked: (Map<String, String>) -> Unit
    ) {
        this.submitButton = submitButton

        configs.forEach { config ->

            val textComponent = addTextInputLayout(config)
            container.addView(textComponent.first)

            textInputLayouts.add(Pair(config.id, textComponent.first))

            setupTextChangeListener(textComponent.second)
        }

        // Setup submit button click listener
        submitButton.setOnClickListener {
            onSubmitClicked(collectInputTexts())
        }
    }

    private fun addTextInputLayout(config: TextInputConfig): Pair<TextInputLayout, TextInputEditText> {
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textInputLayout = TextInputLayout(
            context,
            null,
            com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox
        )

        textInputLayout.run {
            hint = config.hint
            this.layoutParams = layoutParams
            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            setBoxCornerRadii(dpToPx(12f), dpToPx(12f), dpToPx(12f), dpToPx(12f))
            tag = config.id // Use tag to store the string ID
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val textInputEditText = TextInputEditText(context)

        textInputEditText.inputType = config.inputType
        textInputEditText.layoutParams = params

        textInputLayout.addView(textInputEditText)
        return Pair(textInputLayout, textInputEditText)
    }


    private fun createTextInputLayout(config: TextInputConfig): TextInputLayout {
        return TextInputLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dpToPx(8)
            }

            hint = config.hint
            setBoxCornerRadii(dpToPx(12f), dpToPx(12f), dpToPx(12f), dpToPx(12f))
            tag = config.id  // Use tag to store the string ID
        }
    }

    private fun createTextInputEditText(config: TextInputConfig): TextInputEditText {
        return TextInputEditText(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            inputType = config.inputType
            typeface = ResourcesCompat.getFont(context, R.font.cera_pro_light)
            setTypeface(typeface, Typeface.BOLD)
        }
    }

    private fun setupTextChangeListener(editText: TextInputEditText) {
        editText.doAfterTextChanged {
            updateSubmitButtonState()
        }
    }

    private fun updateSubmitButtonState() {
        val allFieldsFilled = textInputLayouts.all {
            !it.second.editText?.text.isNullOrEmpty()
        }

        if (allFieldsFilled) {
            submitButton.enable(resources)
        } else {
            submitButton.disable(resources)
        }
    }

    private fun collectInputTexts(): Map<String, String> {
        return textInputLayouts.associate { (id, layout) ->
            id to (layout.editText?.text?.toString() ?: "")
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}

data class TextInputConfig(
    val id: String,
    val hint: String,
    val inputType: Int = InputType.TYPE_CLASS_TEXT
)