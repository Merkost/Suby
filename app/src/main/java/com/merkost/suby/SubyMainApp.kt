package com.merkost.suby

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.FeedbackAction
import com.merkost.suby.presentation.onboarding.GreetingScreen
import com.merkost.suby.presentation.onboarding.OnboardingCurrencyScreen
import com.merkost.suby.presentation.screens.FeedbackScreen
import com.merkost.suby.presentation.screens.NewSubscriptionScreen
import com.merkost.suby.presentation.screens.PickCurrencyScreen
import com.merkost.suby.presentation.screens.SubscriptionInfoScreen
import com.merkost.suby.presentation.screens.SubscriptionsScreen
import com.merkost.suby.utils.Arguments
import com.merkost.suby.utils.Destinations
import com.merkost.suby.utils.isFirstTimeState
import com.merkost.suby.presentation.viewModel.OnboardingViewModel

@Composable
fun SubyMainApp() {
    val navController = rememberNavController()

    val isFirstTime by isFirstTimeState()
    val startDestination = if (isFirstTime) Destinations.GREETING else Destinations.MAIN_SCREEN
//    val startDestination = Destinations.GREETING
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
                upPress = {
                    if (!navController.popBackStack()) {
                        navController.navigate(Destinations.MAIN_SCREEN)
                    }
                },
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
        GreetingScreen(onContinueClick = { navController.navigate(Destinations.ONBOARDING) })
    }

    navigation(Destinations.ONBOARDING_CURRENCY, Destinations.ONBOARDING) {
        composable(Destinations.ONBOARDING_CURRENCY) {
            val onboardingViewModel = hiltViewModel<OnboardingViewModel>()

            OnboardingCurrencyScreen(
                onCurrencySelected = {
                    onboardingViewModel.saveMainCurrency(it)
                    navController.navigate(Destinations.MAIN_SCREEN)
                }
            )
        }

        //Other onboarding screens
    }

    composable(Destinations.MAIN_SCREEN) { backStackEntry ->
        SubscriptionsScreen(onAddClicked = {
            navController.navigate(Destinations.NEW_SUBSCRIPTION)
        }, onCurrencyClick = {
            navController.navigate(Destinations.CurrencyPick(true))
        }, onSubscriptionInfo = { subId ->
            navController.navigate(Destinations.SubscriptionInfo(subId))
        })
    }

    composable(Destinations.NEW_SUBSCRIPTION) { backStackEntry ->
        NewSubscriptionScreen(
            pickedCurrency = backStackEntry.savedStateHandle.get<Currency>(Arguments.CURRENCY),
            onCurrencyClicked = { navController.navigate(Destinations.CurrencyPick(false)) },
            upPress = upPress,
            onSuggestService = { inputText ->
                navController.navigate(
                    Destinations.Feedback(
                        FeedbackAction.ADD_SERVICE.toString(),
                        text = inputText
                    )
                )
            }
        )
    }

    composable<Destinations.SubscriptionInfo> {
        SubscriptionInfoScreen(
            subscriptionId = it.toRoute<Destinations.SubscriptionInfo>().subscriptionId,
            upPress = upPress
        )
    }

    composable<Destinations.CurrencyPick> {
        val destination = it.toRoute<Destinations.CurrencyPick>()
        PickCurrencyScreen(
            isMainCurrency = destination.isMainCurrency,
            onCurrencySelected = {
                if (destination.isMainCurrency.not()) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Arguments.CURRENCY,
                        it
                    )
                }
                navController.popBackStack()
            }, upPress = upPress
        )
    }

    composable<Destinations.Feedback> {
        val section = it.toRoute<Destinations.Feedback>()
        FeedbackScreen(
            upPress = upPress,
            FeedbackAction.valueOf(section.action),
            text = section.text
        )
    }

}
