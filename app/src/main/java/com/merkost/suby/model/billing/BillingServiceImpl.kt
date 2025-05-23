package com.merkost.suby.model.billing

import android.app.Activity
import com.merkost.suby.utils.analytics.Analytics
import com.qonversion.android.sdk.Qonversion
import com.qonversion.android.sdk.dto.QUser
import com.qonversion.android.sdk.dto.QonversionError
import com.qonversion.android.sdk.dto.entitlements.QEntitlement
import com.qonversion.android.sdk.dto.offerings.QOffering
import com.qonversion.android.sdk.dto.offerings.QOfferings
import com.qonversion.android.sdk.dto.products.QProduct
import com.qonversion.android.sdk.listeners.QonversionEntitlementsCallback
import com.qonversion.android.sdk.listeners.QonversionOfferingsCallback
import com.qonversion.android.sdk.listeners.QonversionProductsCallback
import com.qonversion.android.sdk.listeners.QonversionUserCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

class BillingServiceImpl : BillingService {

    private val TAG = "BillingService"

    override suspend fun getUserInfo() = suspendCancellableCoroutine<Result<QUser>> {
        Qonversion.shared.userInfo(
            object : QonversionUserCallback {
                override fun onError(error: QonversionError) {
                    Timber.tag(TAG).w("Could not get user: $error")
                    it.resume(Result.failure(Exception(error.additionalMessage)))
                }

                override fun onSuccess(user: QUser) {
                    Timber.tag(TAG).d("Got user: $user")
                    it.resume(Result.success(user))
                }
            }
        )
    }

    override suspend fun purchase(
        activity: Activity,
        product: QProduct,
        offerId: String?
    ) =
        suspendCancellableCoroutine<Result<QEntitlement?>> { continuation ->
            Timber.tag(TAG).d("Purchasing: $product")
            Qonversion.shared.purchase(
                activity,
                product.toPurchaseModel(offerId),
                object : QonversionEntitlementsCallback {
                    override fun onError(error: QonversionError) {
                        Timber.tag(TAG).w("Could not purchase: $error")
                        continuation.resume(Result.failure(Exception(error.additionalMessage)))
                    }
                    override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                        Timber.tag(TAG).d("Purchased: $entitlements")
                        Analytics.logPremiumPurchased()
                        continuation.resume(Result.success(entitlements.values.firstOrNull()))
                    }
                }
            )
        }

    override suspend fun getProducts() =
        suspendCancellableCoroutine<Result<List<QProduct>>> { continuation ->
            Qonversion.shared.products(object : QonversionProductsCallback {
                override fun onError(error: QonversionError) {
                    Timber.tag(TAG).w("Could not get Products: $error")
                    continuation.resume(Result.failure(Exception(error.additionalMessage)))
                }

                override fun onSuccess(products: Map<String, QProduct>) {
                    Timber.tag(TAG).d("Got products: $products")
                    continuation.resume(Result.success(products.values.toList()))
                }
            })
        }

    override suspend fun getOfferings(): Result<QOfferings> =
        suspendCancellableCoroutine { continuation ->
            Qonversion.shared.offerings(object : QonversionOfferingsCallback {
                override fun onError(error: QonversionError) {
                    Timber.tag(TAG).w("Could not get Offerings: $error")
                    continuation.resume(Result.failure(Exception(error.additionalMessage)))
                }

                override fun onSuccess(offerings: QOfferings) {
                    Timber.tag(TAG).d("Got offerings: $offerings")
                    continuation.resume(Result.success(offerings))
                }
            }
            )
        }

    override suspend fun getMainOffering(): Result<QOffering> {
        val offerings = getOfferings().getOrNull()
        val mainOffering = offerings?.main
        val productExists = mainOffering?.products.isNullOrEmpty().not()
        return if (mainOffering != null && productExists) {
            Result.success(mainOffering)
        } else {
            Result.failure(Exception("Failed to get main offering"))
        }
    }

    override suspend fun restorePurchase(): Result<List<QEntitlement>> =
        suspendCancellableCoroutine { continuation ->
            Qonversion.shared.restore(object : QonversionEntitlementsCallback {
                override fun onError(error: QonversionError) {
                    Timber.tag(TAG).w("Restore error: $error")
                    continuation.resume(Result.failure(Exception(error.additionalMessage)))
                }

                override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                    Timber.tag(TAG).d("Restore success: $entitlements")
                    continuation.resume(Result.success(entitlements.values.toList()))
                }
            })
        }

    override suspend fun getEntitlements() =
        suspendCancellableCoroutine<List<QEntitlement>> { cont ->
            Qonversion.shared.checkEntitlements(
                object : QonversionEntitlementsCallback {
                    override fun onError(error: QonversionError) {
                        Timber.tag("AppViewModel").w("Error checking entitlements: $error")
                        cont.resume(emptyList())
                    }

                    override fun onSuccess(entitlements: Map<String, QEntitlement>) {
                        Timber.tag("AppViewModel").d("Entitlements: $entitlements")
                        cont.resume(entitlements.values.toList())
                    }
                }
            )
        }

}