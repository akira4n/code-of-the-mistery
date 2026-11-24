# Chapter 3 — *Looping the Anomaly*

> **Lokasi:** `Clockwork Data Spire` (Menara Server Beroda Gigi)  
> **Konsep Python:** Perulangan `for ... in range()`, `while` loop, Akumulator (`+=`), & `break` / `continue`  
> **Musuh:** Chrono Drone (`90 HP`) & **Boss: Loop Titan Golem** (`200 HP`)  
> **Reward:** `+900 XP` | `+1200 Points` | **Loot Box** (*Debugger Visor* / *Syntax Potion*)  
> **Aset Visual Dibutuhkan:** `bg_ch3_clockwork_spire`, `boss_loop_titan_idle`, `boss_loop_titan_death`

---

## Act 1 — Waktu yang Terjebak di Menara

### Scene 1.1: Menara yang Berulang *(Background: `bg_ch3_clockwork_spire`)*
- **PROTAGONIS:** *"Tunggu... Bukankah lampu neon di tangga ini baru saja berkedip dengan pola yang persis sama sepuluh detik lalu?"*
- **ARIA:** *"Kita masuk ke **While True Zone**! Seluruh menara ini terjebak dalam `while` loop tanpa kondisi berhenti."*
- **ARIA *(Micro-Lesson)*:** *"Untuk mengulang aksi berkali-kali di Python, kita punya dua senjata: **`for i in range(n):`** (jumlah ulangan sudah pasti) dan **`while kondisi:`** (terus mengulang selama kondisi benar). Di dalam `while`, nilai penghitung harus diubah mendekati batas berhenti supaya tidak menjadi **Infinite Loop**!"*

### Clue #3 Terbuka (`clue_ch3_loop`)
> **Perulangan `for` & `while` di Python:**  
> - `for i in range(n):` berjalan `n` kali dari `0` sampai `n - 1`.  
> - `range(start, stop)` berhenti sebelum `stop` (mis. `range(1, 5)` menghasilkan `1, 2, 3, 4`).  
> - `while kondisi:` terus mengulang selama kondisi `True` — pastikan variabel pengontrolnya berubah agar tidak terjadi **Infinite Loop**!

---

## Act 2 — Branching Encounter: *Menjinakkan Piston Waktu*

### Scene 2.1: Mesin Jam yang Menggila
- **ARIA:** *"Roda gigi utama berputar 1000 kali per detik! Kita bisa menghentikannya dengan menambahkan kondisi keluar (`break`), atau memaksanya lewat beban berlebih."*

### Branching Choice #3 — *Mengontrol Loop vs Overclock Piston*
| Pilihan Pemain | Alignment | Konsekuensi Cerita & Gameplay |
| :--- | :---: | :--- |
| **A. "Sisipkan kondisi `break` saat suhu inti melewati ambang aman."** | `+1 Patch` | Menyelamatkan generator menara. ARIA menemukan **Secret Clue Ch.3** (pola serangan 3-hit *Loop Titan*) sehingga HP Boss berkurang `25 HP` di awal! |
| **B. "Suntikkan loop paksa agar roda giginya macet seketika!"** | `+1 Purge` | Roda gigi meledak cepat (`+30 FORCE`), namun percikan listrik mengenai Hero (`-1 HP` jika tanpa shield). |

---

## Act 3 — Pre-Battle Micro-Lesson & Skirmish: *Chrono Drone Squadron*

### Scene 3.1: Pengawal Menara Waktu
- **ARIA *(Micro-Lesson)*:** *"Hati-hati menghadapi Chrono Drone! Ingat dua hal ini sebelum menyerang: Pertama, pada `while count > 0:`, kita harus **mengurangi** `count` (`count -= 1`), bukan menambahkannya! Kedua, `range(1, 4)` menghasilkan angka `1, 2, 3` — angka `4` tidak ikut!"*

### Tantangan 3.1 — *Bug Hunt (Infinite Loop Trap)* *(45 HP Damage)*
- **Bantuan Materi (`?`):** *"Dalam `while`, kondisi berhenti harus bisa tercapai. Jika syaratnya `count > 0` dan awalnya `count = 3`, maka `count` harus dikurangi (`-=`), bukan ditambah (`+=`)."*
```python
1 | count = 3
2 | while count > 0:
3 |     print("Sync:", count)
4 |     count += 1
5 | print("Selesai")
```
- **Baris Target:** **Baris 4** (`count += 1` membuat `count` terus membesar sehingga `count > 0` selalu True → **Infinite Loop**! Seharusnya `count -= 1`).

### Tantangan 3.2 — *Predict the Output* *(45 HP Damage)*
- **Bantuan Materi (`?`):** *"`range(start, stop)` berhenti sebelum `stop`. Jadi `range(1, 4)` mengulang nilai `i` = `1`, `2`, `3`. Operator `+=` berarti `total = total + nilai`."*
```python
total = 0
for i in range(1, 4):
    total += i * 10
print(total)
```
- **Opsi:** `A. 100` | `B. 60` | `C. 30` | `D. 40`
- **Jawaban:** **B. `60`** (`1*10 + 2*10 + 3*10 = 10 + 20 + 30 = 60`).

---

## Act 4 — Multi-Phase Boss Battle: *Loop Titan Golem*

### Scene 4.1: Penjaga Inti Menara *(Enemy: `Loop Titan Golem`, 200 HP)*
- *[Screen Shake! Golem raksasa dengan cincin jam berputar di punggungnya menghalangi tangga menuju puncak.]*
- **LOOP TITAN:** `"0x001... EXECUTING INFINITE_SMASH()... 0x002... EXECUTING INFINITE_SMASH()..."`
- **ARIA *(Boss Briefing)*:** *"Satu serangan biasa akan langsung diregenerasi oleh loop-nya! Gunakan **`for i in range(jumlah):`** dan operator akumulator **`total_power += ...`** untuk menumpuk damage di setiap putaran loop!"*

### Fase 1 Boss (`200 → 100 HP`) — *Code Chips*: Pemahaman `range()`
- **Bantuan Materi (`?`):** *"`range(n)` mengulang blok kode sebanyak tepat `n` kali (indeks `0` sampai `n-1`)."*
- **Soal:** Lengkapi kode untuk menembakkan tepat **4 tembakan laser (`25` damage per tembakan)** sehingga total damage mencapai `100`:
  `for shot in range(____): total += 25`
- **Pilihan Chips:** `4` *(Benar)* | `3` | `5` | `1, 4`

### Fase 2 Boss (`100 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Loop Breaker Combo
- **Bantuan Materi (`?`):** *"Format loop pengumpul nilai:\n```python\ntotal = 0\nfor i in range(3):\n    total += nilai\n```"*
- **Instruksi:** Lengkapi fungsi serangan dan perulangan `for` agar `total_power` mengumpulkan `150` damage tepat dalam `3` iterasi.
```python
strike = 25
total_power = 0

# Ulangi serangan sebanyak 3 kali dan gandakan daya pukul
for i in ____(____):
    total_power ____ strike * 2

print("Boss Damage:", total_power)
```
- **Code Chips Tersedia:** `range` | `3` | `+=` | `=` | `while` | `4`
- **Hint Laptop:** *"Isi `range(3)` pada baris `for`, dan gunakan operator akumulator `+=` untuk menambahkan `strike * 2` (50) sebanyak 3 kali."*
- **Validasi:** Wajib mengandung `for` dan `range`; `total_power == 150`, `stdout` memuat `Boss Damage: 150`.
- **Animasi Finisher:** Hero melakukan *3x rapid slash combo* + ledakan cahaya ungu → cincin jam Loop Titan pecah dan berhenti berputar!

---

## Act 5 — Resolution & Cliffhanger

- **ARIA:** *"Loop menara berhasil diputus! Jam kota kembali berdetak normal."*
- **Reward:** `+900 XP` | `+1200 Points` | **Loot Box (`CLAIM >>`)**.
- **Cliffhanger:** Sebelum pergi, sistem menara memutar log terenkripsi terakhir dari **Dr. Arka**: *"Jika kalian mendengar ini, jangan biarkan Vex membuka **Memory Vault** di Distrik 04... Semua memori warga yang hilang disimpan di dalam struktur List & Dictionary di sana!"*
