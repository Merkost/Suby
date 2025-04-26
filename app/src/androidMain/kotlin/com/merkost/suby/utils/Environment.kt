package com.merkost.suby.utils

import com.merkost.suby.BuildConfig

object Environment {
    const val DEBUG = BuildConfig.BUILD_TYPE == "debug"
}