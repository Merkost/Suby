package com.merkost.suby.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Currency(val fullName: String, val symbol: String, val flagEmoji: String) :
    Parcelable {
    USD("US Dollar", "$", "🇺🇸"),
    EUR("Euro", "€", "🇪🇺"),
    JPY("Japanese Yen", "¥", "🇯🇵"),
    GBP("British Pound", "£", "🇬🇧"),
    AUD("Australian Dollar", "$", "🇦🇺"),
    CAD("Canadian Dollar", "$", "🇨🇦"),
    CHF("Swiss Franc", "CHF", "🇨🇭"),
    CNY("Chinese Yuan", "¥", "🇨🇳"),
    INR("Indian Rupee", "₹", "🇮🇳"),
    SGD("Singapore Dollar", "$", "🇸🇬"),
    NZD("New Zealand Dollar", "$", "🇳🇿"),
    BRL("Brazilian Real", "R$", "🇧🇷"),
    KRW("South Korean Won", "₩", "🇰🇷"),
    HKD("Hong Kong Dollar", "$", "🇭🇰"),
    SEK("Swedish Krona", "kr", "🇸🇪"),
    NOK("Norwegian Krone", "kr", "🇳🇴"),
    MXN("Mexican Peso", "$", "🇲🇽"),
    TRY("Turkish Lira", "₺", "🇹🇷"),
    RUB("Russian Ruble", "₽", "🇷🇺"),
    ZAR("South African Rand", "R", "🇿🇦"),
    AED("United Arab Emirates Dirham", "د.إ", "🇦🇪");

    @IgnoredOnParcel
    val code = this.name

    companion object {
        fun find(currencyName: String?): Currency {
            return Currency.values().find {
                it.name.lowercase() == currencyName?.lowercase()
            } ?: Currency.USD
        }

        fun findOrNull(currencyName: String?): Currency? {
            return Currency.values().find {
                it.name.lowercase() == currencyName?.lowercase()
            }
        }
    }
}