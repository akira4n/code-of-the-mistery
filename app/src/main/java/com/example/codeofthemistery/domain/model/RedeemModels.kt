package com.example.codeofthemistery.domain.model

data class RewardCatalogItem(
    val id: String,
    val title: String,
    val partner: String,
    val description: String,
    val costPoints: Int,
    val stockRemaining: Int? // null means unlimited
)

data class RedeemedVoucher(
    val id: String,
    val rewardId: String,
    val rewardTitle: String,
    val partner: String,
    val voucherCode: String,
    val costPaid: Int,
    val redeemedAt: Long
)

sealed interface RedeemResult {
    data class Success(val voucher: RedeemedVoucher) : RedeemResult
    data object InsufficientPoints : RedeemResult
    data object OutOfStock : RedeemResult
    data class Failure(val message: String) : RedeemResult
}
