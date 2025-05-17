package com.merkost.suby.model.entity.full

import com.merkost.suby.model.entity.BasePeriod
import com.merkost.suby.model.room.entity.related.SubscriptionWithDetails

fun SubscriptionWithDetails.toSubscription() =
    Subscription(
        id = subscription.id,

        serviceId = subscription.serviceId,
        serviceName = serviceWithCategory.service.name,
        serviceLogoUrl = serviceWithCategory.service.imageLink,
        serviceCreatedAt = serviceWithCategory.service.createdAt,
        serviceLastUpdated = serviceWithCategory.service.lastUpdated,
        isCustomService = serviceWithCategory.service.backendId == null,

        price = subscription.price,
        currency = subscription.currency,

        category = Category(
            id = serviceWithCategory.category.id,
            name = serviceWithCategory.category.name,
            emoji = serviceWithCategory.category.emoji,
            createdAt = serviceWithCategory.category.createdAt,
            lastUpdated = serviceWithCategory.category.lastUpdated
        ),
        period = BasePeriod(
            type = subscription.periodType,
            duration = subscription.periodDuration
        ),
        status = subscription.status,
        paymentDate = subscription.paymentDate,
        paymentStartDate = subscription.paymentStartDate,
        createdDate = subscription.createdDate,
        description = subscription.description
    )
