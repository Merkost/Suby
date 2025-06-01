package com.merkost.suby.utils.transition

object SharedTransitionKeys {
    
    object Subscription {
        fun serviceName(subscriptionId: Int) = "subscription_service_name_$subscriptionId"
        fun serviceLogo(subscriptionId: Int) = "subscription_service_logo_$subscriptionId"
        fun serviceRow(subscriptionId: Int) = "subscription_service_row_$subscriptionId"
        fun statusBubble(subscriptionId: Int) = "subscription_status_bubble_$subscriptionId"
        
        const val SERVICE_NAME_BOUNDS = "subscription_service_name_bounds"
        const val SERVICE_LOGO_BOUNDS = "subscription_service_logo_bounds"
    }
    
    object Service {
        fun name(serviceId: Int) = "service_name_$serviceId"
        fun logo(serviceId: Int) = "service_logo_$serviceId"
        fun row(serviceId: Int) = "service_row_$serviceId"
    }
    
    object Common {
        const val HERO_BACKGROUND = "hero_background"
        const val PRICE_SECTION = "price_section"
    }
} 