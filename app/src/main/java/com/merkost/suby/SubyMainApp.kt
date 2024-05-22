package com.merkost.suby

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.merkost.suby.model.Currency
import com.merkost.suby.model.FeedbackAction
import com.merkost.suby.presentation.FeedbackScreen
import com.merkost.suby.presentation.GreetingScreen
import com.merkost.suby.presentation.NewSubscriptionScreen
import com.merkost.suby.presentation.PickCurrencyScreen
import com.merkost.suby.presentation.SubscriptionInfoScreen
import com.merkost.suby.presentation.SubscriptionsScreen
import com.merkost.suby.utils.Arguments
import com.merkost.suby.utils.Destinations
import com.merkost.suby.viewModel.AppViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubyMainApp() {
    val navController = rememberNavController()

    val viewModel: AppViewModel = hiltViewModel()
    val isFirstTime by viewModel.isFirstTimeState.collectAsState()

    val startDestination = if (isFirstTime) Destinations.GREETING else Destinations.MAIN_SCREEN

    Scaffold(
        modifier = Modifier,
    ) { scaffoldPadding ->
        NavHost(
            modifier = Modifier
                .padding(scaffoldPadding)
                .consumeWindowInsets(scaffoldPadding),
            navController = navController,
            startDestination = startDestination,
        ) {
            NavGraph(
                navController = navController,
                upPress = navController::popBackStack,
            )
        }
    }
}

private fun NavGraphBuilder.NavGraph(
    modifier: Modifier = Modifier,
    upPress: () -> Unit,
    navController: NavController,
) {
    composable(Destinations.GREETING) {
        GreetingScreen(onContinueClick = {
            navController.navigate(Destinations.MAIN_SCREEN)
        })
    }

    composable(Destinations.ONBOARDING_CURRENCY) {

    }

//    composable(Destinations.ONBOARDING) {
//        // TODO:
//    }

    composable(Destinations.MAIN_SCREEN) { backStackEntry ->
        SubscriptionsScreen(onAddClicked = {
            navController.navigate(Destinations.NEW_SUBSCRIPTION)
        }, onCurrencyClick = {
            navController.navigate(Destinations.MAIN_CURRENCY_PICK)
        }, onSubscriptionInfo = { subId ->
            navController.navigate(Destinations.SubscriptionInfo(subId))
        })
    }

    composable(Destinations.NEW_SUBSCRIPTION) { backStackEntry ->
        NewSubscriptionScreen(
            pickedCurrency = backStackEntry.savedStateHandle.get<Currency>(Arguments.CURRENCY),
            onCurrencyClicked = { navController.navigate(Destinations.CURRENCY_PICK) },
            upPress = upPress,
            onServiceAbsent = { inputText ->
                navController.navigate(Destinations.Feedback(FeedbackAction.ADD_SERVICE.toString(), text = inputText))
            }
        )
    }

    composable<Destinations.SubscriptionInfo> {
        SubscriptionInfoScreen(
            subscriptionId = it.toRoute<Destinations.SubscriptionInfo>().subscriptionId,
            upPress = upPress
        )
    }

    composable(Destinations.CURRENCY_PICK) {
        PickCurrencyScreen(
            onCurrencySelected = {
                navController.previousBackStackEntry?.savedStateHandle?.set(Arguments.CURRENCY, it)
                navController.popBackStack()
            }, upPress = upPress
        )
    }

    composable(Destinations.MAIN_CURRENCY_PICK) {
        PickCurrencyScreen(
            isMainCurrency = true,
            onCurrencySelected = { upPress() },
            upPress = upPress
        )
    }

    composable<Destinations.Feedback> {
        val section = it.toRoute<Destinations.Feedback>()
        FeedbackScreen(upPress = upPress, FeedbackAction.valueOf(section.action), text = section.text)
    }


}