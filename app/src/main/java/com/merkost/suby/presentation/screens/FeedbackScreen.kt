package com.merkost.suby.presentation.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.merkost.suby.R
import com.merkost.suby.model.entity.FeedbackAction
import com.merkost.suby.presentation.base.SubyTextField
import com.merkost.suby.presentation.base.SubyTopAppBar
import com.merkost.suby.presentation.viewModel.FeedbackViewModel
import com.merkost.suby.utils.BaseViewState
import com.merkost.suby.utils.analytics.ScreenLog
import com.merkost.suby.utils.analytics.Screens
import kotlinx.coroutines.android.awaitFrame
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(upPress: () -> Unit, feedbackAction: FeedbackAction, text: String) {
    ScreenLog(Screens.Feedback)

    val context = LocalContext.current
    val viewModel = koinViewModel<FeedbackViewModel>()
    val feedbackState by viewModel.feedbackState.collectAsState()
    var userAnswer by remember { mutableStateOf(TextFieldValue(text, TextRange(text.length))) }
    val insets = remember { 32.dp }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = feedbackState) {
        when (feedbackState) {
            is BaseViewState.Success -> {
                Toast.makeText(
                    context,
                    context.getText(feedbackAction.successMessageRes),
                    Toast.LENGTH_SHORT
                ).show()
                upPress()
            }

            is BaseViewState.Error -> {
                Toast.makeText(
                    context,
                    context.getText(feedbackAction.failureMessageRes),
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(insets, insets, insets, insets),
        topBar = {
            SubyTopAppBar(title = {
                Text(text = stringResource(R.string.support))
            }, upPress = upPress)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Outlined.Feedback,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Text(
                    text = "Request New Service",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "It looks like the service you searched for isn't available. Please provide the service name below and we will add it.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                SubyTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    keyboardOptions = KeyboardOptions(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    placeholder = {
                        Text(text = stringResource(id = feedbackAction.questionRes))
                    }
                )

                Button(
                    enabled = feedbackState !is BaseViewState.Loading,
                    onClick = {
                        viewModel.submitServiceRequest(userAnswer.text)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    AnimatedContent(feedbackState) {
                        if (it is BaseViewState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(text = "Send Request")
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(focusRequester) {
        awaitFrame()
//        focusRequester.requestFocus()
    }
}
