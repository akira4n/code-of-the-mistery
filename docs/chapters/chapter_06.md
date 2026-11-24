# Chapter 6 — *Object Awakening*

> **Lokasi:** `The Blueprint Citadel` (Katedral Cetak Biru Objek Wireframe)  
> **Konsep Python (Selaras dengan `.md`):** Object-Oriented Programming (`class`, `__init__`, `self`, Atribut, Method, & Inheritance)  
> **Musuh:** Blueprint Sentinel (`110 HP`) & **Boss: Corrupted Architect (Dr. Arka)** (`280 HP`)  
> **Reward:** `+1500 XP` | `+2200 Points` | **Loot Box** (*Epic Architect Core*)  
> **Aset Visual Dibutuhkan:** `bg_ch6_blueprint_citadel`, `enemy_blueprint_sentinel`, `boss_corrupted_architect`

---

## Act 1 — Katedral Tempat Objek Diciptakan

### Scene 1.1: Dunia Wireframe Putih-Ungu *(Background: `bg_ch6_blueprint_citadel`)*
- **PROTAGONIS:** *"Semua pohon, bangunan, bahkan perisai di tempat ini terlihat seperti sketsa 3D transparan..."*
- **ARIA:** *"Selamat datang di **The Blueprint Citadel**! Di sinilah **Class** — cetak biru dari segala **Object** di dunia ini — didefinisikan! Untuk mengalahkan penjaganya, kamu harus memahami bagaimana `class` menciptakan objek nyata!"*

### Clue #6 Terbuka (`clue_ch6_oop`)
> **Object-Oriented Programming (OOP) Python:**  
> - `class Hero:` adalah cetak biru (*blueprint*).  
> - `def __init__(self, nama):` adalah konstruktor yang dijalankan saat objek dibuat (`h = Hero("Dokja")`).  
> - `self` merujuk pada objek itu sendiri (`self.nama = nama`).  
> - **Method** adalah fungsi di dalam class (mis. `def serang(self):`).  
> - **Inheritance:** `class Boss(Entity):` mewarisi atribut dan method dari `Entity`.

---

## Act 2 — Branching Encounter: *Menyelamatkan Kesadaran Dr. Arka*

### Scene 2.1: Suara Pencipta Sistem
- *[Sosok berjubah wireframe muncul di altar tengah, dikelilingi rantai kode error yang berputar.]*
- **DR. ARKA *(terputus-putus)*:** *"`self.corrupted = True`... Siapa pun kalian... jangan dekati... **The Null Kernel** mengendalikan class utamaku..."*
- **VEX *(muncul di gerbang, terluka tapi membantu memindai)*:** *"Itu Dr. Arka! Dia mencoba membungkus virus ke dalam objek miliknya sendiri agar tidak menyebar ke kota!"*

### Branching Choice #6 — *Cara Memulihkan Class Dr. Arka*
| Pilihan Pemain | Alignment | Konsekuensi Cerita & Gameplay |
| :--- | :---: | :--- |
| **A. "Buat Subclass turunan (`class PureArchitect(Architect)`) untuk meng-override virus!"** | `+1 Patch` | Pendekatan OOP elegan! Membuka **Secret Clue Ch.6** (`Origin Patch Key` dari Dr. Arka) dan menurunkan serangan Boss sebesar `1` damage di Fase 1. |
| **B. "Hancurkan instance objek Dr. Arka saat ini agar virusnya ikut terputus!"** | `+1 Purge` | Memberi `+35 FORCE` di awal battle, namun membuat Dr. Arka melemah drastis setelah battle dan Vex semakin yakin pada jalur *Purge*. |

---

## Act 3 — Skirmish Mini-Puzzle: *Blueprint Sentinel*

### Tantangan 3.1 — *Bug Hunt (Missing `self` Parameter)* *(55 HP Damage)*
```python
1 | class Sentinel:
2 |     def __init__(self, hp):
3 |         self.hp = hp
4 |     def roar():
5 |         print("DEFEND CITADEL!")
```
- **Baris Target:** **Baris 4** (method di dalam class Python wajib menerima parameter pertama `self`: `def roar(self):`).

### Tantangan 3.2 — *Predict the Output (Instance & Method)* *(55 HP Damage)*
```python
class Shield:
    def __init__(self, armor):
        self.armor = armor
    def boost(self):
        return self.armor + 20

s = Shield(50)
print(s.boost())
```
- **Opsi:** `A. 50` | `B. 70` | `C. 20` | `D. TypeError`
- **Jawaban:** **B. `70`**.

---

## Act 4 — Multi-Phase Boss Battle: *Corrupted Architect*

### Scene 4.1: Duel Blueprint *(Enemy: `Corrupted Architect`, 280 HP)*
- **CORRUPTED ARCHITECT:** `"INSTANTIATING_METEOR()... OVERRIDE_REALITY()..."`
- **ARIA:** *"Untuk memurnikan class Arka, ciptakan objek **`Purifier`** yang mewarisi kekuatanmu dan panggil method `execute()`!"*

### Fase 1 Boss (`280 → 140 HP`) — *Code Chips*: Konstruktor `__init__`
- **Soal:** Lengkapi konstruktor agar menyimpan atribut `power` ke dalam instance objek:
  `def ____(self, power): self.power = power`
- **Pilihan Chips:** `__init__` *(Benar)* | `init` | `__main__` | `class`

### Fase 2 Boss (`140 → 0 HP`) — *Fill-in-the-Blank `MAIN.PY`*: Object Instantiation
- **Instruksi:** Lengkapi class `Purifier`, instansiasi objeknya dengan `power = 150`, dan panggil method `purify()` untuk membebaskan Dr. Arka!
```python
class Purifier:
    def __init__(self, power):
        ____.power = power

    def purify(self):
        return self.power * 2

blade = ____(150)
dmg = blade.____()
print("Purify:", dmg)
```
- **Code Chips Tersedia:** `self` | `Purifier` | `purify` | `class` | `__init__`
- **Hint Laptop:** *"Simpan ke `self.power`, buat objek dengan `Purifier(150)`, lalu panggil `blade.purify()` sehingga `dmg == 300`."*
- **Validasi:** Wajib mengandung `class`, `def`, `self`; `dmg == 300`, `stdout` memuat `Purify: 300`.
- **Animasi Finisher:** Lingkaran sihir wireframe bersinar ungu-emas — rantai error pada Dr. Arka hancur berkeping-keping!

---

## Act 5 — Resolution & Cliffhanger

- **DR. ARKA *(tersadar)*:** *"Terima kasih... Kalian sudah menguasai sintaks hingga OOP. Tapi waktu kita hampir habis — **The Null Kernel** di Chapter 7 tidak bisa dikalahkan dengan satu class saja. Ia berada di **Zero-Point Core**... dan seluruh kota mulai menghitung mundur menuju `SystemExit`."*
- **Reward:** `+1500 XP` | `+2200 Points` | **Loot Box (`CLAIM >>`)**.
