# Chapter 5 — *Modular Echoes*

> **Lokasi:** `Echo Relay Tower` (Puncak Menara Pemancar di Tengah Badai Kode)  
> **Konsep Python:** Fungsi (`def`), Parameter & `return`, Variable Scope, serta Penanganan Error (`try / except`)  
> **Musuh:** Signal Jammer (`100 HP`) & **Boss: Rival Vex ("Null")** (`250 HP`)  
> **Reward:** `+1200 XP` | `+1800 Points` | **Loot Box** (*Epic Weapon / Shield Upgrade*)  
> **Aset Visual Dibutuhkan:** `bg_ch5_relay_tower`, `enemy_signal_jammer`, `vex_sprite_battle`, `hero_portrait_determined`

---

## Act 1 — Badai Sinyal di Puncak Menara

### Scene 1.1: Lift Menuju Pemancar Utama *(Background: `bg_ch5_relay_tower`)*
- **PROTAGONIS:** *"Petir di atas menara ini bukan listrik biasa... Itu aliran `UnhandledException`!"*
- **ARIA:** *"Vex mencoba menyiarkan skrip penghapus ke seluruh distrik lewat pemancar menara! Jika kita tidak membungkus modul kita dengan **Fungsi (`def`)** yang rapi dan **`try/except`** untuk menahan error dadakan, sinyal serangannya akan mematahkan terminal kita."*

### Clue #5 Terbuka (`clue_ch5_functions`)
> **Fungsi (`def`) & Error Handling (`try / except`):**  
> - `def nama_fungsi(param):` membungkus logika agar bisa dipanggil berkali-kali. Gunakan `return hasil` untuk mengembalikan nilai.  
> - `try:` menjalankan kode berisiko error; jika gagal, blok `except:` menangkap error agar program **tidak crash**!

---

## Act 2 — Branching Encounter: *Dialog Terakhir Sebelum Duel*

### Scene 2.1: Konfrontasi di Atap Menara
- *[Petir menyambar antena utama. Vex berdiri di depan konsol pemancar.]*
- **VEX:** *"Kau selalu menambal bug satu per satu... Berapa lama kau mau bertahan seperti itu? Lihat dunia ini — tidak ada yang aman tanpa reset total!"*
- **PROTAGONIS:** *"Kalau kamu menghapus semuanya, setiap perjuangan orang-orang yang selamat di bawah sana juga ikut hilang!"*

### Branching Choice #5 — *Pendekatan Terhadap Vex*
| Pilihan Pemain | Alignment | Konsekuensi Cerita & Gameplay |
| :--- | :---: | :--- |
| **A. "Vex, lihat log Memory Vault! Dr. Arka menyisakan modul perbaikan — bukan penghancuran!"** | `+1 Patch` | Vex terdiam sejenak dan meragu. Membuka **Secret Clue Ch.5** (membongkar alasan pribadi Vex di balik hilangnya keluarganya saat *Runtime Error*) — kunci untuk meyakinkan Vex di Ending A! |
| **B. "Cukup bicaranya! Aku akan memutus kabel pemancarmu secara paksa!"** | `+1 Purge` | Duel dimulai dengan intensitas tinggi (`+25 FORCE` awal), namun Vex mengerahkan pertahanan penuh sejak giliran pertama. |

---

## Act 3 — Skirmish Mini-Puzzle: *Signal Jammer Barrier*

### Tantangan 3.1 — *Bug Hunt (Missing `return` in Function)* *(50 HP Damage)*
```python
1 | def boost_signal(power):
2 |     result = power * 2
3 |     print("Boosting...")
4 | total = boost_signal(30) + 10
```
- **Baris Target:** **Baris 3 / Akhir Fungsi** (fungsi `boost_signal` lupa mengembalikan nilai dengan `return result`, sehingga menghasilkan `None` dan menyebabkan error saat dijumlahkan `+ 10`!).

### Tantangan 3.2 — *Predict the Output (`try/except`)* *(50 HP Damage)*
```python
try:
    dmg = 100 / 0
    print("OK")
except:
    print("SAFE CATCH")
```
- **Opsi:** `A. OK` | `B. SAFE CATCH` | `C. Crash ZeroDivisionError` | `D. OK SAFE CATCH`
- **Jawaban:** **B. `SAFE CATCH`** (pembagian nol masuk ke `except` sehingga program tetap aman!).

---

## Act 4 — Multi-Phase Boss Battle: *Duel vs Rival Vex ("Null")*

### Scene 4.1: Coder vs Coder *(Enemy: `Rival Vex`, 250 HP)*
- *[Animasi: Vex mengaktifkan gauntlet merahnya; hujan turun di antara percikan kode ungu dan crimson!]*
- **VEX:** *"Tunjukkan kalau fungsi `MAIN.PY`-mu layak menghentikan milikku!"*

### Fase 1 Boss (`250 → 125 HP`) — *Code Chips*: Modular Scope & Return
- **Soal:** Lengkapi fungsi agar menerima `base` dan `multiplier`, lalu mengembalikan hasil perkalian keduanya:
  `def strike(base, multiplier): ____ base * multiplier`
- **Pilihan Chips:** `return` *(Benar)* | `print` | `break` | `def`

### Fase 2 Boss (`125 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Counter-Module Protocol
- **Instruksi:** Buat fungsi `counter_attack(energy, bonus)` yang mengembalikan `energy + bonus`, bungkus pemanggilan berisiko dalam `try/except`, dan hasilkan `final_dmg = 160`.
```python
def counter_attack(energy, bonus):
    ____ energy + bonus

final_dmg = 0
try:
    hit_1 = counter_attack(50, 30)
    hit_2 = counter_attack(50, 30)
    final_dmg = hit_1 ____ hit_2
except:
    final_dmg = 0

print("Counter:", final_dmg)
```
- **Code Chips Tersedia:** `return` | `+` | `*` | `print` | `-`
- **Hint Laptop:** *"Gunakan `return` di dalam `def counter_attack`, lalu jumlahkan `hit_1 + hit_2` (`80 + 80 = 160`)."*
- **Validasi:** Wajib mengandung `def` dan `return`; `final_dmg == 160`, `stdout` memuat `Counter: 160`.
- **Animasi Finisher:** Benturan aura ungu Hero vs crimson Vex — Vex terpental mundur ke tepi konsol, pemancar berhasil dinetralkan!

---

## Act 5 — Resolution & Cliffhanger

- **VEX *(terengah)*:** *"Hmph... Ternyata fungsi `try/except`-mu benar-benar menyelamatkan menara ini dari overload. Tapi lihat ke atas sana... Gerbang menuju **The Blueprint Citadel** baru saja terbuka sendiri."*
- **ARIA:** *"Sinyal itu berasal dari **Dr. Arka** — tapi ada sesuatu yang salah! Class cetak biru dunia sedang ditulis ulang oleh virus!"*
- **Reward:** `+1200 XP` | `+1800 Points` | **Loot Box (`CLAIM >>`)**.
