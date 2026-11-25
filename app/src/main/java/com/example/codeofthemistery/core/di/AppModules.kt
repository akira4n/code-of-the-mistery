package com.example.codeofthemistery.core.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.codeofthemistery.core.ui.TerminalSheetViewModel
import com.example.codeofthemistery.data.local.datastore.SessionPreferences
import com.example.codeofthemistery.data.local.db.AppDatabase
import com.example.codeofthemistery.data.local.seed.CatalogSeeder
import com.example.codeofthemistery.data.repository.AchievementRepositoryImpl
import com.example.codeofthemistery.data.repository.AuthRepositoryImpl
import com.example.codeofthemistery.data.repository.ChapterRepositoryImpl
import com.example.codeofthemistery.data.repository.InventoryRepositoryImpl
import com.example.codeofthemistery.data.repository.RedeemRepositoryImpl
import com.example.codeofthemistery.domain.evaluator.MiniPythonEvaluator
import com.example.codeofthemistery.domain.repository.AchievementRepository
import com.example.codeofthemistery.domain.repository.AuthRepository
import com.example.codeofthemistery.domain.repository.ChapterRepository
import com.example.codeofthemistery.domain.repository.InventoryRepository
import com.example.codeofthemistery.domain.repository.RedeemRepository
import com.example.codeofthemistery.feature.achievement.AchievementViewModel
import com.example.codeofthemistery.feature.auth.AuthViewModel
import com.example.codeofthemistery.feature.battle.GameplayViewModel
import com.example.codeofthemistery.feature.config.ConfigViewModel
import com.example.codeofthemistery.feature.home.HomeViewModel
import com.example.codeofthemistery.feature.items.ItemsViewModel
import com.example.codeofthemistery.feature.play.PlayViewModel
import com.example.codeofthemistery.feature.redeem.RedeemViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE chapter_progress ADD COLUMN lastAlignmentChoice TEXT")
    }
}

val dataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "code_of_the_mystery.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().chapterDao() }
    single { get<AppDatabase>().inventoryDao() }
    single { get<AppDatabase>().redeemDao() }
    single { get<AppDatabase>().achievementDao() }

    single { SessionPreferences(androidContext()) }
    single { CatalogSeeder(get(), get(), get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<ChapterRepository> { ChapterRepositoryImpl(get(), get(), get(), get()) }
    single<InventoryRepository> { InventoryRepositoryImpl(get(), get()) }
    single<RedeemRepository> { RedeemRepositoryImpl(get(), get(), get()) }
    single<AchievementRepository> { AchievementRepositoryImpl(get(), get(), get(), get()) }
}

val domainModule = module {
    single { MiniPythonEvaluator() }
}

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { TerminalSheetViewModel(get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { PlayViewModel(get(), get()) }
    viewModel { ItemsViewModel(get(), get()) }
    viewModel { RedeemViewModel(get(), get(), get()) }
    viewModel { AchievementViewModel(get(), get()) }
    viewModel { ConfigViewModel(get()) }
    viewModel { (chapterId: Int) ->
        GameplayViewModel(chapterId, get(), get(), get(), get(), get())
    }
}
