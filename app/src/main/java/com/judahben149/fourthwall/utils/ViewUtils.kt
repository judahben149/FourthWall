package com.judahben149.fourthwall.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView

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
