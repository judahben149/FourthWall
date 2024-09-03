package com.judahben149.fourthwall.utils.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.res.Resources
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.judahben149.fourthwall.R

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

fun MaterialButton.disable(res: Resources, pgBar: CircularProgressIndicator? = null) {
    this.setBackgroundColor(res.getColor(R.color.disabledPrimaryBtnBackgroundTint))

    // Disable clicking
    this.isClickable = false
    this.isEnabled = false

    pgBar?.let {
        visibility = View.INVISIBLE
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

fun showSnack(message: String, rootView: View, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(rootView, message, duration).show()
}
