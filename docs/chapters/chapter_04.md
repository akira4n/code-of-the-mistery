# Chapter 4 — *The Memory Vault*

> **Lokasi:** `Black-Market Memory Vault` (Gudang Arsip Data Kubus Melayang)  
> **Konsep Python:** Struktur Data `List` (`[]`, indexing `0/-1`, `.append()`, `len()`), & `Dictionary` (`{"key": value}`)  
> **Musuh:** Data Mimic (`100 HP`) & **Boss: Cache Phantom** (`220 HP`)  
> **Reward:** `+1000 XP` | `+1500 Points` | **Loot Box** (*Rare/Epic Equipment*)  
> **Aset Visual Dibutuhkan:** `bg_ch4_memory_vault`, `enemy_data_mimic`, `boss_cache_phantom`, `vex_portrait_serious`

---

## Act 1 — Arsip Memori Kota yang Hilang

### Scene 1.1: Lorong Kubus Memori *(Background: `bg_ch4_memory_vault`)*
- **PROTAGONIS:** *"Semua bangunan yang menghilang di Chapter 1... ternyata tidak hancur! Semuanya mengambang sebagai kubus memori di tempat ini?"*
- **ARIA:** *"Benar! Garbage Collector mengira mereka sampah memori dan membuangnya ke **Memory Vault**. Tapi indeksnya berantakan — satu salah ambil index (`IndexError`), kubus bisa meledak!"*
- **ARIA *(Micro-Lesson)*:** *"Di Python, **List** memakai kurung siku `[a, b, c]` dan **indeks selalu dimulai dari `0`**! Jadi list isi 3 item hanya punya indeks `0, 1, 2`. Sedangkan **Dictionary** memakai kurung kurawal `{"kunci": nilai}` untuk menyimpan data berpasangan, dan nilainya diakses lewat nama kuncinya!"*

### Clue #4 Terbuka (`clue_ch4_structures`)
> **List & Dictionary di Python:**  
> - **List:** Kumpulan data terurut, indeks mulai dari `0`. Contoh: `items = ["Key", "Shield"]`, elemen pertama `items[0]`, elemen terakhir `items[-1]`.  
> - **Dictionary:** Pasangan `key: value`. Contoh: `user = {"name": "Dokja", "hp": 6}`, akses dengan `user["hp"]`.  
> - `len(data)` menghitung jumlah elemen di List atau Dictionary.

---

## Act 2 — Branching Encounter: *Penawaran Vex di Ruang Arsip*

### Scene 2.1: Jejak Vex di Lemari Cache
- **VEX:** *"Kau lagi. Aku tidak menyangka kau bisa melewati Loop Titan. Lihat kubus-kubus ini — penuh data rusak, termasuk memori lama seseorang bernama Dr. Arka."*
- **ARIA:** *"Hati-hati! Vex sedang memindahkan data inti ke dalam list penampung untuk dihapus sekaligus!"*

### Branching Choice #4 — *Menyelamatkan Backup Memori Warga vs Merebut Kunci Arka*
| Pilihan Pemain | Alignment | Konsekuensi Cerita & Gameplay |
| :--- | :---: | :--- |
| **A. "Pulihkan daftar backup memori warga ke server cadangan lebih dulu!"** | `+1 Patch` | Menyimpan `backup_list` warga. Membuka **Secret Clue Ch.4** (log catatan Dr. Arka: `key = "origin_patch"`) dan menambah `+1 Syntax Potion`. |
| **B. "Rebut langsung fragment memori Dr. Arka dari tangan Vex!"** | `+1 Purge` | Vex mundur sambil mengaktifkan jebakan *Cache Phantom* lebih cepat; pemain mendapat `+30 FORCE` dari fragment energi. |

---

## Act 3 — Pre-Battle Micro-Lesson & Skirmish: *Data Mimic Ambush*

### Scene 3.1: Menyaring Kubus Palsu
- **ARIA *(Micro-Lesson)*:** *"Awas jebakan Data Mimic! Ingat: jumlah item (`len`) misalnya `3`, berarti indeks maksimal yang sah adalah `len - 1` (yaitu `2`). Lalu untuk mengubah nilai di Dictionary, cukup lakukan assignment seperti `dict["key"] = nilai_baru`."*

### Tantangan 3.1 — *Bug Hunt (IndexError & KeyError)* *(50 HP Damage)*
- **Bantuan Materi (`?`):** *"Indeks List di Python dimulai dari `0`. List dengan 3 elemen (`["alpha", "beta", "gamma"]`) hanya memiliki indeks `0`, `1`, dan `2`."*
```python
1 | keys = ["alpha", "beta", "gamma"]
2 | print(keys[0])
3 | print(keys[3])
4 | print(len(keys))
```
- **Baris Target:** **Baris 3** (`keys` hanya punya indeks `0, 1, 2`; `keys[3]` memicu `IndexError`!).

### Tantangan 3.2 — *Predict the Output (Dictionary)* *(50 HP Damage)*
- **Bantuan Materi (`?`):** *"Nilai Dictionary bersifat mutable (bisa diubah). `vault["locked"] = False` memperbarui nilai kunci `"locked"` dari `True` menjadi `False`."*
```python
vault = {"sector": 4, "locked": True}
vault["locked"] = False
print(vault["sector"], vault["locked"])
```
- **Opsi:** `A. 4 True` | `B. 4 False` | `C. sector False` | `D. KeyError`
- **Jawaban:** **B. `4 False`**.

---

## Act 4 — Multi-Phase Boss Battle: *Cache Phantom*

### Scene 4.1: Hantu Memori yang Berubah Bentuk *(Enemy: `Cache Phantom`, 220 HP)*
- *[Animasi: Bayangan bercahaya cyan-merah menyusun ulang kubus memori menjadi tubuh raksasa tanpa wajah.]*
- **ARIA *(Boss Briefing)*:** *"Cache Phantom menyembunyikan inti aslinya di dalam List target dan Dictionary kunci! Kita bisa menggabungkan `for elemen in nama_list:` untuk mengiterasi seluruh isi list satu per satu tanpa repot memakai `range()`, lalu mengambil kunci Dictionary dengan `dict["nama_key"]`!"*

### Fase 1 Boss (`220 → 110 HP`) — *Code Chips*: Akses Dictionary & List
- **Bantuan Materi (`?`):** *"Jika `meta["weak"]` bernilai `2`, maka `cores[meta["weak"]]` sama dengan `cores[2]` (elemen ketiga)."*
- **Soal:** Diketahui `cores = [20, 45, 90]` dan `meta = {"weak": 2}`. Bagaimana mengambil nilai `90` menggunakan `meta["weak"]`?
  `target = cores[____]`
- **Pilihan Chips:** `meta["weak"]` *(Benar)* | `meta[2]` | `"weak"` | `3`

### Fase 2 Boss (`110 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Memory Extraction
- **Bantuan Materi (`?`):** *"`for val in nama_list:` mengambil setiap angka dari list ke dalam `val`. Akses dictionary selalu menggunakan kunci string di dalam kurung siku: `key_data["power"]`."*
- **Instruksi:** Lengkapi perulangan untuk menjumlahkan seluruh energi memori dalam list `nodes` serta mengakses nilai `"power"` pada dictionary `key_data`.
```python
nodes = [30, 40, 50]
key_data = {"code": "0xFA", "power": 80}
total = 0

for val in ____:
    total += val

final_hit = total + key_data[____]
print("Vault Hit:", final_hit)
```
- **Code Chips Tersedia:** `nodes` | `"power"` | `key_data` | `"code"` | `3`
- **Hint Laptop:** *"Iterasi langsung atas list `nodes`, lalu ambil nilai `80` dari dictionary dengan key string `\"power\"`."*
- **Validasi:** `total == 120`, `final_hit == 200`, `stdout` memuat `Vault Hit: 200`.
- **Animasi Finisher:** Kubus memori meledak rapi dan kembali ke rak arsip kota; Cache Phantom lenyap!

---

## Act 5 — Resolution & Cliffhanger

- **ARIA:** *"Seluruh arsip bangunan kota berhasil dipulihkan! Dan lihat log yang baru terbuka... **Dr. Arka ternyata masih hidup — terjebak di Blueprint Citadel**, namun sebelumnya kita harus melewati **Echo Relay Tower** yang kini dikuasai Vex!"*
- **Reward:** `+1000 XP` | `+1500 Points` | **Loot Box (`CLAIM >>`)**.
