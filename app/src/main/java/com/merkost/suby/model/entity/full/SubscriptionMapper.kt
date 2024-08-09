package com.merkost.suby.model.entity.full

import com.merkost.suby.model.room.entity.related.SubscriptionWithCustomDetails
import com.merkost.suby.model.room.entity.related.SubscriptionWithDetails

fun SubscriptionWithCustomDetails.toSubscription() =
    Subscription(
        id = subscription.id,

        serviceId = subscription.serviceId,
        serviceName = serviceWithCategory.customService.name,
        serviceLogoUrl = serviceWithCategory.customService.imageUri,
        serviceCreatedAt = serviceWithCategory.customService.createdAt,
        serviceLastUpdated = serviceWithCategory.customService.lastUpdated,
        isCustomService = true,

        price = subscription.price,
        currency = subscription.currency,
        category = Category(
            id = serviceWithCategory.category.id,
            name = serviceWithCategory.category.name,
            emoji = serviceWithCategory.category.emoji,
            createdAt = serviceWithCategory.category.createdAt,
            lastUpdated = serviceWithCategory.category.lastUpdated
        ),
        period = subscription.period,
        customPeriodType = subscription.customPeriodType,
        customPeriodDuration = subscription.customPeriodDuration,
        status = subscription.status,
        paymentDate = subscription.paymentDate,
        createdDate = subscription.createdDate,
        durationDays = subscription.durationDays,
        description = subscription.description
    )

fun SubscriptionWithDetails.toSubscription() =
    Subscription(
        id = subscription.id,

        serviceId = subscription.serviceId,
        serviceName = serviceWithCategory.service.name,
        serviceLogoUrl = serviceWithCategory.service.logoLink,
        serviceCreatedAt = serviceWithCategory.service.createdAt,
        serviceLastUpdated = serviceWithCategory.service.lastUpdated,
        isCustomService = false,

        price = subscription.price,
        currency = subscription.currency,

        category = Category(
            id = serviceWithCategory.category.id,
            name = serviceWithCategory.category.name,
            emoji = serviceWithCategory.category.emoji,
            createdAt = serviceWithCategory.category.createdAt,
            lastUpdated = serviceWithCategory.category.lastUpdated
        ),
        period = subscription.period,
        customPeriodType = subscription.customPeriodType,
        customPeriodDuration = subscription.customPeriodDuration,
        status = subscription.status,
        paymentDate = subscription.paymentDate,
        createdDate = subscription.createdDate,
        durationDays = subscription.durationDays,
        description = subscription.description
    )
