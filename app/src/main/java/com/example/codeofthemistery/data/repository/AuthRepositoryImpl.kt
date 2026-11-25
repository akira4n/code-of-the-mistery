package com.example.codeofthemistery.data.repository

import com.example.codeofthemistery.core.util.AppResult
import com.example.codeofthemistery.core.util.HashUtil
import com.example.codeofthemistery.data.local.dao.AchievementDao
import com.example.codeofthemistery.data.local.dao.ChapterDao
import com.example.codeofthemistery.data.local.dao.InventoryDao
import com.example.codeofthemistery.data.local.dao.UserDao
import com.example.codeofthemistery.data.local.datastore.SessionPreferences
import com.example.codeofthemistery.data.local.entity.UserEntity
import com.example.codeofthemistery.data.local.seed.AppSeedData
import com.example.codeofthemistery.data.local.seed.CatalogSeeder
import com.example.codeofthemistery.domain.model.User
import com.example.codeofthemistery.domain.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val chapterDao: ChapterDao,
    private val inventoryDao: InventoryDao,
    private val achievementDao: AchievementDao,
    private val sessionPreferences: SessionPreferences,
    private val catalogSeeder: CatalogSeeder
) : AuthRepository {

    private var pendingResetEmail: String? = null
    private var pendingResetOtp: String? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeActiveUser(): Flow<User?> =
        sessionPreferences.activeUserIdFlow.flatMapLatest { userId ->
            if (userId == null) flowOf(null)
            else userDao.observeUser(userId).map { entity ->
                if (entity == null || entity.isDemoAccount) null else entity.toDomain()
            }
        }

    override fun observeHapticsEnabled(): Flow<Boolean> = sessionPreferences.hapticsEnabledFlow
    override fun observeSoundEnabled(): Flow<Boolean> = sessionPreferences.soundEnabledFlow

    override suspend fun hasActiveSession(): Boolean {
        catalogSeeder.ensureSeeded()
        userDao.purgeLegacyDemoUsers()
        val id = sessionPreferences.getActiveUserId() ?: return false
        val exists = userDao.getById(id) != null
        if (!exists) {
            sessionPreferences.setActiveUserId(null)
        }
        return exists
    }

    override suspend fun login(email: String, password: String): AppResult<User> {
        catalogSeeder.ensureSeeded()
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || password.isBlank()) {
            return AppResult.Error("Email dan password wajib diisi.")
        }
        val entity = userDao.getByEmail(cleanEmail)
            ?: return AppResult.Error("Akun dengan email tersebut belum terdaftar.")
        val hash = HashUtil.hashPassword(password, cleanEmail)
        if (entity.passwordHash != hash) {
            return AppResult.Error("Password yang Anda masukkan salah.")
        }
        sessionPreferences.setActiveUserId(entity.id)
        return AppResult.Success(entity.toDomain())
    }

    override suspend fun register(name: String, email: String, password: String): AppResult<User> {
        catalogSeeder.ensureSeeded()
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        if (cleanName.length < 2) return AppResult.Error("Nama minimal 2 karakter.")
        if (!EMAIL_REGEX.matches(cleanEmail)) return AppResult.Error("Format email tidak valid.")
        if (password.length < 6) return AppResult.Error("Password minimal 6 karakter.")
        if (userDao.getByEmail(cleanEmail) != null) {
            return AppResult.Error("Email sudah terdaftar. Silakan login.")
        }

        val userId = "user_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()
        val entity = UserEntity(
            id = userId,
            name = cleanName,
            email = cleanEmail,
            passwordHash = HashUtil.hashPassword(password, cleanEmail),
            level = 1,
            currentXp = 0,
            points = 0,
            isDemoAccount = false,
            createdAt = now
        )
        userDao.upsert(entity)
        seedUserDefaults(userId = userId, now = now)
        sessionPreferences.setActiveUserId(userId)
        sessionPreferences.setLastChapterId(1)
        return AppResult.Success(entity.toDomain())
    }

    private suspend fun seedUserDefaults(userId: String, now: Long) {
        chapterDao.upsertAll(AppSeedData.defaultProgressForNewUser(userId, now = now))
        inventoryDao.upsertUserItems(AppSeedData.defaultItemsForNewUser(userId))
        achievementDao.upsertUserAchievements(AppSeedData.defaultAchievementsForUser(userId))
    }

    override suspend fun requestPasswordResetOtp(email: String): AppResult<String> {
        val cleanEmail = email.trim().lowercase()
        val entity = userDao.getByEmail(cleanEmail)
            ?: return AppResult.Error("Email tidak ditemukan di terminal lokal.")
        val otp = HashUtil.generateOtp()
        pendingResetEmail = entity.email
        pendingResetOtp = otp
        return AppResult.Success(otp)
    }

    override suspend fun resetPassword(email: String, otp: String, newPassword: String): AppResult<Unit> {
        val cleanEmail = email.trim().lowercase()
        if (pendingResetEmail != cleanEmail || pendingResetOtp != otp.trim()) {
            return AppResult.Error("Kode OTP tidak sesuai.")
        }
        if (newPassword.length < 6) {
            return AppResult.Error("Password baru minimal 6 karakter.")
        }
        val entity = userDao.getByEmail(cleanEmail)
            ?: return AppResult.Error("Akun tidak ditemukan.")
        userDao.updatePasswordHash(entity.id, HashUtil.hashPassword(newPassword, cleanEmail))
        pendingResetEmail = null
        pendingResetOtp = null
        return AppResult.Success(Unit)
    }

    override suspend fun updateProfileName(userId: String, newName: String): AppResult<Unit> {
        val clean = newName.trim()
        if (clean.length < 2) return AppResult.Error("Nama minimal 2 karakter.")
        userDao.updateName(userId, clean)
        return AppResult.Success(Unit)
    }

    override suspend fun changePassword(userId: String, currentPassword: String, newPassword: String): AppResult<Unit> {
        val entity = userDao.getById(userId) ?: return AppResult.Error("Akun tidak ditemukan.")
        val currentHash = HashUtil.hashPassword(currentPassword, entity.email)
        if (entity.passwordHash != currentHash) {
            return AppResult.Error("Password saat ini tidak cocok.")
        }
        if (newPassword.length < 6) {
            return AppResult.Error("Password baru minimal 6 karakter.")
        }
        userDao.updatePasswordHash(userId, HashUtil.hashPassword(newPassword, entity.email))
        return AppResult.Success(Unit)
    }

    override suspend fun setHapticsEnabled(enabled: Boolean) = sessionPreferences.setHapticsEnabled(enabled)
    override suspend fun setSoundEnabled(enabled: Boolean) = sessionPreferences.setSoundEnabled(enabled)

    override suspend fun logout() {
        sessionPreferences.setActiveUserId(null)
    }

    private fun UserEntity.toDomain(): User = User(
        id = id,
        name = name,
        email = email,
        level = level,
        currentXp = currentXp,
        points = points,
        isDemoAccount = isDemoAccount
    )

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
