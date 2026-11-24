package com.example.codeofthemistery.feature.redeem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codeofthemistery.domain.model.GameEvent
import com.example.codeofthemistery.domain.model.RedeemResult
import com.example.codeofthemistery.domain.model.RedeemedVoucher
import com.example.codeofthemistery.domain.model.RewardCatalogItem
import com.example.codeofthemistery.domain.repository.AchievementRepository
import com.example.codeofthemistery.domain.repository.AuthRepository
import com.example.codeofthemistery.domain.repository.RedeemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface RedeemDialogState {
    data object None : RedeemDialogState
    data class Confirm(val reward: RewardCatalogItem, val currentPoints: Int) : RedeemDialogState
    data class Success(val voucher: RedeemedVoucher) : RedeemDialogState
    data object InsufficientPoints : RedeemDialogState
    data class Error(val message: String) : RedeemDialogState
}

data class RedeemUiState(
    val userId: String? = null,
    val userPoints: Int = 0,
    val catalog: List<RewardCatalogItem> = emptyList(),
    val history: List<RedeemedVoucher> = emptyList(),
    val dialogState: RedeemDialogState = RedeemDialogState.None,
    val isProcessing: Boolean = false
)

class RedeemViewModel(
    authRepository: AuthRepository,
    private val redeemRepository: RedeemRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val dialogFlow = MutableStateFlow<RedeemDialogState>(RedeemDialogState.None)
    private val processingFlow = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<RedeemUiState> = authRepository.observeActiveUser()
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(RedeemUiState())
            } else {
                combine(
                    redeemRepository.observeCatalog(),
                    redeemRepository.observeVoucherHistory(user.id),
                    dialogFlow,
                    processingFlow
                ) { catalog, history, dialog, processing ->
                    RedeemUiState(
                        userId = user.id,
                        userPoints = user.points,
                        catalog = catalog,
                        history = history,
                        dialogState = dialog,
                        isProcessing = processing
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RedeemUiState())

    fun onClickRedeem(reward: RewardCatalogItem) {
        val pts = uiState.value.userPoints
        if (pts < reward.costPoints) {
            dialogFlow.value = RedeemDialogState.InsufficientPoints
        } else {
            dialogFlow.value = RedeemDialogState.Confirm(reward, pts)
        }
    }

    fun confirmRedeem(reward: RewardCatalogItem) {
        val uid = uiState.value.userId ?: return
        viewModelScope.launch {
            processingFlow.value = true
            when (val res = redeemRepository.redeemReward(uid, reward.id)) {
                is RedeemResult.Success -> {
                    achievementRepository.evaluateEvent(uid, GameEvent.VoucherRedeemed)
                    dialogFlow.value = RedeemDialogState.Success(res.voucher)
                }
                RedeemResult.InsufficientPoints -> {
                    dialogFlow.value = RedeemDialogState.InsufficientPoints
                }
                RedeemResult.OutOfStock -> {
                    dialogFlow.value = RedeemDialogState.Error("Stok hadiah ini sudah habis.")
                }
                is RedeemResult.Failure -> {
                    dialogFlow.value = RedeemDialogState.Error(res.message)
                }
            }
            processingFlow.value = false
        }
    }

    fun dismissDialog() {
        dialogFlow.update { RedeemDialogState.None }
    }
}
