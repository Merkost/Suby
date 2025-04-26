package com.merkost.suby.presentation.states

sealed class EditSubscriptionEvent {
    data object SubscriptionSaved : EditSubscriptionEvent()
}