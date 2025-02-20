package com.merkost.suby.di

import com.merkost.suby.presentation.viewModel.AppViewModel
import com.merkost.suby.presentation.viewModel.BillingViewModel
import com.merkost.suby.presentation.viewModel.CalendarViewModel
import com.merkost.suby.presentation.viewModel.CustomServiceViewModel
import com.merkost.suby.presentation.viewModel.EditSubscriptionViewModel
import com.merkost.suby.presentation.viewModel.FeedbackViewModel
import com.merkost.suby.presentation.viewModel.MainViewModel
import com.merkost.suby.presentation.viewModel.NewSubscriptionViewModel
import com.merkost.suby.presentation.viewModel.OnboardingViewModel
import com.merkost.suby.presentation.viewModel.SelectServiceViewModel
import com.merkost.suby.presentation.viewModel.SubscriptionDetailsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::CustomServiceViewModel)
    viewModelOf(::EditSubscriptionViewModel)
    viewModelOf(::FeedbackViewModel)
    viewModelOf(::NewSubscriptionViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::SelectServiceViewModel)
    viewModelOf(::SubscriptionDetailsViewModel)
    viewModelOf(::BillingViewModel)
    viewModelOf(::CalendarViewModel)
}