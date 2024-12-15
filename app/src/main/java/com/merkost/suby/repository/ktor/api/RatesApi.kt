package com.merkost.suby.repository.ktor.api

import com.merkost.suby.CURRENCY_ENDPOINT_FALLBACK
import com.merkost.suby.CURRENCY_ENDPOINT_FREE
import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.dto.RatesDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class RatesApi(private val client: HttpClient) {

    suspend fun getCurrencyRates(
        currencyCode: String
    ) = flow<Result<RatesDto>> {
        val url = buildString {
            append(CURRENCY_ENDPOINT_FREE)
            append("/${currencyCode.lowercase()}")
            append(".json")
        }

        val fallbackUrl = buildString {
            append(CURRENCY_ENDPOINT_FALLBACK)
            append("/${currencyCode.lowercase()}")
            append(".json")
        }

        val mainResponse = client.get(url)
        val response = if (mainResponse.status != HttpStatusCode.OK) {
            client.get(fallbackUrl)
        } else {
            mainResponse
        }

        if (response.status == HttpStatusCode.OK) {
            val responseContent: String = response.bodyAsText()
            val jsonObject = JSONObject(responseContent)

            val currencyRatesDto = parseCurrencyRates(jsonObject, currencyCode)
            emit(Result.success(currencyRatesDto))
        } else {
            emit(Result.failure(Exception("Failed to fetch currency rates: ${response.status}, ${response.bodyAsText()}")))
        }
    }.catch {
        emit(Result.failure(it))
    }

    private fun parseCurrencyRates(jsonObject: JSONObject, currencyCode: String): RatesDto {
        val date = jsonObject.getString("date")
        val rates = jsonObject.getJSONObject(currencyCode.lowercase())

        val ratesMap = mutableMapOf<Currency, Double>()

        rates.keys().forEach { key ->
            val value = rates.getDouble(key)
            Currency.findOrNull(key)?.let {
                ratesMap[it] = value
            }
        }

        return RatesDto(date, ratesMap)
    }
}