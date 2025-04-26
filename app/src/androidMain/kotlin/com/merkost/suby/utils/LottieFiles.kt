package com.merkost.suby.utils

sealed class LottieFiles(val name: String) {
    data object Error : LottieFiles("error_cat")
    data object Loading : LottieFiles("loading_wave")
    data object EmptySubscriptions: LottieFiles("norecentsearches")

}