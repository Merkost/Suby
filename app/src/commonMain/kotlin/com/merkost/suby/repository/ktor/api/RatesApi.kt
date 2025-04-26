package com.merkost.suby.repository.ktor.api

import com.merkost.suby.model.entity.Currency
import com.merkost.suby.model.entity.dto.RatesDto
import com.merkost.suby.utils.AndroidConstants.CURRENCY_ENDPOINT_FALLBACK
import com.merkost.suby.utils.AndroidConstants.CURRENCY_ENDPOINT_FREE
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RatesApi(private val client: HttpClient) {

    suspend fun getCurrencyRates(currencyCode: String) = flow<Result<RatesDto>> {
        val code = currencyCode.lowercase()
        val url = "$CURRENCY_ENDPOINT_FREE/$code.json"
        val fallbackUrl = "$CURRENCY_ENDPOINT_FALLBACK/$code.json"
        val mainResponse = client.get(url)
        val response = if (mainResponse.status != HttpStatusCode.OK) {
            client.get(fallbackUrl)
        } else {
            mainResponse
        }
        if (response.status == HttpStatusCode.OK) {
            val text = response.bodyAsText()
            val root = Json.parseToJsonElement(text).jsonObject
            val dto = parseCurrencyRates(root, code)
            emit(Result.success(dto))
        } else {
            emit(Result.failure(Exception("Failed to fetch currency rates: ${response.status}, ${response.bodyAsText()}")))
        }
    }.catch {
        emit(Result.failure(it))
    }

    private fun parseCurrencyRates(root: kotlinx.serialization.json.JsonObject, code: String): RatesDto {
        val date = root["date"]!!.jsonPrimitive.content
        val ratesObj = root[code]!!.jsonObject
        val ratesMap = ratesObj.mapNotNull { (key, value) ->
            Currency.findOrNull(key)?.let { it to value.jsonPrimitive.double }
        }.toMap()
        return RatesDto(date, ratesMap)
    }
}