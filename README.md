# FoundIt: Pencarian Barang Hilang

Aplikasi desktop Java Swing + MySQL/MariaDB (JDBC), memakai skema `foundit_web` (file `sql/foundit_web.sql`).

## Struktur proyek

```
FoundIt/
├── sql/foundit_web.sql              # dump database Anda (tanpa diubah)
├── db.properties                    # host, port, nama DB, user, password
├── lib/                             # taruh mysql-connector-j-x.y.z.jar di sini
└── src/com/foundit/
    ├── Main.java
    ├── config/DatabaseConnection.java
    ├── util/PasswordUtil.java
    ├── model/    Identifiable, BaseEntity, Account(abstract) -> User, Admin,
    │             Category, Item, Claim, ItemType, ItemStatus, ClaimStatus
    ├── dao/      CrudDAO<T>, AbstractDAO<T>, DAOException, Category/User/Admin/Item/ClaimDAO
    ├── service/  AbstractService<T>, AccountService<T>, Validator, *Service, *Exception
    └── view/     MainFrame, AbstractCrudPanel<T>, AccountPanel<T>,
                  Item/Claim/Category/User/AdminPanel, Option, UiUtil
```

## Cara menjalankan (contoh dengan XAMPP)

1. **Pastikan JDK 17 atau lebih baru** terpasang: `java -version` dan `javac -version`.
2. **Nyalakan MySQL** di XAMPP Control Panel (tombol *Start* pada baris MySQL).
3. **Import database.** Dump Anda tidak berisi `CREATE DATABASE`, jadi buat dulu databasenya:
   - Lewat phpMyAdmin: *New* -> nama `foundit_web`, collation `utf8mb4_general_ci` -> *Create*,
     buka database itu -> tab *Import* -> pilih `sql/foundit_web.sql` -> *Go*.
   - Atau lewat terminal:
     ```bash
     mysql -u root -e "CREATE DATABASE foundit_web CHARACTER SET utf8mb4"
     mysql -u root foundit_web < sql/foundit_web.sql
     ```
4. **Cek `db.properties`**: default `localhost:3306`, database `foundit_web`, user `root`, password kosong
   (default XAMPP). Ubah bila berbeda.
5. **Unduh driver JDBC** `mysql-connector-j-8.x.x.jar` (pilih "Platform Independent", ambil .jar di dalam ZIP-nya) dari
   https://dev.mysql.com/downloads/connector/j/ dan taruh di folder `lib/`.
6. **Kompilasi dan jalankan** dari folder `FoundIt/`:

   Windows (CMD)
   ```bat
   mkdir out
   dir /s /b src\*.java > sources.txt
   javac -encoding UTF-8 -cp "lib/*" -d out @sources.txt
   java -cp "out;lib/*" com.foundit.Main
   ```
   Linux / macOS
   ```bash
   mkdir -p out
   javac -encoding UTF-8 -cp "lib/*" -d out $(find src -name "*.java")
   java -cp "out:lib/*" com.foundit.Main
   ```

### Lewat IDE
- **IntelliJ IDEA**: File > New > Project from Existing Sources > pilih folder `FoundIt`; tandai `src` sebagai
  *Sources Root*; File > Project Structure > Libraries > **+** > Java > pilih jar connector.
  Di *Run Configuration*, isi **Working directory** dengan folder `FoundIt` (agar `db.properties` terbaca),
  lalu run `com.foundit.Main`.
- **NetBeans / Eclipse**: buat project Java kosong, salin isi `src/`, tambahkan jar ke *Libraries / Build Path*,
  set working directory ke folder proyek.

## Aturan bisnis
- Tipe **Hilang**: pelapor (kolom `users_id`) wajib. Tipe **Ditemukan**: nama penemu wajib.
- Alur status barang (tombol di tab Barang): Pending -> **Terbitkan** / **Tolak Laporan**; Terbit -> **Tandai Selesai**;
  Terbit/Selesai/Ditolak -> **Arsipkan**.
- Klaim hanya untuk barang tipe Ditemukan berstatus Terbit, tidak oleh pelapornya sendiri, dan satu pengguna hanya
  boleh punya satu klaim aktif per barang.
- **Setujui** klaim (pilih admin pemroses dulu) -> barang menjadi Selesai, klaim Menunggu lain pada barang itu otomatis
  Ditolak (satu transaksi). **Tolak** -> klaim Ditolak. **Batalkan** -> klaim Dibatalkan.
- Klaim hanya bisa diubah saat Menunggu. Menghapus klaim Disetujui mengembalikan barang Selesai -> Terbit.
- ID dibuat otomatis berurutan (ITM011, CLM004, ...).
- Foto barang/lampiran hanya menyimpan **nama file** (kolom `foto` wajib; bila kosong diisi `default.jpg`).
- Password akun baru disimpan sebagai SHA-256. Data contoh Anda berisi password dummy (`HASH_DUMMY_001`), jadi
  kosongkan kolom password saat mengubah akun lama agar tidak berubah.
