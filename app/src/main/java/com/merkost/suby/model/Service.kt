package com.merkost.suby.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.merkost.suby.R

enum class Service(
    val serviceName: String,
    val category: Category,
    val brandColor: Color,
    @DrawableRes val iconResource: Int? = null
) {
    // Entertainment
    NETFLIX("Netflix", Category.ENTERTAINMENT, Color(0xFFE50914), R.drawable.ic_netflix),
    HULU("Hulu", Category.ENTERTAINMENT, Color(0xFF1CE783), R.drawable.ic_hulu),
    AMAZON_PRIME_VIDEO("Amazon Prime Video", Category.ENTERTAINMENT, Color(0xFF00A8E1), R.drawable.ic_amazon_prime),
    SPOTIFY("Spotify", Category.ENTERTAINMENT, Color(0xFF1ED760), R.drawable.ic_spotify),
    APPLE_MUSIC("Apple Music", Category.ENTERTAINMENT, Color(0xFFBB0000), R.drawable.ic_apple_music),
    DISNEY_PLUS("Disney+", Category.ENTERTAINMENT, Color(0xFF0033CC), R.drawable.ic_disney_plus),
    HBO_MAX("HBO Max", Category.ENTERTAINMENT, Color(0xFF45046A), R.drawable.ic_hbo),
    PEACOCK("Peacock", Category.ENTERTAINMENT, Color(0xFFFF5700)),

    // Technology
    DROPBOX("Dropbox", Category.TECHNOLOGY, Color(0xFF007EE5)),
    MICROSOFT_365("Microsoft 365", Category.TECHNOLOGY, Color(0xFF0078D4)),
    ICLOUD("iCloud", Category.TECHNOLOGY, Color(0xFF0071E0)),
    GOOGLE_DRIVE("Google Drive", Category.TECHNOLOGY, Color(0xFF4285F4)),
    EVERNOTE("Evernote", Category.TECHNOLOGY, Color(0xFF2DBE60)),
    ADOBE_CREATIVE_CLOUD("Adobe Creative Cloud", Category.TECHNOLOGY, Color(0xFFFF0000)),
    TRELLO("Trello", Category.TECHNOLOGY, Color(0xFF0079BF)),

    // Social Media
    TWITTER_BLUE("Twitter Blue", Category.SOCIAL_MEDIA, Color(0xFF1DA1F2)),
    LINKEDIN_PREMIUM("LinkedIn Premium", Category.SOCIAL_MEDIA, Color(0xFF0077B5)),
    INSTAGRAM_PRO("Instagram Pro", Category.SOCIAL_MEDIA, Color(0xFF833AB4)),
    FACEBOOK_PREMIUM("Facebook Premium", Category.SOCIAL_MEDIA, Color(0xFF1877F2)),
    PINTEREST_PREMIUM("Pinterest Premium", Category.SOCIAL_MEDIA, Color(0xFFBD081C)),
    SNAPCHAT_PREMIUM("Snapchat Premium", Category.SOCIAL_MEDIA, Color(0xFFFFFC00)),

    // Education
    UDEMY("Udemy", Category.EDUCATION, Color(0xFFEC5252)),
    COURSERA("Coursera", Category.EDUCATION, Color(0xFF0056D2)),
    SKILLSHARE("Skillshare", Category.EDUCATION, Color(0xFFF3671A)),
    MASTERCLASS("MasterClass", Category.EDUCATION, Color(0xFF000000)),
    KHAN_ACADEMY("Khan Academy", Category.EDUCATION, Color(0xFF14BF96)),
    EDX("edX", Category.EDUCATION, Color(0xFF28324E)),

    // News & Media
    THE_NEW_YORK_TIMES("The New York Times", Category.NEWS_MEDIA, Color(0xFF231F20)),
    THE_ECONOMIST("The Economist", Category.NEWS_MEDIA, Color(0xFFE3120B)),
    WASHINGTON_POST("Washington Post", Category.NEWS_MEDIA, Color(0xFF231F20)),
    BBC_NEWS("BBC News", Category.NEWS_MEDIA, Color(0xFFBB1919)),
    REUTERS("Reuters", Category.NEWS_MEDIA, Color(0xFFFF8000)),
    NPR("NPR", Category.NEWS_MEDIA, Color(0xFF0066CC)),


    CUSTOM("Other Service", Category.CUSTOM, Color.Unspecified),

}