package com.judahben149.fourthwall.utils

import timber.log.Timber

fun Any.log(prepend: String = "", tag: String = "FourthWallLogger") {
    Timber.tag(tag).d(prepend.plus(this.toString()))
}