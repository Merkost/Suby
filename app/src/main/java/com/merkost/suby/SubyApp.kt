package com.merkost.suby

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.merkost.suby.presentation.GreetingScreen
import com.merkost.suby.presentation.SubscriptionsScreen
import com.merkost.suby.utils.Destinations

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubyApp() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier,
    ) { scaffoldPadding ->
        NavHost(
            modifier = Modifier
                .padding(scaffoldPadding)
                .consumeWindowInsets(scaffoldPadding),
            navController = navController,
            startDestination = Destinations.GREETING,
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

    composable(Destinations.ONBOARDING) {
        // TODO:
    }

    composable(Destinations.MAIN_SCREEN) {
        SubscriptionsScreen(onAddClicked = {})
    }


}