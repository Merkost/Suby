package com.merkost.suby

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.FeedbackAction
import com.merkost.suby.presentation.home.SubscriptionsScreen
import com.merkost.suby.presentation.onboarding.GreetingScreen
import com.merkost.suby.presentation.onboarding.OnboardingCurrencyScreen
import com.merkost.suby.presentation.premium.PremiumFeaturesScreen
import com.merkost.suby.presentation.screens.EditSubscriptionScreen
import com.merkost.suby.presentation.screens.FeedbackScreen
import com.merkost.suby.presentation.screens.NewSubscriptionScreen
import com.merkost.suby.presentation.screens.PickCurrencyScreen
import com.merkost.suby.presentation.screens.SubscriptionDetailsScreen
import com.merkost.suby.presentation.viewModel.OnboardingViewModel
import com.merkost.suby.utils.Arguments
import com.merkost.suby.utils.Destinations
import com.merkost.suby.utils.appState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SubyMainApp() {
    val navController = rememberNavController()

    val appState by appState()
    val startDestination =
        if (appState.isFirstTimeLaunch) Destinations.GREETING else Destinations.MAIN_SCREEN
//    val startDestination = Destinations.GREETING
    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets(0)
    ) { scaffoldPadding ->
        NavHost(
            modifier = Modifier
                .padding(scaffoldPadding),
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
            val onboardingViewModel = koinViewModel<OnboardingViewModel>()

            OnboardingCurrencyScreen(onCurrencySelected = {
                onboardingViewModel.saveMainCurrency(it)
                navController.navigate(Destinations.MAIN_SCREEN)
            })
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
        }, onPremiumClick = {
            navController.navigate(Destinations.PremiumFeatures)
        })
    }

    composable(Destinations.NEW_SUBSCRIPTION) { backStackEntry ->
        NewSubscriptionScreen(
            pickedCurrency = backStackEntry.savedStateHandle.get<Currency>(Arguments.CURRENCY),
            onCurrencyClicked = { navController.navigate(Destinations.CurrencyPick(false)) },
            upPress = upPress,
            onSuggestService = { inputText ->
                navController.navigate(
                    Destinations.Feedback(FeedbackAction.ADD_SERVICE.toString(), text = inputText)
                )
            },
            onPremiumClicked = {
                navController.navigate(Destinations.PremiumFeatures)
            },
        )
    }

    composable<Destinations.EditSubscription> {
        val subscriptionId = it.toRoute<Destinations.EditSubscription>().subscriptionId
        EditSubscriptionScreen(
            subscriptionId = subscriptionId,
            onSave = {
                navController.popBackStack()
            },
            onCancel = {
                navController.popBackStack()
            },
            pickedCurrency = it.savedStateHandle.get<Currency>(Arguments.CURRENCY),
            onCurrencyClicked = { navController.navigate(Destinations.CurrencyPick(false)) },
        )

    }

    composable<Destinations.SubscriptionInfo> {
        val subscriptionInfo = it.toRoute<Destinations.SubscriptionInfo>()
        SubscriptionDetailsScreen(upPress = upPress, onEditClick = {
            navController.navigate(
                Destinations.EditSubscription(
                    subscriptionInfo.subscriptionId
                )
            )
        })
    }

    composable<Destinations.CurrencyPick> {
        val destination = it.toRoute<Destinations.CurrencyPick>()
        PickCurrencyScreen(
            isMainCurrency = destination.isMainCurrency, onCurrencySelected = {
                if (destination.isMainCurrency.not()) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        Arguments.CURRENCY, it
                    )
                }
                navController.popBackStack()
            }, upPress = upPress
        )
    }

    composable<Destinations.Feedback> {
        val section = it.toRoute<Destinations.Feedback>()
        FeedbackScreen(
            upPress = upPress, FeedbackAction.valueOf(section.action), text = section.text
        )
    }

    composable<Destinations.PremiumFeatures> {
        PremiumFeaturesScreen(onBackClick = upPress)
    }

}
