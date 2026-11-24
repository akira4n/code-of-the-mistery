# Chapter 2 — *Branching Protocol, Break the Firewall*

> **Lokasi:** `Subway Station 05` (Distrik Stasiun Bawah Tanah Neon)  
> **Konsep Python:** Operator Perbandingan (`==`, `!=`, `>=`, `<=`), Logika (`and`, `or`, `not`), & Percabangan `if / elif / else`  
> **Musuh:** Security Turret (`70 HP`) & **Boss: Rogue Surveillance Drone** (`150 HP`)  
> **Reward:** `+650 XP` | `+800 Points` | **Loot Box** (*Syntax Potion* / *Debugger Visor* jika sempurna)  
> **Aset Visual Dibutuhkan:** `bg_ch2_subway_firewall`, `enemy_turret_laser`, `drone_atk/death`, `vex_portrait_serious`

---

## Act 1 — Masuk ke Peron Terlarang

### Scene 1.1: Gerbang Laser Stasiun 05 *(Background: `bg_ch2_subway_firewall`)*
- **PROTAGONIS:** *"Udara di bawah tanah dingin sekali... Kenapa semua kereta berhenti di tempat yang sama?"*
- **ARIA:** *"Sistem persinyalan stasiun sedang dibajak oleh protokol percabangan rusak! Setiap penumpang yang tidak lolos logika `if / elif / else` akan ditembak oleh turret keamanan."*
- **ARIA *(Micro-Lesson)*:** *"Di Python, kamu bisa memeriksa banyak cabang kondisi sekaligus! Gunakan **`if`** untuk syarat pertama, **`elif`** (*else if*) untuk syarat cadangan jika yang pertama salah, dan **`else:`** sebagai penutup jika semuanya salah. Jangan lupa tanda **`:`** di akhir setiap baris syarat!"*
- **WARGA TERJEBAK (NPC Rina):** *"Tolong! Pintu gerbang peron terkunci, dan drone patroli menyisir dari terowongan utara!"*

### Clue #2 Terbuka (`clue_ch2_if`)
> **Percabangan `if / elif / else` & Logika Boolean:**  
> - `if kondisi_1:` dicek paling awal.  
> - `elif kondisi_2:` dicek jika `if` bernilai `False`.  
> - `else:` dijalankan jika semua kondisi di atas `False`.  
> - `and` butuh kedua sisi `True`; `or` butuh minimal satu `True`; `not` membalik `True ↔ False`.

---

## Act 2 — Branching Encounter: *Rute Terowongan Utara vs Ruang Kontrol*

### Scene 2.1: Dua Sinyal dari Vex
- **ARIA:** *"Aku mendeteksi dua sinyal di depan. Di kiri ada **Ruang Kontrol** tempat Rina dan 3 warga lain terjebak di balik pintu kode. Di kanan ada **Jalur Cepat Vex** menuju terowongan inti."*

### Branching Choice #2 — *Menyelamatkan Warga atau Mengejar Vex*
| Pilihan Pemain | Alignment | Konsekuensi Cerita & Gameplay |
| :--- | :---: | :--- |
| **A. "Buka pintu Ruang Kontrol dan selamatkan warga dulu!"** | `+1 Patch` | Rina menyerahkan **Access Card** (*Secret Clue Ch.2*: frekuensi bypass radar Drone + `Syntax Potion +1`!). |
| **B. "Kejar Vex lewat jalur pintas sebelum dia menghancurkan panel stasiun!"** | `+1 Purge` | Langsung memotong jalan dan berhadapan dengan Vex yang memberi tantangan cepat; mendapat `+20 FORCE`, tapi warga baru diselamatkan setelah Boss kalah. |

---

## Act 3 — Skirmish Mini-Puzzle: *Security Turret Gate*

### Scene 3.1: Turret Penjaga Pintu *(Enemy: `Security Turret`, 70 HP)*
- **SYSTEM:** `ACCESS DENIED. VERIFY LOGIC GATE TO DISABLE TURRET.`
- **ARIA *(Micro-Lesson)*:** *"Ingat aturan emas percabangan: setiap header `if`, `elif`, atau `else` **wajib diakhiri tanda titik dua (`:`)** dan operator `not` membalik nilai logika (`not False` menjadi `True`, `not True` menjadi `False`)."*

### Tantangan 3.1 — *Bug Hunt (Tap the Error Line)* *(35 HP Damage)*
- **Bantuan Materi (`?`):** *"Setiap blok `if kondisi:`, `elif kondisi:`, dan `else:` di Python wajib ditutup dengan tanda `:` sebelum baris berindentasi di bawahnya."*
```python
1 | clearance = 4
2 | has_pass = True
3 | if clearance >= 3 and has_pass
4 |     print("GATE OPEN")
5 | else:
6 |     print("LOCKED")
```
- **Baris Target:** **Baris 3** (kekurangan tanda titik dua `:` di akhir `if`).

### Tantangan 3.2 — *Predict the Output* *(35 HP Damage)*
- **Bantuan Materi (`?`):** *"`or` bernilai True jika salah satu sisi benar. `and` butuh kedua sisi benar. `not False` berarti `True`. Python hanya menjalankan cabang pertama yang bernilai True!"*
```python
speed = 85
alert = False
if speed > 90 or alert:
    print("RED")
elif speed >= 80 and not alert:
    print("AMBER")
else:
    print("GREEN")
```
- **Opsi:** `A. RED` | `B. AMBER` | `C. GREEN` | `D. RED AMBER`
- **Jawaban:** **B. `AMBER`** (`speed > 90 or alert` bernilai False, lalu `85 >= 80 and not False` bernilai True!).

---

## Act 4 — Multi-Phase Boss Battle: *Rogue Surveillance Drone*

### Scene 4.1: Duel Udara di Atas Rel *(Enemy: `Rogue Drone`, 150 HP)*
- *[Animasi: Rogue Drone meluncur dari terowongan sambil menembakkan laser pemindai merah!]*
- **VEX *(suara lewat interkom)*:** *"Drone itu mengevaluasi jarakmu setiap milidetik! Jika logikamu bercabang terlalu lambat, kau habis!"*
- **ARIA *(Boss Briefing)*:** *"Gabungkan tanda kurung `(...)` saat mencampur `or` dan `and` agar prioritasnya jelas, lalu susun tiga cabang: jarak dekat (`< 5`), jarak menengah (`elif distance <= 15`), dan jarak jauh (`else`)."*

### Fase 1 Boss (`150 → 75 HP`) — *Quiz Logika Operator*
- **Bantuan Materi (`?`):** *"Gunakan tanda kurung `(...)` untuk mengelompokkan syarat `and` di dalam `or`: `A or (B and C)`."*
- **Soal:** Drone hanya rentan jika `shield == 0` **ATAU** (`battery < 20` **DAN** `overheated == True`). Manakah kondisi Python yang tepat?
- **Jawaban:** `shield == 0 or (battery < 20 and overheated == True)`

### Fase 2 Boss (`75 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Protocol Overload
- **Bantuan Materi (`?`):** *"Urutan cabang multi-kondisi: `if syarat1:` → `elif syarat2:` → `else:`. Karena `distance = 12`, cabang kedua (`<= 15`) akan terpilih."*
- **Instruksi:** Lengkapi cabang `if / elif / else` di `MAIN.PY` berdasarkan jarak `distance = 12` agar `mode = "STRIKE"` dan `damage = 80`.
```python
distance = 12
mode = "WAIT"
damage = 0

if distance ____ 5:
    mode = "MELEE"
    damage = 50
____ distance <= 15:
    mode = ____
    damage = ____
else:
    mode = "SCAN"
    damage = 20

print(mode, damage)
```
- **Code Chips Tersedia:** `<` | `elif` | `"STRIKE"` | `80` | `else` | `==`
- **Hint Laptop:** *"Baris 5 butuh `<` (atau `<= 5`), baris 8 menggunakan `elif`, lalu isi `mode = \"STRIKE\"` dan `damage = 80`."*
- **Validasi:** Wajib menggunakan `if`, `elif`, `else`; `mode == "STRIKE"`, `damage == 80`, `stdout` memuat `STRIKE 80`.
- **Animasi Finisher:** Hero menembakkan *Pulse Slash* ke atas → Drone meledak jatuh (`drone_death_1..2`)!

---

## Act 5 — Resolution & Cliffhanger

- **ARIA:** *"Protokol persinyalan stasiun kembali normal! Rina dan para warga aman menuju shelter."*
- **Reward:** `+650 XP` | `+800 Points` | **Loot Box (`CLAIM >>`)**.
- **Cliffhanger:** Dari layar monitor stasiun, sinyal utama anomali tidak berasal dari bawah tanah — melainkan dari **Clockwork Data Spire**, menara jam digital raksasa yang jarumnya terus berputar mundur tanpa henti dalam *Infinite Loop*!
