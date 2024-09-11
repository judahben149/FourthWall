package com.judahben149.fourthwall.utils.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.judahben149.fourthwall.R
import android.animation.ArgbEvaluator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.graphics.drawable.GradientDrawable
import com.tapadoo.alerter.Alerter
import com.tapadoo.alerter.OnHideAlertListener

fun ImageView.animateBounce() {
    this.animate()
        .translationY(-50f)
        .setDuration(5000)
        .setInterpolator(AccelerateDecelerateInterpolator())
        .start()
}

fun ImageView.setup3DBounceAnimation() {
    // Vertical translation animation
    val translationY = ObjectAnimator.ofFloat(this, "translationY", 0f, -100f).apply {
        duration = 1500
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.REVERSE
    }

    // Scale animation for 3D-like effect
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.2f).apply {
        duration = 1500
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.REVERSE
    }
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.2f).apply {
        duration = 1500
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.REVERSE
    }

    // Slight rotation for added dynamism
    val rotation = ObjectAnimator.ofFloat(this, "rotation", -5f, 5f).apply {
        duration = 3000  // Slower rotation for subtlety
        repeatCount = ObjectAnimator.INFINITE
        repeatMode = ObjectAnimator.REVERSE
    }

    // Combine all animations
    AnimatorSet().apply {
        playTogether(translationY, scaleX, scaleY, rotation)
        interpolator = AccelerateDecelerateInterpolator()
        start()
    }
}

fun ImageView.setupCryptoPhoneAnimation() {
    val floatAnimation = ObjectAnimator.ofFloat(this, "translationY", 0f, -30f).apply {
        duration = 2000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
    }

    val rotateAnimation = ObjectAnimator.ofFloat(this, "rotationY", 0f, 360f).apply {
        duration = 6000
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
    }

    AnimatorSet().apply {
        playTogether(floatAnimation, rotateAnimation)
        start()
    }
}

fun ImageView.setupDeFiNetworkAnimation() {
    val pulseAnimation = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.1f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
    }

    val rotateAnimation = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f).apply {
        duration = 4000
        repeatCount = ValueAnimator.INFINITE
        interpolator = AccelerateDecelerateInterpolator()
    }

    AnimatorSet().apply {
        playTogether(pulseAnimation, rotateAnimation)
        start()
    }
}

fun View.dpToPx(dp: Float): Int {
    return (dp * resources.displayMetrics.density).toInt()
}


fun View.animateBorderColorForError(durationMs: Long = 2000) {
    val drawable = background as? GradientDrawable ?: return
    val context = context ?: return

    val originalColor = ContextCompat.getColor(context, R.color.tvStrokeNotInFocus)
    val errorColor = ContextCompat.getColor(context, R.color.bright_red_error)

    val originalStrokeWidth = dpToPx(1.5F)
    val errorStrokeWidth = dpToPx(2.0F)

    val colorHolder = PropertyValuesHolder.ofObject("color", ArgbEvaluator(), originalColor, errorColor)
    val strokeHolder = PropertyValuesHolder.ofInt("stroke", originalStrokeWidth, errorStrokeWidth)

    val toErrorAnimator = ValueAnimator().apply {
        setValues(colorHolder, strokeHolder)
        duration = durationMs / 4
        addUpdateListener { animator ->
            val color = animator.getAnimatedValue("color") as Int
            val stroke = animator.getAnimatedValue("stroke") as Int
            drawable.setStroke(stroke, color)
            invalidate()
        }
    }

    val holdErrorAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = durationMs / 2
    }

    val toOriginalAnimator = ValueAnimator().apply {
        setValues(
            PropertyValuesHolder.ofObject("color", ArgbEvaluator(), errorColor, originalColor),
            PropertyValuesHolder.ofInt("stroke", errorStrokeWidth, originalStrokeWidth)
        )
        duration = durationMs / 4
        addUpdateListener { animator ->
            val color = animator.getAnimatedValue("color") as Int
            val stroke = animator.getAnimatedValue("stroke") as Int
            drawable.setStroke(stroke, color)
            invalidate()
        }
    }

    toErrorAnimator.start()
    toErrorAnimator.addListener(onEnd = {
        holdErrorAnimator.start()
    })
    holdErrorAnimator.addListener(onEnd = {
        toOriginalAnimator.start()
    })
}

fun MaterialButton.disable(res: Resources, pgBar: CircularProgressIndicator? = null) {
    this.setBackgroundColor(res.getColor(R.color.disabledPrimaryBtnBackgroundTint))

    // Disable clicking
    this.isClickable = false
    this.isEnabled = false
    this.alpha = 1F
    this.textScaleX = 1F

    pgBar?.let {
        pgBar.visibility = View.INVISIBLE
    }
}

fun MaterialButton.enable(res: Resources, pgBar: CircularProgressIndicator? = null) {
    this.setBackgroundColor(res.getColor(R.color.primaryBtnBackgroundTint))

    // Disable clicking
    this.isClickable = true
    this.isEnabled = true

    this.textScaleX = 1F
    this.alpha = 1F

    pgBar?.let {
        pgBar.visibility = View.INVISIBLE
    }
}

fun MaterialButton.isLoading(res: Resources, pgBar: CircularProgressIndicator) {
    this.setBackgroundColor(res.getColor(R.color.primaryBtnBackgroundTint))
    this.alpha = 0.6F

    // Disable clicking
    this.isClickable = false
    this.isEnabled = true

    this.textScaleX = 0F
    pgBar.visibility = View.VISIBLE
}

fun TextView.setAmountFont(context: Context) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
    val customFont = ResourcesCompat.getFont(context, R.font.cera_pro_medium)

    this.setTypeface(customFont, Typeface.BOLD)
    this.setTextColor(ContextCompat.getColor(context, R.color.textOnBackground))
}

fun TextView.setErrorFont(context: Context) {
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
    this.setTypeface(null, Typeface.NORMAL)
    this.setTextColor(ContextCompat.getColor(context, R.color.red_error))
}

fun showSnack(message: String, rootView: View, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(rootView, message, duration).show()
}

fun Activity.showInfoAlerter(message: String, durationInSecs: Long = 3) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInSecs * 1000)
        .setBackgroundColorRes(R.color.light_purple_tint)
        .setTextAppearance(R.style.AlerterInfoTextAppearance)
        .setIcon(R.drawable.ic_dollar)
        .setIconColorFilter(0)
        .setIconSize(androidx.appcompat.R.dimen.abc_star_big)
        .enableSwipeToDismiss()
        .show()
}

fun Activity.showSuccessAlerter(
    message: String,
    durationInSecs: Long = 3,
    onHideCallBack: () -> Unit
) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInSecs * 1000)
        .setBackgroundColorRes(R.color.green_success_bg)
        .setTextAppearance(R.style.AlerterInfoTextAppearance)
        .setIcon(R.drawable.ic_dollar)
        .setIconColorFilter(0)
        .setIconSize(androidx.appcompat.R.dimen.abc_star_big)
        .enableSwipeToDismiss()
        .setOnHideListener(OnHideAlertListener {
            onHideCallBack()
        })
        .show()
}

fun Activity.showErrorAlerter(
    message: String,
    durationInSecs: Long = 3,
    onHideCallBack: () -> Unit
) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInSecs * 1000)
        .setBackgroundColorRes(R.color.red_error)
        .setTextAppearance(R.style.AlerterInfoTextAppearance)
        .setIcon(R.drawable.ic_dollar)
        .setIconColorFilter(0)
        .setIconSize(androidx.appcompat.R.dimen.abc_star_big)
        .enableSwipeToDismiss()
        .setOnHideListener(OnHideAlertListener {
            onHideCallBack()
        })
        .show()
}
