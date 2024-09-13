package com.judahben149.fourthwall.utils.views

import android.animation.Animator
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
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
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

fun ImageView.animateCheckmark() {
    val rotationAnimator = ValueAnimator.ofFloat(-3f, 3f).apply {
        duration = 100
        repeatCount = 3
        repeatMode = ValueAnimator.REVERSE
        interpolator = AccelerateDecelerateInterpolator()
        addUpdateListener { animator ->
            rotation = animator.animatedValue as Float
        }
    }

    val delayBetweenAnimations = 1500L

    val runAnimation = object : Runnable {
        override fun run() {
            rotationAnimator.start()
            postDelayed(this, rotationAnimator.duration * (rotationAnimator.repeatCount + 1) + delayBetweenAnimations)
        }
    }

    post(runAnimation)
}

fun ImageView.startWarmupAnimation() {
    val bounce = ObjectAnimator.ofFloat(this, "translationY", 0f, -20f, 0f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
        interpolator = AccelerateDecelerateInterpolator()
    }

    val shake = ObjectAnimator.ofFloat(this, "rotation", -2f, 2f).apply {
        duration = 100
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.REVERSE
    }

    AnimatorSet().apply {
        playTogether(bounce, shake)
        start()
    }
}

fun ImageView.startTakeoffAnimation(onAnimationEnd: () -> Unit) {
    val screenHeight = resources.displayMetrics.heightPixels.toFloat()

    val shake = PropertyValuesHolder.ofFloat(View.ROTATION, -5f, 5f)
    val moveUp = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -screenHeight)
    val shrink = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.5f)
    val shrinkY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.5f)

    ObjectAnimator.ofPropertyValuesHolder(this, shake, moveUp, shrink, shrinkY).apply {
        duration = 2000
        interpolator = AccelerateInterpolator()
        addListener(onEnd = {
            onAnimationEnd()
        })
        start()
    }
}

fun ImageView.startIrregularHeartbeatAnimation() {
    val scaleX = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.2f, 1f)
    val scaleY = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.2f, 1f)

    val firstBeat = AnimatorSet().apply {
        playTogether(scaleX, scaleY)
        duration = 200
        interpolator = OvershootInterpolator()
    }

    val secondBeat = AnimatorSet().apply {
        playTogether(
            ObjectAnimator.ofFloat(this@startIrregularHeartbeatAnimation, "scaleX", 1f, 1.1f, 1f),
            ObjectAnimator.ofFloat(this@startIrregularHeartbeatAnimation, "scaleY", 1f, 1.1f, 1f)
        )
        duration = 200
        interpolator = AccelerateDecelerateInterpolator()
    }

    val pause = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1000
    }

    AnimatorSet().apply {
        playSequentially(firstBeat, secondBeat, pause)
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                start()
            }
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        start()
    }
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
    this.alpha = 1F
    this.textScaleX = 1F

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

fun Activity.showInfoAlerter(message: String, durationInMillis: Long = 3000) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInMillis)
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
    durationInMillis: Long = 3000,
    onHideCallBack: () -> Unit
) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInMillis)
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
    durationInMillis: Long = 3000,
    onHideCallBack: () -> Unit
) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInMillis)
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

fun Activity.showWarningAlerter(
    message: String,
    durationInMillis: Long = 3000,
    onHideCallBack: () -> Unit
) {
    Alerter.create(this)
        .setText(message)
        .setDuration(durationInMillis)
        .setBackgroundColorRes(R.color.orange_warning)
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
