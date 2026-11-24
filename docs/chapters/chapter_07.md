# Chapter 7 — *The Final Kernel* (Grand Finale & Multi-Ending)

> **Lokasi:** `Zero-Point Kernel Core` (Inti Ruang Hampa Sumber *The Great Runtime Error*)  
> **Konsep Python:** Algoritma Gabungan (Filtering, Akumulasi, Max/Min, Multi-Function Debugging, & OOP/Control Flow Terpadu)  
> **Musuh:** Null Vanguard (`130 HP`) & **Final Boss: The Null Kernel (`0xDEADCODE`)** (`350 HP`)  
> **Reward:** `+2500 XP` | `+3500 Points` | **Trophy Ending + Final Loot Box**  
> **Aset Visual Dibutuhkan:** `bg_ch7_null_kernel`, `enemy_null_vanguard`, `boss_null_kernel_idle`, `boss_null_kernel_death`

---

## Act 1 — Di Ambang `0xDEADCODE`

### Scene 1.1: Inti Realitas yang Retak *(Background: `bg_ch7_null_kernel`)*
- *[Latar ruang angkasa data raksasa; pilar-pilar hexadecimal merah dan ungu melayang menuju pusaran hitam di tengah.]*
- **ARIA:** *"Kita sampai di titik nol: **Zero-Point Kernel Core**. Di sinilah sumber `The Great Runtime Error` berada."*
- **ARIA *(Micro-Lesson)*:** *"Di pertarungan puncak ini, kita menggabungkan semua ilmu sebelumnya menjadi **Algoritma Pembersih**: mencari nilai maksimum/minimum dengan membandingkan tiap elemen (`>` untuk terbesar, `<` untuk terkecil), dan menyaring (*filtering*) sinyal positif (`if item > 0:`) di dalam fungsi!"*
- **VEX *(berdiri di samping Hero, melihat pusaran)*:** *"Ini saatnya, Coder. Semua yang kita pelajari — variabel, kondisi, loop, struktur data, fungsi, hingga class — akan diuji di sini."*

### Clue #7 Terbuka (`clue_ch7_kernel`)
> **Strategi Algoritma Final Debugging:**  
> Pecahkan masalah kompleks menjadi langkah kecil (*decomposition*):  
> 1. **Filter** data error dari list (`if val > 0:` / kondisi valid).  
> 2. **Proses** dengan fungsi atau loop bersih (`for`, `def`).  
> 3. **Verifikasi** hasil akhir sebelum mengeksekusi patch ke sistem utama!

---

## Act 2 — Branching Encounter: *Keputusan Nasib Kernel Bumi*

### Scene 2.1: Konsol Pilihan Akhir
- *[Sistem menampilkan dua tombol raksasa di udara: `APPLY ORIGIN PATCH` vs `EXECUTE HARD PURGE`.]*
- **DR. ARKA *(lewat transmisi ARIA)*:** *"Jika kalian menemukan cukup banyak **Secret Clues**, kalian bisa menggabungkan fragmen memorinya untuk menambal kernel tanpa menghapus kota. Jika tidak... hanya penghapusan total yang bisa menghentikan penyebaran error ini!"*

### Branching Choice #7 — *Menentukan Arah Akhir*
| Pilihan Pemain | Alignment | Dampak Sebelum Final Boss |
| :--- | :---: | :--- |
| **A. "Kita perbaiki Kernel tanpa menghapus satu ingatan warga pun (`Patch`)!"** | `+2 Patch` | Jika `Secret Clues >= 5`, membuka kunci skenario **Origin Patch** saat Finisher. Vex memilih mempercayaimu dan bertarung di sisimu! |
| **B. "Risikonya terlalu besar! Kita reset paksa semuanya sebelum menyebar (`Purge`)!"** | `+2 Purge` | Mengaktifkan mode *Overclock* (`+50 FORCE`), mengarahkan alur ke **Ending B (Clean Reboot)** setelah Boss kalah. |

---

## Act 3 — Pre-Battle Micro-Lesson & Skirmish: *Null Vanguard*

### Scene 3.1: Memecah Pola Pengawal Inti
- **ARIA *(Micro-Lesson)*:** *"Ingat logika pencarian nilai maksimum: kita hanya mengganti `best` jika elemen baru **lebih besar** (`if s > best:`). Dan saat menyaring angka positif saja, masukkan aksi penjumlahan `valid += p` di dalam cabang `if p > 0:`!"*

### Tantangan 3.1 — *Bug Hunt (Algorithm Logic Bug)* *(65 HP Damage)*
- **Bantuan Materi (`?`):** *"Untuk mencari nilai terbesar (Max), periksa apakah elemen baru `s > best` (lebih besar dari rekor saat ini), bukan `<`."*
```python
1 | scores = [10, 45, 25, 80]
2 | best = 0
3 | for s in scores:
4 |     if s < best:
5 |         best = s
6 | print("Max:", best)
```
- **Baris Target:** **Baris 4** (tanda `<` mencari nilai terkecil bukan terbesar, padahal `best = 0` dan semua angka positif sehingga `best` tetap `0`! Seharusnya `if s > best:`).

### Tantangan 3.2 — *Predict the Output (Filter & Sum)* *(65 HP Damage)*
- **Bantuan Materi (`?`):** *"Kondisi `if p > 0:` menyaring angka negatif (`-5`, `-2`) sehingga hanya `12`, `18`, dan `10` yang ditambahkan ke `valid`."*
```python
packets = [12, -5, 18, -2, 10]
valid = 0
for p in packets:
    if p > 0:
        valid += p
print(valid)
```
- **Opsi:** `A. 33` | `B. 40` | `C. -7` | `D. 47`
- **Jawaban:** **B. `40`** (`12 + 18 + 10 = 40`; nilai negatif terfilter oleh `if p > 0:`).

---

## Act 4 — Multi-Phase Final Boss Battle: *The Null Kernel (`0xDEADCODE`)*

### Scene 4.1: Manifestasi Error Tertinggi *(Enemy: `The Null Kernel`, 350 HP)*
- *[Screen Shake kuat! Bola kode raksasa bermata crimson membuka jutaan fragmen error.]*
- **THE NULL KERNEL:** `"FATAL_EXCEPTION: HUMAN_LOGIC_INSUFFICIENT. TERMINATING WORLD()..."`
- **ARIA *(Boss Briefing)*:** *"Ini fase terakhir! Gabungkan **Filtering (`if item > 0`)**, **Loop (`for`)**, dan **Fungsi (`def`)** untuk menjumlahkan sinyal bersih lalu memanggil fungsi `stabilize(streams)` sebagai serangan penutup!"*

### Fase 1 Boss (`350 → 175 HP`) — *Code Chips*: Filter & Transformasi List
- **Bantuan Materi (`?`):** *"Bilangan positif adalah nilai yang lebih besar dari nol (`> 0`)."*
- **Soal:** Lengkapi fungsi untuk hanya menjumlahkan energi positif (`> 0`) yang tidak korup:
  `for e in signals: if e ____ 0: clean += e`
- **Pilihan Chips:** `>` *(Benar)* | `<` | `==` | `!=`

### Fase 2 Boss (`175 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Kernel Restoration Protocol
- **Bantuan Materi (`?`):** *"Gabungan `def`, `for`, dan `if`:\n```python\ndef proses(data):\n    total = 0\n    for x in data:\n        if x > 0:\n            total += x\n    return total * 2\n```"*
- **Instruksi:** Bersihkan list `streams = [40, -99, 60, -10, 50]` dari sinyal negatif (`< 0`), hitung total energi bersih melalui fungsi `stabilize(data)`, lalu lipatgandakan 2x untuk menghasilkan `kernel_power = 300`!
```python
def stabilize(data):
    total = 0
    for item in data:
        if item ____ 0:
            total ____ item
    return total ____ 2

streams = [40, -99, 60, -10, 50]
kernel_power = ____(streams)
print("Kernel Power:", kernel_power)
```
- **Code Chips Tersedia:** `>` | `+=` | `*` | `stabilize` | `<` | `=`
- **Hint Laptop:** *"Filter `if item > 0:`, jumlahkan dengan `total += item`, kembalikan `total * 2` (`150 * 2 = 300`), dan panggil `stabilize(streams)`."*
- **Validasi:** Wajib mengandung `def`, `for`, `if`, `return`; `kernel_power == 300`, `stdout` memuat `Kernel Power: 300`.
- **Animasi Finisher:** Cahaya neon ungu & cyan menyapu seluruh layar, merobek inti `0xDEADCODE` hingga langit digital pulih kembali!

---

## Act 5 — Resolusi & 3 Cabang Ending

Berdasarkan skor sepanjang Chapter 1–7, sistem menampilkan adegan epilog sesuai kondisi pemain:

### Ending A — *True Kernel Patch* *(Jika `Patch >= Purge` dan `Secret Clues >= 5`)*
- Fragmen *Secret Clues* bersatu menjadi **Origin Patch**. Langit yang retak menutup dengan tenang; gedung-gedung dari *Memory Vault* kembali ke tempatnya beserta ingatan seluruh warga.
- **VEX:** *"Kau membuktikan sistem yang rusak tidak selalu harus dibuang... Mungkin lain kali aku belajar cara *debug* bersamamu."*
- **ARIA:** *"Kernel Bumi stabil 100%. Selamat, The Mystery Coder!"*

### Ending B — *Clean Reboot* *(Jika `Purge > Patch`)*
- Pemain menjalankan `system.reboot(hard=True)`. Kota terselamatkan dan seluruh monster lenyap, tetapi langit dan bangunan kembali seperti hari pertama tanpa ingatan warga akan *Great Runtime Error*. Hanya pemain dan Vex yang mengingat segalanya.

### Ending C — *Bitwise Rogue* *(Jika `Patch >= Purge` namun `Secret Clues < 5`)*
- *The Null Kernel* berhasil disegel ke dalam *Sandbox* terisolasi. Warga selamat, namun garis retakan tipis ungu masih tampak di cakrawala — menjadi benih tantangan *Season 2 / Mode Endless*.

---

**Reward Akhir:** `+2500 XP` | `+3500 Points` | **Final Boss Loot Box** + Achievement **`Kernel Savior`**!
