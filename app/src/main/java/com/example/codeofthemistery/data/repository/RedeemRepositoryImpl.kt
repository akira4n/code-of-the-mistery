package com.example.codeofthemistery.data.repository

import com.example.codeofthemistery.core.util.HashUtil
import com.example.codeofthemistery.data.local.dao.RedeemDao
import com.example.codeofthemistery.data.local.dao.UserDao
import com.example.codeofthemistery.data.local.entity.RedeemedVoucherEntity
import com.example.codeofthemistery.data.local.seed.CatalogSeeder
import com.example.codeofthemistery.domain.model.RedeemResult
import com.example.codeofthemistery.domain.model.RedeemedVoucher
import com.example.codeofthemistery.domain.model.RewardCatalogItem
import com.example.codeofthemistery.domain.repository.RedeemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RedeemRepositoryImpl(
    private val redeemDao: RedeemDao,
    private val userDao: UserDao,
    private val catalogSeeder: CatalogSeeder
) : RedeemRepository {

    override fun observeCatalog(): Flow<List<RewardCatalogItem>> =
        redeemDao.observeCatalog().map { rows ->
            rows.map {
                RewardCatalogItem(
                    id = it.id,
                    title = it.title,
                    partner = it.partner,
                    description = it.description,
                    costPoints = it.costPoints,
                    stockRemaining = it.stockRemaining
                )
            }
        }

    override fun observeVoucherHistory(userId: String): Flow<List<RedeemedVoucher>> =
        redeemDao.observeVoucherHistory(userId).map { rows ->
            rows.map { it.toDomain() }
        }

    override suspend fun redeemReward(userId: String, rewardId: String): RedeemResult {
        catalogSeeder.ensureSeeded()
        val reward = redeemDao.getRewardById(rewardId)
            ?: return RedeemResult.Failure("Produk hadiah tidak ditemukan.")
        val user = userDao.getById(userId)
            ?: return RedeemResult.Failure("Pengguna tidak ditemukan.")
        if (reward.stockRemaining != null && reward.stockRemaining <= 0) {
            return RedeemResult.OutOfStock
        }
        if (user.points < reward.costPoints) {
            return RedeemResult.InsufficientPoints
        }

        val voucherEntity = RedeemedVoucherEntity(
            id = "vch_${UUID.randomUUID()}",
            userId = userId,
            rewardId = reward.id,
            rewardTitle = reward.title,
            partner = reward.partner,
            voucherCode = HashUtil.generateVoucherCode(reward.partner),
            costPaid = reward.costPoints,
            redeemedAt = System.currentTimeMillis()
        )

        return try {
            val ok = redeemDao.executeAtomicRedeem(
                userDao = userDao,
                userId = userId,
                reward = reward,
                voucher = voucherEntity
            )
            if (ok) RedeemResult.Success(voucherEntity.toDomain())
            else RedeemResult.InsufficientPoints
        } catch (e: IllegalStateException) {
            if (e.message == "OUT_OF_STOCK") RedeemResult.OutOfStock
            else RedeemResult.Failure(e.message ?: "Gagal menukar poin.")
        }
    }

    private fun RedeemedVoucherEntity.toDomain(): RedeemedVoucher = RedeemedVoucher(
        id = id,
        rewardId = rewardId,
        rewardTitle = rewardTitle,
        partner = partner,
        voucherCode = voucherCode,
        costPaid = costPaid,
        redeemedAt = redeemedAt
    )
}
