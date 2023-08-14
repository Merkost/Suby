package com.merkost.suby.repository.ktor.api

import com.merkost.suby.CURRENCY_ENDPOINT_FREE
import com.merkost.suby.model.Currency
import com.merkost.suby.model.entity.Rates
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class RatesApi(private val client: HttpClient) {

    suspend fun getCurrencyRates(
        currencyCode: String
    ) = flow<Rates> {
        val url = buildString {
            append(CURRENCY_ENDPOINT_FREE)
            append("/${currencyCode.lowercase()}")
            append(".json")
        }

        val response: HttpResponse = client.get(url)

        if (response.status == HttpStatusCode.OK) {
            val responseContent: String = response.bodyAsText()
            val jsonObject = JSONObject(responseContent)

            val date = jsonObject.getString("date")
            val rates = jsonObject.getJSONObject(currencyCode.lowercase())

            val ratesMap = mutableMapOf<Currency, Double>()

            rates.keys().forEach { key ->
                val value = rates.getDouble(key)
                Currency.findOrNull(key)?.let {
                    ratesMap[it] = value
                }
            }

            val currencyRates = Rates(date, ratesMap)
            emit(currencyRates)
        } else {
            // Handle error cases
            // Emit an empty or default CurrencyRates object
        }
    }
}