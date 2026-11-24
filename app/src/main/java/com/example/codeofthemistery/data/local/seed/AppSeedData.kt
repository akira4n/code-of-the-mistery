package com.example.codeofthemistery.data.local.seed

import com.example.codeofthemistery.core.util.HashUtil
import com.example.codeofthemistery.data.local.entity.AchievementEntity
import com.example.codeofthemistery.data.local.entity.ChapterProgressEntity
import com.example.codeofthemistery.data.local.entity.ItemEntity
import com.example.codeofthemistery.data.local.entity.RewardCatalogEntity
import com.example.codeofthemistery.data.local.entity.UserAchievementEntity
import com.example.codeofthemistery.data.local.entity.UserEntity
import com.example.codeofthemistery.data.local.entity.UserItemEntity
import com.example.codeofthemistery.domain.model.BackgroundKey
import com.example.codeofthemistery.domain.model.ChapterScript
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ClueDefinition
import com.example.codeofthemistery.domain.model.CodeValidationRule
import com.example.codeofthemistery.domain.model.EnemyAnimState
import com.example.codeofthemistery.domain.model.EnemyType
import com.example.codeofthemistery.domain.model.HeroAnimState
import com.example.codeofthemistery.domain.model.SceneStep

/**
 * Provides initial catalogs, chapter scripts (Ch. 1–3 playable, Ch. 4–7 locked roadmap),
 * and user/demo account seeding data.
 */
object AppSeedData {

    const val DEMO_USER_ID = "user_demo_tester"
    const val DEMO_EMAIL = "demo@codeofthemistery.app"

    data class ChapterMeta(
        val id: Int,
        val title: String,
        val subtitle: String,
        val topicLabel: String,
        val isPlayable: Boolean,
        val rewardXp: Int,
        val rewardPoints: Int
    )

    val chaptersMeta = listOf(
        ChapterMeta(
            id = 1,
            title = "Chapter I : Mastering Logic, Surviving Syntax",
            subtitle = "Perkenalan kepada bahasa pemrograman Python",
            topicLabel = "Variabel & Tipe Data",
            isPlayable = true,
            rewardXp = 500,
            rewardPoints = 500
        ),
        ChapterMeta(
            id = 2,
            title = "Chapter II : Branching Protocol",
            subtitle = "Menembus firewall menggunakan percabangan if/elif/else",
            topicLabel = "Kondisi & Logika",
            isPlayable = true,
            rewardXp = 650,
            rewardPoints = 800
        ),
        ChapterMeta(
            id = 3,
            title = "Chapter III : Looping the Anomaly",
            subtitle = "Boss Battle melawan Armored Golem dengan perulangan & fungsi",
            topicLabel = "Loop & Fungsi Boss",
            isPlayable = true,
            rewardXp = 900,
            rewardPoints = 1200
        ),
        ChapterMeta(
            id = 4,
            title = "Chapter IV : Data Vault Structures",
            subtitle = "Mengelola List dan Dictionary untuk inventaris sistem",
            topicLabel = "List & Dictionary",
            isPlayable = false,
            rewardXp = 1000,
            rewardPoints = 1500
        ),
        ChapterMeta(
            id = 5,
            title = "Chapter V : Modular Echoes",
            subtitle = "Memecah kode besar menjadi modul yang bersih",
            topicLabel = "Functions & Modules",
            isPlayable = false,
            rewardXp = 1200,
            rewardPoints = 1800
        ),
        ChapterMeta(
            id = 6,
            title = "Chapter VI : Object Awakening",
            subtitle = "Membangun Blueprint Class dan Object di dunia nyata",
            topicLabel = "OOP Basics",
            isPlayable = false,
            rewardXp = 1500,
            rewardPoints = 2200
        ),
        ChapterMeta(
            id = 7,
            title = "Chapter VII : The Final Kernel",
            subtitle = "Pertarungan puncak algoritma menghadapi sumber Runtime Error",
            topicLabel = "Algorithm Boss",
            isPlayable = false,
            rewardXp = 2500,
            rewardPoints = 3500
        )
    )

    val itemsCatalog = listOf(
        ItemEntity(
            id = "item_laptop",
            name = "Laptop",
            description = "Terminal portabel utama The Mystery Coder. Membuka editor MAIN.PY dan menyediakan Hint saat menghadapi bug.",
            statLabel = "DEFAULT ITEM",
            type = "EQUIPMENT",
            rarity = "COMMON",
            canUnequip = false
        ),
        ItemEntity(
            id = "item_firewall_shield",
            name = "Firewall Shield",
            description = "Perisai enkripsi pasif yang menahan kesalahan logika pertama di setiap chapter dan meredam serangan Boss.",
            statLabel = "Def +120",
            type = "EQUIPMENT",
            rarity = "RARE",
            canUnequip = true
        ),
        ItemEntity(
            id = "item_syntax_potion",
            name = "Syntax Potion",
            description = "Restore core vitality. Cairan abstraksi tingkat tinggi yang menambal runtime error dan memulihkan +4 HP saat battle.",
            statLabel = "HP +4",
            type = "CONSUMABLE",
            rarity = "COMMON",
            canUnequip = true
        ),
        ItemEntity(
            id = "item_debugger_visor",
            name = "Debugger Visor",
            description = "Kacamata pemindai sintaks dari Loot Box langka. Memberikan tambahan +15% bonus XP saat menyelesaikan chapter.",
            statLabel = "XP +15%",
            type = "EQUIPMENT",
            rarity = "RARE",
            canUnequip = true
        ),
        ItemEntity(
            id = "item_overclock_core",
            name = "Overclock Core",
            description = "Inti prosesor epik dari Boss Battle sempurna. Memberikan bonus +250 Poin tambahan tiap misi selesai.",
            statLabel = "PTS +250",
            type = "EQUIPMENT",
            rarity = "EPIC",
            canUnequip = true
        )
    )

    val achievementsCatalog = listOf(
        AchievementEntity(
            id = "ach_var_1",
            title = "Variable Explorer 1",
            description = "Berhasil mengeksekusi 2 tantangan kode Python di dalam editor MAIN.PY.",
            rewardXp = 150,
            targetCount = 2,
            iconKey = "explore"
        ),
        AchievementEntity(
            id = "ach_var_2",
            title = "Variable Explorer 2",
            description = "Menyelesaikan 4 tantangan kode Python dari Chapter 1 hingga 3.",
            rewardXp = 250,
            targetCount = 4,
            iconKey = "code"
        ),
        AchievementEntity(
            id = "ach_bug_hunter",
            title = "Bug Hunter",
            description = "Menyelesaikan sebuah chapter dengan sisa HP minimal 5/6.",
            rewardXp = 200,
            targetCount = 1,
            iconKey = "bug"
        ),
        AchievementEntity(
            id = "ach_algo_wizard",
            title = "Algorithm Wizard",
            description = "Menaklukkan tantangan percabangan & loop mulai dari Chapter 2.",
            rewardXp = 300,
            targetCount = 2,
            iconKey = "wizard"
        ),
        AchievementEntity(
            id = "ach_first_redeem",
            title = "Reward Hunter",
            description = "Menukarkan poin pertama kali di Milestone Reward Center.",
            rewardXp = 100,
            targetCount = 1,
            iconKey = "redeem"
        )
    )

    val rewardsCatalog = listOf(
        RewardCatalogEntity(
            id = "rew_tokopedia",
            title = "Tokopedia Rp50k Voucher",
            partner = "tokopedia",
            description = "Belanja apapun di Tokopedia dengan voucher ini.",
            costPoints = 7000,
            stockRemaining = null
        ),
        RewardCatalogEntity(
            id = "rew_shopee",
            title = "Shopee Rp70k Voucher",
            partner = "shopee",
            description = "Belanja apapun di Shopee dengan voucher ini.",
            costPoints = 10000,
            stockRemaining = null
        ),
        RewardCatalogEntity(
            id = "rew_dana",
            title = "Dana Rp50k",
            partner = "dana",
            description = "Saldo e-wallet Dana langsung untuk kebutuhan harianmu.",
            costPoints = 10000,
            stockRemaining = null
        ),
        RewardCatalogEntity(
            id = "rew_copilot",
            title = "GitHub Copilot (1 Bulan)",
            partner = "copilot",
            description = "Biarkan AI membantu generate boilerplate, kamu fokus di arsitektur & debugging.",
            costPoints = 40000,
            stockRemaining = 8
        ),
        RewardCatalogEntity(
            id = "rew_chatgpt",
            title = "ChatGPT Plus (1 Bulan)",
            partner = "chatgpt",
            description = "Akses model cepat untuk belajar, review kode, dan riset sistem.",
            costPoints = 55000,
            stockRemaining = 4
        )
    )

    fun createDemoUser(now: Long = System.currentTimeMillis()): UserEntity = UserEntity(
        id = DEMO_USER_ID,
        name = "Player Name",
        email = DEMO_EMAIL,
        passwordHash = HashUtil.hashPassword("demo123", DEMO_EMAIL),
        level = 2,
        currentXp = 697,
        points = 15000,
        isDemoAccount = true,
        createdAt = now
    )

    fun defaultProgressForNewUser(userId: String, unlockAllPlayable: Boolean = false, now: Long = System.currentTimeMillis()): List<ChapterProgressEntity> =
        chaptersMeta.map { ch ->
            val status = when {
                ch.id == 1 -> ChapterStatus.UNLOCKED.name
                unlockAllPlayable && ch.isPlayable -> ChapterStatus.UNLOCKED.name
                else -> ChapterStatus.LOCKED.name
            }
            ChapterProgressEntity(
                userId = userId,
                chapterId = ch.id,
                status = status,
                bestHpRemaining = 0,
                questionsAnswered = 0,
                updatedAt = now
            )
        }

    fun defaultItemsForNewUser(userId: String, isDemo: Boolean): List<UserItemEntity> = listOf(
        UserItemEntity(userId = userId, itemId = "item_laptop", quantity = 1, equippedSlot = 1, isUnlocked = true),
        UserItemEntity(
            userId = userId,
            itemId = "item_firewall_shield",
            quantity = if (isDemo) 1 else 0,
            equippedSlot = if (isDemo) 2 else null,
            isUnlocked = isDemo
        ),
        UserItemEntity(
            userId = userId,
            itemId = "item_syntax_potion",
            quantity = if (isDemo) 3 else 1,
            equippedSlot = if (isDemo) 3 else null,
            isUnlocked = true
        ),
        UserItemEntity(userId = userId, itemId = "item_debugger_visor", quantity = 0, equippedSlot = null, isUnlocked = false),
        UserItemEntity(userId = userId, itemId = "item_overclock_core", quantity = 0, equippedSlot = null, isUnlocked = false)
    )

    fun defaultAchievementsForUser(userId: String): List<UserAchievementEntity> =
        achievementsCatalog.map { ach ->
            UserAchievementEntity(
                userId = userId,
                achievementId = ach.id,
                currentCount = 0,
                isUnlocked = false,
                unlockedAt = null
            )
        }

    fun getChapterScript(chapterId: Int): ChapterScript = when (chapterId) {
        1 -> chapter1Script
        2 -> chapter2Script
        3 -> chapter3Script
        else -> chapter1Script
    }

    private val chapter1Script = ChapterScript(
        chapterId = 1,
        title = "Chapter 1",
        subtitle = "Mastering Logic, Surviving Syntax",
        rewardXp = 500,
        rewardPoints = 500,
        steps = listOf(
            SceneStep.NarrationStep(
                id = "c1_s1",
                speaker = "NARATOR",
                text = "The Great Runtime Error !! Pada tahun 2026, langit retak seperti layar crash, memperlihatkan aliran kode hexadecimal. Hukum fisika kolaps akibat Logic Error di kernel bumi.",
                backgroundKey = BackgroundKey.STORY_ERROR,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.NONE
            ),
            SceneStep.NarrationStep(
                id = "c1_s2",
                speaker = "???",
                text = "AAH, DIMANA AKU SEKARANG!? Di depan Dokja Supermarket, udara berbau ozon dan kode Python melayang di udara...",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.RUN,
                enemyType = EnemyType.NONE
            ),
            SceneStep.NarrationStep(
                id = "c1_s3",
                speaker = "SYSTEM",
                text = "Kamu masih <undefined>. Kamu bisa men-assign nama kamu di variabel agar sistem mengenalimu sebelum monster anomali mendeteksi keberadaanmu.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.NONE,
                systemBubbleText = "Kamu masih <undefined>, assign nama kamu di variabel agar sistem mengenalinya!",
                unlockClue = ClueDefinition(
                    id = "clue_ch1_vars",
                    chapterId = 1,
                    title = "Variabel & Tipe Data Dasar Python",
                    content = "Di Python, variabel dibuat dengan: nama_variabel = nilai.\nTipe data dasar: string (\"teks\"), integer (6), float (3.5), dan boolean (True/False)."
                )
            ),
            SceneStep.CodeBattleStep(
                id = "c1_c1",
                title = "Inisialisasi Identitas Coder",
                lessonSummary = "Di Python, kamu bisa membuat variabel dengan cara:\nnama_variabel = \"value\"\nTipe data dasar: string, integer, float, boolean.",
                starterCode = """
                    # Ini adalah komentar, baris ini tidak akan dieksekusi
                    nama = "Dokja"
                    HP = 6
                    skill = "Programming"
                    print("Nama:", nama)
                    print("HP:", HP)
                    print("Skill:", skill)
                """.trimIndent(),
                hintText = "Pastikan variabel nama berisi string tidak kosong (contoh: \"Dokja\") dan ketiga baris print() tetap ada.",
                validationRule = CodeValidationRule(
                    nonEmptyStringVars = listOf("nama", "skill"),
                    expectedVars = mapOf("HP" to 6),
                    minStdoutLines = 3
                ),
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.NONE,
                isBoss = false
            ),
            SceneStep.NarrationStep(
                id = "c1_s4",
                speaker = "SYSTEM ALERT",
                text = "Golem Batu muncul dari retakan aspal Dokja Supermarket! Gunakan pengetahuan tipe data & kondisi logika untuk menembus pertahanannya!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.GOLEM,
                enemyAnim = EnemyAnimState.IDLE,
                systemBubbleText = "<MAIN SCENARIO #1> Golem Menghadang! Jawab analisis tipe data dengan tepat!"
            ),
            SceneStep.QuizBattleStep(
                id = "c1_q1",
                prompt = "Pada Python, jika kamu menulis `shields_active = True`, tipe data apakah yang dimiliki variabel `shields_active`?",
                options = listOf("String", "Integer", "Boolean", "Float"),
                correctIndex = 2,
                explanation = "`True` atau `False` (huruf awal kapital di Python) adalah tipe data Boolean.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM,
                isBoss = false
            ),
            SceneStep.CodeBattleStep(
                id = "c1_c2",
                title = "Decryption Battle vs Golem",
                lessonSummary = "Gunakan kondisi `if energy > 50:` untuk menurunkan perisai Golem dan isi `total_power = 100`.",
                starterCode = """
                    # Player input values
                    energy = 70
                    shields_active = True

                    # Turunkan perisai jika energy > 50
                    if energy > 50:
                        shields_active = False
                        total_power = 100

                    print("Power:", total_power)
                """.trimIndent(),
                hintText = "Gunakan keyword `if`, ubah `shields_active = False`, dan set `total_power = 100` di dalam blok if.",
                validationRule = CodeValidationRule(
                    requiredKeywords = listOf("if"),
                    expectedVars = mapOf("shields_active" to false, "total_power" to 100),
                    expectedStdoutContains = listOf("Power:", "100")
                ),
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM,
                isBoss = false
            ),
            SceneStep.LootClaimStep(
                id = "c1_loot",
                message = "Golem berhasil dilumpuhkan! Ketuk CLAIM >> untuk mengambil item pertahanan dari reruntuhan.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM
            )
        )
    )

    private val chapter2Script = ChapterScript(
        chapterId = 2,
        title = "Chapter 2",
        subtitle = "Branching Protocol, Break the Firewall",
        rewardXp = 650,
        rewardPoints = 800,
        steps = listOf(
            SceneStep.NarrationStep(
                id = "c2_s1",
                speaker = "SYSTEM",
                text = "Memasuki Sektor Stasiun Bawah Tanah. Rogue Drone berpatroli memindai setiap sinyal dengan logika percabangan.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.RUN,
                enemyType = EnemyType.DRONE,
                enemyAnim = EnemyAnimState.ATTACK,
                unlockClue = ClueDefinition(
                    id = "clue_ch2_if",
                    chapterId = 2,
                    title = "Percabangan if / elif / else",
                    content = "Gunakan `if kondisi:`, `elif kondisi_lain:`, dan `else:` dengan indentasi 4 spasi.\nOperator logika: `and`, `or`, `not`."
                )
            ),
            SceneStep.QuizBattleStep(
                id = "c2_q1",
                prompt = "Manakah ekspresi Python yang bernilai `True` jika `level` = 3 dan `has_key` = True?",
                options = listOf(
                    "level > 5 and has_key",
                    "level >= 3 and has_key",
                    "level == 2 or not has_key",
                    "level < 3"
                ),
                correctIndex = 1,
                explanation = "Karena `level >= 3` (True) dan `has_key` (True), maka `True and True` menghasilkan `True`.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.DRONE,
                isBoss = false
            ),
            SceneStep.CodeBattleStep(
                id = "c2_c1",
                title = "Overload Rogue Drone",
                lessonSummary = "Tentukan `mode` dan `damage` berdasarkan jarak `distance = 12` menggunakan `if / elif / else`.",
                starterCode = """
                    distance = 12
                    mode = "WAIT"
                    damage = 0

                    if distance <= 15:
                        mode = "STRIKE"
                        damage = 80
                    else:
                        mode = "SCAN"
                        damage = 20

                    print(mode, damage)
                """.trimIndent(),
                hintText = "Saat `distance <= 15`, pastikan `mode = \"STRIKE\"` dan `damage = 80`.",
                validationRule = CodeValidationRule(
                    requiredKeywords = listOf("if", "else"),
                    expectedVars = mapOf("mode" to "STRIKE", "damage" to 80),
                    expectedStdoutContains = listOf("STRIKE", "80")
                ),
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.DRONE,
                isBoss = false
            ),
            SceneStep.LootClaimStep(
                id = "c2_loot",
                message = "Rogue Drone hancur berkeping-keping! Klaim jarahan energi dari inti drone.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.DRONE
            )
        )
    )

    private val chapter3Script = ChapterScript(
        chapterId = 3,
        title = "Chapter 3",
        subtitle = "Looping the Anomaly (Boss Battle)",
        rewardXp = 900,
        rewardPoints = 1200,
        steps = listOf(
            SceneStep.NarrationStep(
                id = "c3_s1",
                speaker = "BOSS ALERT",
                text = "Armored Core Golem menjaga Gerbang Kernel! Satu serangan biasa tidak cukup — kamu perlu serangan berulang (`for` loop) dan fungsi (`def`)!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.GOLEM,
                enemyAnim = EnemyAnimState.IDLE,
                systemBubbleText = "<BOSS BATTLE> Kumpulkan total_hit menggunakan loop & fungsi!",
                unlockClue = ClueDefinition(
                    id = "clue_ch3_loop",
                    chapterId = 3,
                    title = "Perulangan for & Fungsi def",
                    content = "`for i in range(5):` mengulang blok kode 5 kali (i = 0..4).\n`def hitung(x): return x * 2` mendefinisikan fungsi yang mengembalikan nilai."
                )
            ),
            SceneStep.QuizBattleStep(
                id = "c3_q1",
                prompt = "Berapa kali perulangan `for i in range(1, 5):` akan dijalankan oleh Python?",
                options = listOf("3 kali", "4 kali", "5 kali", "6 kali"),
                correctIndex = 1,
                explanation = "`range(1, 5)` menghasilkan angka 1, 2, 3, 4 (berhenti sebelum 5), jadi total 4 kali.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM,
                isBoss = true
            ),
            SceneStep.CodeBattleStep(
                id = "c3_c1",
                title = "Final Boss: Armored Core Golem",
                lessonSummary = "Buat fungsi `calc_combo(base)` yang mengembalikan `base * 2`, lalu jumlahkan ke `total_power` selama 3 kali loop `for`.",
                starterCode = """
                    def calc_combo(base):
                        return base * 2

                    total_power = 0
                    for i in range(3):
                        total_power += calc_combo(25)

                    print("Boss Damage:", total_power)
                """.trimIndent(),
                hintText = "Dengan `calc_combo(25)` mengembalikan 50 dan loop 3 kali, `total_power` akan menjadi `150`.",
                validationRule = CodeValidationRule(
                    requiredKeywords = listOf("def", "for"),
                    expectedVars = mapOf("total_power" to 150),
                    expectedStdoutContains = listOf("Boss Damage:", "150")
                ),
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM,
                isBoss = true
            ),
            SceneStep.LootClaimStep(
                id = "c3_loot",
                message = "BOSS KALAH! Gerbang Kernel terbuka! Klaim peti Boss Loot sekarang.",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM
            )
        )
    )
}
