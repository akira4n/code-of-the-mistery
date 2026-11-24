# Chapter 1 — *Mastering Logic, Surviving Syntax*

> **Lokasi:** Jalanan Depan `Dokja Supermarket` (Distrik 01)  
> **Konsep Python:** Komentar (`#`), Variabel, Tipe Data (`str`, `int`, `float`, `bool`), `print()`, & Pengenalan `if`  
> **Musuh:** Syntax Slime (`60 HP`) & **Boss: Stone Golem** (`120 HP`)  
> **Reward:** `+500 XP` | `+500 Points` | **Loot Box** (*Firewall Shield* / *Syntax Potion*)  
> **Aset Visual Dibutuhkan:** `bg_story_error`, `bg_ch1_dokja_street`, `hero_idle/run/skill/death`, `aria_orb_idle`, `vex_portrait_smirk`, `enemy_slime_glitch`, `golem_idle/death`

---

## Act 1 — Anomaly Opening: *The Great Runtime Error*

### Scene 1.1: Langit yang Crash *(Background: `bg_story_error`)*
- **NARATOR:** *"Tahun 2026. Sebuah fenomena katastrofik melanda tanpa peringatan: **The Great Runtime Error**. Langit retak bagai monitor yang gagal merender realitas, menumpahkan aliran kode hexadecimal `0xDEADCODE` ke permukaan kota."*
- **NARATOR:** *"Gedung-gedung bertingkat tiba-tiba lenyap karena Garbage Collector salah men-dealokasi memori. Gravitasi tersendat, lampu jalan berkedip-kedip dalam pola biner."*

### Scene 1.2: Terbangun di Dokja Supermarket *(Background: `bg_ch1_dokja_street`)*
- *[Animasi: Hero berlari terengah-engah lalu berhenti di trotoar basah depan Dokja Supermarket.]*
- **PROTAGONIS:** *"AAH, DIMANA AKU SEKARANG!? Kenapa neon toko itu bertuliskan `ERROR_LOGIC_KERNEL_FAILURE`?!"*
- **ARIA *(orb cyan muncul dari tas laptop)*:** *"Sinkronisasi berhasil! Halo, aku **ARIA**. Selamat datang di Neo-Nusantara pasca-Runtime Error. Status identitasmu di jaringan saat ini masih `<undefined>`."*
- **PROTAGONIS:** *"Identitasku `<undefined>`? Apa maksudnya?!"*
- **ARIA:** *"Kalau entitas tidak punya variabel identitas yang valid, sistem keamanan kota akan menganggapmu bug dan mengirim pemburu anomali untuk menghapusmu! Buka terminal `MAIN.PY` sekarang!"*

### Clue #1 Terbuka (`clue_ch1_vars`)
> **Variabel & Tipe Data Dasar Python:**  
> Di Python, variabel dibuat dengan format `nama_variabel = nilai`.  
> - **String (`str`)**: teks di dalam tanda kutip, mis. `"Dokja"`  
> - **Integer (`int`)**: bilangan bulat, mis. `6`  
> - **Float (`float`)**: bilangan desimal, mis. `3.5`  
> - **Boolean (`bool`)**: logika `True` atau `False` (huruf awal kapital!)

### Tantangan 1.1 — *Fill-in-the-Blank `MAIN.PY`*: Inisialisasi Identitas
- **Instruksi:** Lengkapi bagian `____` agar variabel `nama` bertipe **String**, `HP` bertipe **Integer `6`**, dan cetak keduanya ke terminal.
```python
# Inisialisasi identitas The Mystery Coder
nama = ____       # Clue: isi namamu dengan tanda kutip "..."
HP = ____         # Clue: isi angka 6 (tanpa tanda kutip!)
aktif = True

print("Coder:", nama)
print("HP:", HP)
```
- **Code Chips Tersedia:** `"Coder"` | `6` | `"6"` | `True` | `undefined`
- **Validasi:** Tidak ada `____` tersisa, `nama` string non-kosong, `HP == 6` (`Int`), `stdout` min. 2 baris.
- **Feedback Sukses:** `SYSTEM: Identitas terverifikasi. Selamat datang, <nama>.`

---

## Act 2 — Branching Encounter: *Kemunculan Vex & Pilihan Jalur*

### Scene 2.1: Pertemuan Pertama dengan Rival
- *[Animasi: Sosok berjaket hitam-merah berdiri di atap truk logistik sambil memainkan terminal merahnya.]*
- **VEX ("Null"):** *"Oh? Ada orang lain yang laptop-nya ikut bangkit? Menarik. Tapi jangan buang waktumu menambal jalanan rusak ini."*
- **PROTAGONIS:** *"Siapa kau? Dan kenapa kau membiarkan monster glitch berkeliaran?"*
- **VEX:** *"Namaku Vex. Kota ini sudah penuh *spaghetti code*. Satu-satunya jalan adalah mencapai **Null Kernel** dan menghapus semuanya dari awal (`Purge`)."*
- **ARIA:** *"Jangan dengarkan dia! Menghapus kernel akan menghapus seluruh ingatan warga yang terjebak di buffer kota!"*

### Branching Choice #1 — *Sikap Terhadap Vex*
| Pilihan Pemain | Alignment | Konsekuensi Cerita & Gameplay |
| :--- | :---: | :--- |
| **A. "Aku akan mencari cara memperbaiki bug ini tanpa mengorbankan siapa pun!"** *(Bela warga)* | `+1 Patch` | Vex tertawa sinis dan menghilang. ARIA memindai sisa energi Vex dan membuka **Secret Clue Ch.1** (`clue_ch1_secret_comments`: cara membaca jejak komentar `#` di kode sistem) + membuka kelemahan Boss Golem! |
| **B. "Tunjukkan di mana Null Kernel berada, aku tidak peduli kota ini!"** *(Ikut logika cepat)* | `+1 Purge` | Vex melempar *Overclock Fragment* (`+15 FORCE` awal), namun ARIA khawatir dan HP maksimal sementara terasa lebih berisiko (Boss menyerang lebih cepat di fase 1). |
| **C. "Berikan aku satu alasan kenapa kode dunia ini tidak bisa di-debug."** *(Analitis)* | `+1 Patch` | Vex menjawab dengan teka-teki tentang *Runtime Error* dan menjatuhkan **Secret Clue Ch.1** sebelum pergi. |

---

## Act 3 — Skirmish Mini-Puzzle: *Syntax Slime Attack*

### Scene 3.1: Serangan Anomali Pertama *(Enemy: `Syntax Slime`, 60 HP)*
- *[Animasi: Gumpalan lendir pixel ungu merangkak keluar dari selokan berkedip.]*
- **ARIA:** *"Waspada! Itu **Syntax Slime** — ia lahir dari baris variabel yang salah ketik! Gunakan teknik **Bug Hunt** untuk menebas baris yang salah!"*

### Tantangan 3.1 — *Bug Hunt (Tap the Error Line)* *(Damage ke Slime: `35 HP`)*
```python
1 | # Status awal pertahanan
2 | shield_level = 2
3 | hero_name = "Mystery Coder"
4 | is_ready = true
5 | print(hero_name, shield_level)
```
- **Baris Target yang Benar:** **Baris 4** (`is_ready = true` salah karena di Python Boolean harus huruf kapital `True`).
- **Animasi:** Jika benar, Hero *lunge* ke depan dengan `SKILL_1` (`-35 Enemy HP` + partikel ungu). Jika salah, Slime melompat (`-1 Player HP` / ditahan `Firewall Shield`).

### Tantangan 3.2 — *Predict the Output* *(Finisher Slime: `25 HP`)*
- **Soal:** Jika dijalankan kode berikut, apa yang muncul di terminal `OUTPUT`?
```python
x = 10
y = 3
print(x + y * 2)
```
- **Opsi:** `A. 26` | `B. 16` | `C. 20` | `D. Error`
- **Jawaban:** **B. `16`** (perkalian `3 * 2` dieksekusi sebelum penjumlahan `10 + 6`).
- **Hasil:** Slime pecah menjadi partikel bit (`01`)!

---

## Act 4 — Multi-Phase Boss Battle: *Stone Golem di Dokja Supermarket*

### Scene 4.1: Kebangkitan Penjaga Gerbang *(Enemy: `Stone Golem`, 120 HP)*
- *[Screen Shake! Batu trotoar terangkat membentuk Stone Golem raksasa.]*
- **SYSTEM ALERT:** `<MAIN SCENARIO #1: DECRYPTION BATTLE> Kategori: MAIN | Musuh: Stone Golem | Kondisi Menang: Turunkan Perisai Golem!`
- **ARIA:** *"Golem itu dilindungi perisai energi (`shields_active = True`). Patahkan perisainya dulu, lalu hantam intinya!"*

### Fase 1 Boss (`120 → 60 HP`) — *Code Chips*: Analisis Tipe Data & Kondisi
- **Soal:** Lengkapi kode kondisi berikut untuk mengecek apakah energi terminal cukup untuk menembus armor Golem (`60 HP` damage):
  `if energy ____ 50 and shields_active ____ True:`
- **Pilihan Chips:** `>` & `==` *(Benar)* | `<` & `=` | `>=` & `!=`
- **Visual:** Perisai biru di sekitar Golem retak (`Status: EXPOSED`)!

### Fase 2 Boss (`60 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Decryption Finisher
- **Instruksi:** Lengkapi bagian `____` agar jika `energy > 50`, nilai `shields_active` berubah menjadi `False` dan `total_power` menjadi `100` (atau `150` jika pemain membuka *Secret Clue* di Act 2!).
```python
# Player input values
energy = 70
shields_active = True
total_power = 0

# Lengkapi kondisi untuk menurunkan perisai jika energy > 50
if energy ____ 50:
    shields_active = ____
    total_power = ____

print("Power:", total_power)
```
- **Code Chips Tersedia:** `>` | `False` | `True` | `100` | `50` | `==`
- **Hint Laptop:** *"Gunakan `>` untuk membandingkan `energy > 50`, ubah `shields_active = False`, dan isi `total_power = 100`."*
- **Validasi:** Wajib mengandung `if`, `shields_active == false`, `total_power == 100`, `stdout` memuat `Power: 100`.
- **Animasi Finisher:** Hero melompat + `SKILL_2` tebasan ungu besar → Golem hancur berlutut (`golem_death_1`)!

---

## Act 5 — Resolution, Loot Drop & Cliffhanger

- *[Tombol kuning **`CLAIM >>`** berkedip di atas puing Golem.]*
- **ARIA:** *"Kerja bagus! Kamu bukan lagi `<undefined>`. Lihat, Golem itu menjatuhkan inti pertahanan!"*
- **Reward Drop:** **Firewall Shield** *(RARE — memblokir 1 kesalahan pertama di tiap chapter)* + `+500 XP` + `+500 Points`.
- **Cliffhanger:** Di kejauhan, gerbang kereta bawah tanah **Subway Station 05** berbunyi nyaring. Lampu alarm merah menyala — ratusan **Rogue Drone** mengudara, sementara siluet Vex terlihat menuruni eskalator gelap.
