package com.example.codeofthemistery.domain.repository

import com.example.codeofthemistery.domain.model.RedeemResult
import com.example.codeofthemistery.domain.model.RedeemedVoucher
import com.example.codeofthemistery.domain.model.RewardCatalogItem
import kotlinx.coroutines.flow.Flow

interface RedeemRepository {
    fun observeCatalog(): Flow<List<RewardCatalogItem>>
    fun observeVoucherHistory(userId: String): Flow<List<RedeemedVoucher>>
    suspend fun redeemReward(userId: String, rewardId: String): RedeemResult
}
