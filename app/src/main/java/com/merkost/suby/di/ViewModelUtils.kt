package com.merkost.suby.di

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
inline fun <reified VM : ViewModel> koinActivityViewModel(): VM {
    return koinViewModel(
        viewModelStoreOwner = LocalContext.current as ComponentActivity
    )
}
