package com.merkost.suby.model

enum class Category(val categoryName: String, val emoji: String) {
    ENTERTAINMENT("Entertainment", "🍿"),
    TECHNOLOGY("Technology", "📱"),
    SOCIAL_MEDIA("Social Media", "📷"),
    EDUCATION("Education", "📚"),
    NEWS_MEDIA("News & Media", "📰"),
    FITNESS_HEALTH("Fitness & Health", "💪"),
    FOOD("Food", "🍔"),
    SHOPPING("Shopping", "🛍️"),
    UTILITIES("Utilities", "💡"),
    FINANCE("Finance", "💰"),
    TRAVEL("Travel", "✈️"),
    CUSTOM("Custom", "✏️")
}