package com.merkost.suby

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import coil3.request.crossfade
import coil3.svg.SvgDecoder
import coil3.util.DebugLogger
import coil3.util.Logger
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.FeedbackAction
import com.merkost.suby.presentation.home.SubscriptionsScreen
import com.merkost.suby.presentation.onboarding.GreetingScreen
import com.merkost.suby.presentation.onboarding.OnboardingCurrencyScreen
import com.merkost.suby.presentation.premium.PremiumFeaturesScreen
import com.merkost.suby.presentation.screens.AboutScreen
import com.merkost.suby.presentation.screens.EditSubscriptionScreen
import com.merkost.suby.presentation.screens.FeedbackScreen
import com.merkost.suby.presentation.screens.NewSubscriptionScreen
import com.merkost.suby.presentation.screens.PickCurrencyScreen
import com.merkost.suby.presentation.screens.SubscriptionDetailsScreen
import com.merkost.suby.presentation.screens.calendar.CalendarViewScreen
import com.merkost.suby.presentation.viewModel.OnboardingViewModel
import com.merkost.suby.ui.theme.LocalAppState
import com.merkost.suby.utils.Arguments
import com.merkost.suby.utils.Destinations
import com.merkost.suby.utils.transition.ProvideAnimatedVisibilityScope
import com.merkost.suby.utils.transition.SharedTransitionProvider
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SubyMainNavigation() {
    val navController = rememberNavController()
    val appState = LocalAppState.current
    val startDestination =
        if (appState.isFirstTimeLaunch) Destinations.Greeting else Destinations.MainScreen

    ImageManager()

    Scaffold(
        modifier = Modifier,
        contentWindowInsets = WindowInsets(0.dp)
    ) { scaffoldPadding ->
        SharedTransitionProvider(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination,
            ) {
                NavGraph(
                    navController = navController,
                    upPress = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Destinations.MainScreen)
                        }
                    },
                )
            }
        }
    }
}

@Composable
internal fun ImageManager() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.filesDir.resolve("coil_cache"))
                    .maxSizeBytes(40 * 1024 * 1024)
                    .build()
            }
            .components {
                add(SvgDecoder.Factory())
            }
            .logger(logger = DebugLogger(minLevel = if (BuildConfig.DEBUG) Logger.Level.Debug else Logger.Level.Error))
            .build()
    }
}

private fun NavGraphBuilder.NavGraph(
    upPress: () -> Unit,
    navController: NavController,
) {
    composable<Destinations.Greeting> {
        WelcomeScreen(
            onContinue = {
                navController.navigate(Destinations.Onboarding)
            }
        )
    }

    composable<Destinations.Onboarding> {
        val onboardingViewModel = koinViewModel<OnboardingViewModel>()

        OnboardingPagerScreen(
            onComplete = { currency ->
                onboardingViewModel.saveMainCurrency(currency)
                onboardingViewModel.completeOnboarding()
                navController.navigate(Destinations.MainScreen) {
                    popUpTo(Destinations.Greeting) { inclusive = true }
                }
            },
            onEnableNotifications = {
                onboardingViewModel.enableNotifications()
            },
            upPress = upPress,
        )
    }

    composable<Destinations.MainScreen> { backStackEntry ->
        ProvideAnimatedVisibilityScope(this) {
            SubscriptionsScreen(
                onNavigate = { destination ->
                    navController.navigate(destination)
                }
            )
        }
    }

    composable<Destinations.NewSubscription> { backStackEntry ->
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
        ProvideAnimatedVisibilityScope(this) {
            SubscriptionDetailsScreen(
                upPress = upPress,
                onEditClick = {
                    navController.navigate(
                        Destinations.EditSubscription(
                            subscriptionInfo.subscriptionId
                        )
                    )
                },
                onCalendarClick = {
                    navController.navigate(Destinations.CalendarView)
                })
        }
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

    composable<Destinations.CalendarView> {
        ProvideAnimatedVisibilityScope(this) {
            CalendarViewScreen(
                upPress = upPress,
                onSubscriptionClick = {
                    navController.navigate(Destinations.SubscriptionInfo(it.id))
                }
            )
        }
    }

    composable<Destinations.About> {
        AboutScreen(upPress = upPress)
    }
}
