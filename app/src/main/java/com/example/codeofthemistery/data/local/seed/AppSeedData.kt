package com.example.codeofthemistery.data.local.seed

import com.example.codeofthemistery.data.local.entity.AchievementEntity
import com.example.codeofthemistery.data.local.entity.ChapterProgressEntity
import com.example.codeofthemistery.data.local.entity.ItemEntity
import com.example.codeofthemistery.data.local.entity.RewardCatalogEntity
import com.example.codeofthemistery.data.local.entity.UserAchievementEntity
import com.example.codeofthemistery.data.local.entity.UserEntity
import com.example.codeofthemistery.data.local.entity.UserItemEntity
import com.example.codeofthemistery.domain.model.AlignmentChoice
import com.example.codeofthemistery.domain.model.BackgroundKey
import com.example.codeofthemistery.domain.model.BranchOption
import com.example.codeofthemistery.domain.model.ChapterScript
import com.example.codeofthemistery.domain.model.ChapterStatus
import com.example.codeofthemistery.domain.model.ClueDefinition
import com.example.codeofthemistery.domain.model.CodeValidationRule
import com.example.codeofthemistery.domain.model.EnemyAnimState
import com.example.codeofthemistery.domain.model.EnemyType
import com.example.codeofthemistery.domain.model.HelpMateriInfo
import com.example.codeofthemistery.domain.model.HeroAnimState
import com.example.codeofthemistery.domain.model.PortraitKey
import com.example.codeofthemistery.domain.model.SceneStep

/**
 * Provides initial catalogs, chapter scripts (Ch. 1–3 playable, Ch. 4–7 locked roadmap),
 * and new user default seeding data.
 */
object AppSeedData {

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

    fun defaultProgressForNewUser(userId: String, now: Long = System.currentTimeMillis()): List<ChapterProgressEntity> =
        chaptersMeta.map { ch ->
            val status = if (ch.id == 1) ChapterStatus.UNLOCKED.name else ChapterStatus.LOCKED.name
            ChapterProgressEntity(
                userId = userId,
                chapterId = ch.id,
                status = status,
                bestHpRemaining = 0,
                questionsAnswered = 0,
                updatedAt = now
            )
        }

    fun defaultItemsForNewUser(userId: String): List<UserItemEntity> = listOf(
        UserItemEntity(userId = userId, itemId = "item_laptop", quantity = 1, equippedSlot = 1, isUnlocked = true),
        UserItemEntity(userId = userId, itemId = "item_firewall_shield", quantity = 0, equippedSlot = null, isUnlocked = false),
        UserItemEntity(userId = userId, itemId = "item_syntax_potion", quantity = 1, equippedSlot = null, isUnlocked = true),
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
        cliffhangerText = "Di kejauhan, gerbang kereta bawah tanah Subway Station 05 berbunyi nyaring. Lampu alarm merah menyala — ratusan Rogue Drone mengudara, sementara siluet Vex terlihat menuruni eskalator gelap...",
        steps = listOf(
            // ACT 1: ANOMALY OPENING & MICRO-LESSON 1
            SceneStep.NarrationStep(
                id = "c1_s1",
                speaker = "NARATOR",
                text = "The Great Runtime Error! Pada tahun 2026, langit terbelah bagai monitor yang gagal merender realitas, menumpahkan aliran kode hexadecimal 0xDEADCODE ke seluruh penjuru kota.",
                backgroundKey = BackgroundKey.STORY_ERROR,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.NONE
            ),
            SceneStep.NarrationStep(
                id = "c1_s2",
                speaker = "PROTAGONIS",
                portrait = PortraitKey.HERO,
                text = "AAH, DIMANA AKU SEKARANG!? Kenapa papan neon Dokja Supermarket berkedip menuliskan ERROR_LOGIC_KERNEL_FAILURE?!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.RUN,
                enemyType = EnemyType.NONE
            ),
            SceneStep.NarrationStep(
                id = "c1_s3_aria",
                speaker = "ARIA",
                portrait = PortraitKey.ARIA,
                showAriaCompanion = true,
                text = "Sinkronisasi berhasil! Halo, aku ARIA, asisten runtime-mu. Status identitasmu di jaringan saat ini masih <undefined> — sistem keamanan akan menganggapmu bug jika dibiarkan!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.NONE,
                systemBubbleText = "Peringatan Sistem: Entitas <undefined> terdeteksi di Distrik 01!"
            ),
            SceneStep.NarrationStep(
                id = "c1_s4_lesson",
                speaker = "ARIA",
                portrait = PortraitKey.ARIA,
                showAriaCompanion = true,
                microLessonBadge = "MATERI PYTHON • VARIABEL & TIPE DATA",
                text = "Di Python, variabel dibuat dengan format nama = nilai. Gunakan tanda kutip untuk teks/String (\"Coder\"), angka murni untuk Integer (6), huruf awal kapital untuk Boolean (True/False), dan # untuk komentar catatan!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.NONE,
                unlockClue = ClueDefinition(
                    id = "clue_ch1_vars",
                    chapterId = 1,
                    title = "Variabel & Tipe Data Dasar Python",
                    content = "Format: nama_variabel = nilai\n• str: teks bertanda kutip, mis. \"Coder\"\n• int: bilangan bulat tanpa kutip, mis. 6\n• float: desimal, mis. 3.5\n• bool: True atau False (awal kapital)\n• #: komentar (diabaikan saat eksekusi)"
                )
            ),
            SceneStep.CodeBattleStep(
                id = "c1_c1",
                title = "Inisialisasi Identitas Coder (MAIN.PY)",
                lessonSummary = "Lengkapi slot ____ agar variabel nama bertipe String, HP bernilai angka 6 (Integer), lalu jalankan terminal untuk mendaftarkan identitasmu.",
                starterCode = """
                    # Inisialisasi identitas The Mystery Coder
                    nama = ____
                    HP = ____
                    aktif = True

                    print("Coder:", nama)
                    print("HP:", HP)
                """.trimIndent(),
                codeChips = listOf("\"Coder\"", "6", "\"6\"", "True", "undefined"),
                hintText = "Isi slot pertama dengan teks berkutip seperti \"Coder\" atau namamu, lalu isi HP dengan angka 6 tanpa kutip.",
                helpMateri = HelpMateriInfo(
                    title = "Variabel, String, Integer & print()",
                    summary = "String wajib diapit tanda kutip ganda/tunggal, sedangkan angka bulat (Integer) ditulis langsung tanpa kutip. Gunakan print(...) untuk mencetak hasil ke terminal.",
                    codeExample = "nama = \"Dokja\"\nHP = 6\nprint(\"Coder:\", nama)"
                ),
                validationRule = CodeValidationRule(
                    nonEmptyStringVars = listOf("nama"),
                    expectedVars = mapOf("HP" to 6),
                    minStdoutLines = 2
                ),
                showAriaCompanion = true,
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.NONE,
                isBoss = false
            ),

            // ACT 2: BRANCHING ENCOUNTER WITH VEX ("NULL")
            SceneStep.NarrationStep(
                id = "c1_s5_vex",
                speaker = "VEX (\"Null\")",
                portrait = PortraitKey.VEX,
                showAriaCompanion = true,
                text = "Oh? Ada orang lain yang laptop-nya ikut bangkit? Jangan buang waktu menambal kota penuh spaghetti code ini. Satu-satunya jalan adalah menghapus kernel dan me-reset dunia dari nol (Purge)!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.VEX,
                enemyAnim = EnemyAnimState.IDLE,
                systemBubbleText = "Pertemuan Rival: Vex (\"Null\") menunggumu di jalanan."
            ),
            SceneStep.BranchChoiceStep(
                id = "c1_branch_vex",
                speaker = "VEX (\"Null\")",
                portrait = PortraitKey.VEX,
                showAriaCompanion = true,
                prompt = "ARIA memperingatkan bahwa hard reset akan menghapus seluruh ingatan warga di buffer kota. Bagaimana sikapmu terhadap Vex?",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.VEX,
                options = listOf(
                    BranchOption(
                        id = "opt_patch_defend",
                        label = "Aku akan memperbaiki bug ini tanpa mengorbankan siapa pun!",
                        alignment = AlignmentChoice.PATCH,
                        replySpeaker = "ARIA",
                        replyPortrait = PortraitKey.ARIA,
                        replyText = "Pilihan berani! Saat Vex mundur, aku berhasil memindai jejak kode miliknya — kelemahan armor Stone Golem dan Secret Clue tersimpan di Codex!",
                        consequenceBadge = "+1 PATCH • SECRET CLUE • KELEMAHAN BOSS",
                        unlockClue = ClueDefinition(
                            id = "clue_ch1_secret_comments",
                            chapterId = 1,
                            title = "[Secret Clue] Jejak Kernel & Overdrive 150",
                            content = "Catatan tersembunyi di komentar (#): Arsitek sistem mengizinkan total_power ditingkatkan ke 150 (Overdrive) saat menghadapi Boss Golem di Distrik 01!"
                        ),
                        weakensBoss = true
                    ),
                    BranchOption(
                        id = "opt_purge_power",
                        label = "Tunjukkan di mana Null Kernel berada, aku butuh kekuatan cepat!",
                        alignment = AlignmentChoice.PURGE,
                        replySpeaker = "VEX (\"Null\")",
                        replyPortrait = PortraitKey.VEX,
                        replyText = "Hah, akhirnya seseorang yang realistis. Ambil Overclock Fragment ini (+15 FORCE). Tunjukkan kalau kau sanggup bertahan melawan penjaga Distrik 01!",
                        consequenceBadge = "+1 PURGE • +15 FORCE AWAL",
                        bonusForce = 15,
                        weakensBoss = false
                    ),
                    BranchOption(
                        id = "opt_patch_analyze",
                        label = "Berikan satu bukti kenapa kode dunia ini tidak bisa di-debug.",
                        alignment = AlignmentChoice.PATCH,
                        replySpeaker = "VEX (\"Null\")",
                        replyPortrait = PortraitKey.VEX,
                        replyText = "Keras kepala... Baca saja log komentar tersembunyi itu kalau kau memang bisa membuktikan teorimu. Sampai jumpa di sektor berikutnya!",
                        consequenceBadge = "+1 PATCH • SECRET CLUE • KELEMAHAN BOSS",
                        unlockClue = ClueDefinition(
                            id = "clue_ch1_secret_comments",
                            chapterId = 1,
                            title = "[Secret Clue] Jejak Kernel & Overdrive 150",
                            content = "Catatan tersembunyi di komentar (#): Arsitek sistem mengizinkan total_power ditingkatkan ke 150 (Overdrive) saat menghadapi Boss Golem di Distrik 01!"
                        ),
                        weakensBoss = true
                    )
                )
            ),

            // ACT 3: PRE-BATTLE MICRO-LESSON & SKIRMISH VS SYNTAX SLIME (60 HP)
            SceneStep.NarrationStep(
                id = "c1_s6_slime_lesson",
                speaker = "ARIA",
                portrait = PortraitKey.ARIA,
                showAriaCompanion = true,
                microLessonBadge = "MATERI PYTHON • CASE-SENSITIVE & PEMDAS",
                text = "Awas, Syntax Slime muncul! Sebelum menyerang, ingat dua aturan penting Python: (1) Boolean WAJIB huruf awal kapital (`True`/`False`, bukan `true`/`false`). (2) Perkalian `*` selalu dihitung lebih dulu sebelum penjumlahan `+`!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.SLIME,
                enemyAnim = EnemyAnimState.IDLE,
                enemyMaxHp = 60,
                enemyName = "Syntax Slime",
                systemBubbleText = "Anomali Mendekat: Syntax Slime (60 HP) muncul dari selokan!"
            ),
            SceneStep.BugHuntStep(
                id = "c1_bughunt_slime",
                title = "Bug Hunt • Hancurkan Pertahanan Slime",
                lessonSummary = "Temukan dan ketuk 1 baris kode Python yang menyebabkan error agar tebasan logika mengenai Syntax Slime (-35 HP)!",
                helpMateri = HelpMateriInfo(
                    title = "Aturan Huruf Kapital (Case-Sensitive)",
                    summary = "Python membedakan huruf besar dan kecil. Nilai logika Boolean hanya sah bila ditulis True atau False dengan huruf awal kapital.",
                    codeExample = "is_ready = True   # Benar\nis_ready = true   # NameError!"
                ),
                codeLines = listOf(
                    "# Status awal pertahanan",
                    "shield_level = 2",
                    "hero_name = \"Mystery Coder\"",
                    "is_ready = true",
                    "print(hero_name, shield_level)"
                ),
                bugLineIndex = 3,
                explanation = "Baris 4 salah karena menulis `true` huruf kecil. Di Python, Boolean wajib diawali huruf kapital: `True`!",
                damageToEnemy = 35,
                enemyMaxHp = 60,
                enemyName = "Syntax Slime",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.SLIME,
                showAriaCompanion = true,
                isBoss = false
            ),
            SceneStep.QuizBattleStep(
                id = "c1_predict_slime",
                title = "Predict Output • Finisher Slime",
                prompt = "Tebak hasil output terminal berikut untuk melepaskan tebasan penutup melawan Syntax Slime (-25 HP):",
                codeSnippet = "x = 10\ny = 3\nprint(x + y * 2)",
                options = listOf("26", "16", "20", "Error"),
                correctIndex = 1,
                explanation = "Sesuai prioritas operator (PEMDAS), perkalian `3 * 2` (= 6) dieksekusi lebih dulu sebelum `10 + 6` = `16`.",
                helpMateri = HelpMateriInfo(
                    title = "Prioritas Operator Matematika",
                    summary = "Perkalian (*) dan pembagian (/) selalu diproses terlebih dahulu dibanding penjumlahan (+) dan pengurangan (-), kecuali diberi tanda kurung ().",
                    codeExample = "print(10 + 3 * 2)    # 16\nprint((10 + 3) * 2)  # 26"
                ),
                damageToEnemy = 25,
                enemyMaxHp = 60,
                enemyName = "Syntax Slime",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.SLIME,
                showAriaCompanion = true,
                isBoss = false
            ),

            // ACT 4: BOSS BRIEFING & MULTI-PHASE BOSS BATTLE VS STONE GOLEM (120 HP)
            SceneStep.NarrationStep(
                id = "c1_s7_boss_lesson",
                speaker = "ARIA",
                portrait = PortraitKey.ARIA,
                showAriaCompanion = true,
                microLessonBadge = "MATERI BOSS • PERCABANGAN IF & OPERATOR",
                text = "Hati-hati! Boss Stone Golem memiliki perisai energi (`shields_active = True`). Gunakan pernyataan `if kondisi:` (akhiri dengan `:`, beri 4 spasi menjorok di baris dalamnya). Gunakan `>` untuk 'lebih dari' dan `==` untuk membandingkan kesamaan!",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                heroAnim = HeroAnimState.IDLE,
                enemyType = EnemyType.GOLEM,
                enemyAnim = EnemyAnimState.IDLE,
                enemyMaxHp = 120,
                enemyName = "Stone Golem",
                systemBubbleText = "<MAIN SCENARIO #1: DECRYPTION BATTLE> Musuh: Stone Golem (120 HP)",
                unlockClue = ClueDefinition(
                    id = "clue_ch1_ops_if",
                    chapterId = 1,
                    title = "Percabangan if & Operator Perbandingan",
                    content = "Struktur if:\nif kondisi:\n    aksi_menjorok_4_spasi\n\nOperator: > (lebih dari), < (kurang dari), == (sama dengan), != (tidak sama)."
                )
            ),
            SceneStep.CodeChipsStep(
                id = "c1_boss_phase1",
                title = "Boss Fase 1 • Retakkan Perisai Golem",
                lessonSummary = "Pilih 2 Code Chips untuk melengkapi syarat pengecekan energi terminal lebih dari 50 DAN perisai sedang aktif (-60 HP Boss).",
                helpMateri = HelpMateriInfo(
                    title = "Operator Perbandingan & and",
                    summary = "Gunakan `>` untuk mengecek lebih besar, dan `==` (dua sama dengan) untuk mengecek apakah dua nilai sama — jangan tertukar dengan `=` pengisian variabel!",
                    codeExample = "if energy > 50 and shields_active == True:\n    print(\"Perisai Terdeteksi!\")"
                ),
                templateSegments = listOf("if energy ", " 50 and shields_active ", " True:"),
                chipOptions = listOf(">", "==", "<", "=", ">=", "!="),
                expectedChipsInOrder = listOf(">", "=="),
                explanation = "Operator `>` mengecek apakah energy lebih dari 50, dan `==` membandingkan apakah shields_active bernilai True!",
                damageToEnemy = 60,
                enemyMaxHp = 120,
                enemyName = "Stone Golem",
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM,
                showAriaCompanion = true,
                isBoss = true
            ),
            SceneStep.CodeBattleStep(
                id = "c1_boss_phase2",
                title = "Boss Fase 2 • Decryption Finisher (MAIN.PY)",
                lessonSummary = "Perisai Golem sudah retak! Lengkapi 3 slot ____ pada blok if untuk menonaktifkan shields_active (False) dan mengatur total_power = 100 (atau 150 jika Secret Clue terbuka)!",
                starterCode = """
                    # Status energi & perisai Golem
                    energy = 70
                    shields_active = True
                    total_power = 0

                    # Turunkan perisai jika energy > 50
                    if energy ____ 50:
                        shields_active = ____
                        total_power = ____

                    print("Power:", total_power)
                """.trimIndent(),
                codeChips = listOf(">", "False", "100", "150", "True", "=="),
                hintText = "Isi kondisi dengan >, ubah shields_active = False, lalu isi total_power = 100 (atau 150 bila kamu membuka Secret Clue!).",
                helpMateri = HelpMateriInfo(
                    title = "Blok if dan Indentasi 4 Spasi",
                    summary = "Baris di bawah `if kondisi:` wajib menjorok (4 spasi) agar hanya berjalan ketika kondisi benar. Pastikan tidak ada slot `____` yang tertinggal.",
                    codeExample = "if energy > 50:\n    shields_active = False\n    total_power = 100"
                ),
                validationRule = CodeValidationRule(
                    requiredKeywords = listOf("if"),
                    expectedVars = mapOf("shields_active" to false),
                    allowedVarValues = mapOf("total_power" to listOf(100, 150)),
                    expectedStdoutContains = listOf("Power:"),
                    minStdoutLines = 1
                ),
                damageToEnemy = 60,
                enemyMaxHp = 120,
                enemyName = "Stone Golem",
                showAriaCompanion = true,
                backgroundKey = BackgroundKey.DOKJA_STREET,
                enemyType = EnemyType.GOLEM,
                isBoss = true
            ),

            // ACT 5: LOOT DROP
            SceneStep.LootClaimStep(
                id = "c1_loot",
                enemyName = "Stone Golem",
                enemyMaxHp = 120,
                message = "Stone Golem berhasil dilumpuhkan! Ketuk CLAIM >> di atas puing untuk mengambil Loot Box dari inti pertahanan.",
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
