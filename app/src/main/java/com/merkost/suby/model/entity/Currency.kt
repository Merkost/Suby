package com.merkost.suby.model.entity

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Currency(val fullName: String, val symbol: String, val flagEmoji: String) :
    Parcelable {
    AED("United Arab Emirates Dirham", "د.إ", "🇦🇪"),
    ARS("Argentine Peso", "$", "🇦🇷"),
    AUD("Australian Dollar", "$", "🇦🇺"),
    BDT("Bangladeshi Taka", "৳", "🇧🇩"),
    BRL("Brazilian Real", "R$", "🇧🇷"),
    CAD("Canadian Dollar", "$", "🇨🇦"),
    CHF("Swiss Franc", "CHF", "🇨🇭"),
    CLP("Chilean Peso", "$", "🇨🇱"),
    COP("Colombian Peso", "$", "🇨🇴"),
    CNY("Chinese Yuan", "¥", "🇨🇳"),
    CZK("Czech Koruna", "Kč", "🇨🇿"),
    DKK("Danish Krone", "kr", "🇩🇰"),
    EGP("Egyptian Pound", "E£", "🇪🇬"),
    EUR("Euro", "€", "🇪🇺"),
    GBP("British Pound", "£", "🇬🇧"),
    HKD("Hong Kong Dollar", "$", "🇭🇰"),
    IDR("Indonesian Rupiah", "Rp", "🇮🇩"),
    ILS("Israeli Shekel", "₪", "🇮🇱"),
    INR("Indian Rupee", "₹", "🇮🇳"),
    JPY("Japanese Yen", "¥", "🇯🇵"),
    KRW("South Korean Won", "₩", "🇰🇷"),
    MXN("Mexican Peso", "$", "🇲🇽"),
    MYR("Malaysian Ringgit", "RM", "🇲🇾"),
    NGN("Nigerian Naira", "₦", "🇳🇬"),
    NOK("Norwegian Krone", "kr", "🇳🇴"),
    NZD("New Zealand Dollar", "$", "🇳🇿"),
    PHP("Philippine Peso", "₱", "🇵🇭"),
    PKR("Pakistani Rupee", "₨", "🇵🇰"),
    PLN("Polish Zloty", "zł", "🇵🇱"),
    RUB("Russian Ruble", "₽", "🇷🇺"),
    SAR("Saudi Riyal", "﷼", "🇸🇦"),
    SEK("Swedish Krona", "kr", "🇸🇪"),
    SGD("Singapore Dollar", "$", "🇸🇬"),
    THB("Thai Baht", "฿", "🇹🇭"),
    TRY("Turkish Lira", "₺", "🇹🇷"),
    USD("US Dollar", "$", "🇺🇸"),
    VND("Vietnamese Dong", "₫", "🇻🇳"),
    ZAR("South African Rand", "R", "🇿🇦"),
    OMR("Omani Rial", "ر.ع.", "🇴🇲"),
    QAR("Qatari Riyal", "ر.ق", "🇶🇦");

    @IgnoredOnParcel
    val code = this.name

    companion object {
        fun find(currencyName: String?): Currency {
            return entries.find {
                it.name.lowercase() == currencyName?.lowercase()
            } ?: USD
        }

        fun findOrNull(currencyName: String?): Currency? {
            return entries.find {
                it.name.lowercase() == currencyName?.lowercase()
            }
        }
    }
}