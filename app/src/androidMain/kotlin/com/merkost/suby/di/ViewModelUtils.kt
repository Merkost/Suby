package com.merkost.suby.di

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
inline fun <reified VM : ViewModel> koinActivityViewModel(): VM {
    return koinViewModel<VM>(
        viewModelStoreOwner = LocalActivity.current as ComponentActivity
    )
}
