# Story Bible — Code of The Mystery (Chapter 1–7)

> **Genre:** Educational Cyberpunk Mystery RPG / Visual Novel  
> **Tema:** *"Learn, Play, and Earn — Fix the World, One Line at a Time"*  
> **Bahasa Kurikulum:** Python 3  

---

## 1. Premis Dunia: *The Great Runtime Error (Tahun 2026)*

Pada suatu malam di tahun 2026, sebuah kejadian misterius bernama **The Great Runtime Error** mengguncang kota Neo-Nusantara. Langit terbelah menampilkan baris-baris kode `0xDEADCODE` dan `ERROR_LOGIC_KERNEL_FAILURE`.

Tanpa penjelasan ilmiah biasa, infrastruktur kota mulai bertingkah seperti program komputer yang rusak:
- Gedung-gedung menghilang karena **Garbage Collector** salah men-dealokasi memori.
- Kereta bawah tanah terjebak dalam **Infinite Loop** di stasiun yang sama.
- Data identitas warga berubah menjadi `<undefined>`, dan monster-monster anomali (*Golem*, *Drone*, *Phantom*) lahir dari *exception* yang tidak tertangani (*unhandled exceptions*).

Satu-satunya cara bertahan hidup dan memulihkan dunia bukanlah senjata api, melainkan **Terminal Kernel (`MAIN.PY`)** yang hanya bisa dioperasikan oleh manusia dengan pemahaman logika pemrograman.

---

## 2. Profil Karakter Utama

### 1. Protagonis — *The Mystery Coder* (`<Nama Pemain>`)
- **Latar Belakang:** Mahasiswa biasa yang terbangun di depan **Dokja Supermarket** saat langit retak. Berbeda dari warga lain yang panik, laptop di tasnya bereaksi dan berubah menjadi **Kernel Terminal**.
- **Sifat:** Adaptif, rasa ingin tahu tinggi, berkembang dari pemula yang takut error (*coding anxiety*) menjadi arsitek kode yang tenang di bawah tekanan.

### 2. ARIA — *Autonomous Runtime Intelligence Assistant*
- **Peran:** AI Companion berbentuk hologram orb ungu-cyan yang menempel pada laptop protagonis.
- **Sifat:** Cerdas, sedikit sarkastik tapi peduli, menjelaskan konsep Python lewat analogi dunia nyata dan selalu mencatat **Clue** ke log sistem.

### 3. Vex ("Null") — *The Purge Hacker*
- **Peran:** Rival misterius sekaligus hacker jenius yang juga selamat dari *Great Runtime Error*.
- **Filosofi:** Percaya bahwa manusia dan kernel lama terlalu penuh bug untuk diperbaiki (*Patch*). Ia ingin mencapai *The Null Kernel* terlebih dahulu untuk menjalankan perintah **Purge / Hard Reset** guna membangun dunia baru dari nol — meski mengorbankan memori kota lama.
- **Dinamika:** Muncul di Ch. 1, 2, 4, berduel di Ch. 5, dan menjadi sekutu atau antagonis di Ch. 7 tergantung pilihan pemain!

### 4. The Architect (Dr. Arka) — *Pencipta Kernel Bumi* *(Terungkap di Ch. 6–7)*
- **Peran:** Arsitek sistem yang hilang di balik terciptanya *Runtime Error*. Terkorupsi oleh bug rekursif di Ch. 6 dan menjadi kunci rahasia **True Ending**.

---

## 3. Peta Besar Kurikulum & Alur 7 Chapter

| Chapter | Judul & Setting | Konsep Python | Musuh / Boss | Link Naskah Detail |
| :---: | :--- | :--- | :--- | :--- |
| **Ch. 1** | *Mastering Logic, Surviving Syntax* (`Dokja Supermarket`) | Komentar, Variabel, `str`/`int`/`float`/`bool`, `print()`, `if` dasar | Syntax Slime & **Stone Golem** | [chapter_01.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_01.md) |
| **Ch. 2** | *Branching Protocol, Break the Firewall* (`Subway Station 05`) | Operator Perbandingan, Logika (`and`/`or`/`not`), `if`/`elif`/`else` | Security Turret & **Rogue Drone** | [chapter_02.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_02.md) |
| **Ch. 3** | *Looping the Anomaly* (`Clockwork Data Spire`) | `for ... in range()`, `while`, `break`/`continue`, Akumulator | Chrono Drone & **Loop Titan Golem** | [chapter_03.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_03.md) |
| **Ch. 4** | *The Memory Vault* (`Black-Market Archive`) | `List`, Indexing/Slicing, `.append()`, `Dictionary` (`key: value`), Iterasi | Data Mimic & **Cache Phantom** | [chapter_04.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_04.md) |
| **Ch. 5** | *Modular Echoes* (`Echo Relay Tower`) | `def` Fungsi, Parameter, `return`, Scope, `try/except` Error Handling | Signal Jammer & **Rival Vex (Duel)** | [chapter_05.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_05.md) |
| **Ch. 6** | *Object Awakening* (`The Blueprint Citadel`) | OOP: `class`, `__init__`, `self`, Atribut, Method, & Inheritance | Blueprint Sentinel & **Corrupted Architect** | [chapter_06.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_06.md) |
| **Ch. 7** | *The Final Kernel* (`Zero-Point Kernel Core`) | Algoritma Gabungan (Filter, Max/Min, Search/Sort, Multi-function Debug) | Null Vanguard & **The Null Kernel** | [chapter_07.md](file:///d:/projects/Android/code-of-the-mistery/docs/chapters/chapter_07.md) |

---

## 4. Sistem 3 Ending (Chapter 7 Finale)

Kondisi akhir cerita di Chapter 7 ditentukan oleh **Alignment (`Patch` vs `Purge`)** dan **Secret Clues Found (`0..7`)** yang dikumpulkan pemain sepanjang Chapter 1–7:

1. **Ending A — *True Kernel Patch* (True Ending)**
   - *Syarat:* `Patch >= Purge` DAN minimal `5/7 Secret Clues` ditemukan.
   - *Kisah:* Pemain tidak hanya mengalahkan *The Null Kernel*, tetapi berhasil menemukan modul sumber milik Dr. Arka (`Origin Patch`), memperbaiki *Kernel Bumi* tanpa menghapus ingatan warga, serta meyakinkan **Vex** bahwa kode yang tidak sempurna tetap layak diperbaiki (*patched*), bukan dihapus.
2. **Ending B — *Clean Reboot* (Hard Reset Ending)**
   - *Syarat:* `Purge > Patch` (dominan memilih solusi agresif/paksa).
   - *Kisah:* Pemain mengambil alih otoritas Vex dan menjalankan `system.reboot(hard=True)`. Semua anomali lenyap seketika, tetapi seluruh kota ter-reset ke kondisi *factory default* — hanya pemain yang mengingat petualangan mereka.
3. **Ending C — *Bitwise Rogue* (Bittersweet / Secret Survival Ending)**
   - *Syarat:* `Patch >= Purge` namun `Secret Clues < 5`.
   - *Kisah:* Boss berhasil ditahan sementara di *sandbox* terisolasi. Dunia selamat dari kehancuran total, namun retakan langit kecil tetap ada — membuka misteri sekuel *(Season 2 / Phase 2 Online Raid)*.
