package com.merkost.suby.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.merkost.suby.MainActivity

@Composable
inline fun <reified VM : ViewModel> hiltActivityViewModel(): VM {
    val activity = LocalContext.current as MainActivity
    return hiltViewModel<VM>(activity)
}
