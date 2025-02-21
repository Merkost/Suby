package com.merkost.suby.model.billing

import android.app.Activity
import com.qonversion.android.sdk.dto.QUser
import com.qonversion.android.sdk.dto.entitlements.QEntitlement
import com.qonversion.android.sdk.dto.offerings.QOffering
import com.qonversion.android.sdk.dto.offerings.QOfferings
import com.qonversion.android.sdk.dto.products.QProduct

interface BillingService {
    suspend fun getProducts(): Result<List<QProduct>>
    suspend fun getOfferings(): Result<QOfferings>
    suspend fun getMainOffering(): Result<QOffering>
    suspend fun restorePurchase(): Result<List<QEntitlement>>
    suspend fun getEntitlements(): List<QEntitlement>
    suspend fun getUserInfo(): Result<QUser>
    suspend fun purchase(activity: Activity, product: QProduct, offerId: String?): Result<QEntitlement?>
}