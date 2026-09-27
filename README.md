# Halaman Pemesanan Tiket

Project ini merupakan implementasi halaman pemesanan tiket menggunakan **Jetpack Compose** dengan menerapkan **State Hoisting**, **LaunchedEffect**, dan **rememberSaveable**.

## State Hoisting

State dikelola oleh **Parent Composable**, yaitu:

1. Harga Tiket
2. Jumlah Tiket
3. Nama Pembeli Tiket

## LaunchedEffect

`LaunchedEffect` digunakan untuk menampilkan perubahan status pada proses pemesanan tiket, yaitu:

- **Status: Nama Masih Kosong**
- **Status: Memproses pesanan.........**
- Setelah 5 detik, status berubah menjadi **Status: Tiket telah dipesan**

## rememberSaveable

`rememberSaveable` digunakan untuk mempertahankan data atau state ketika terjadi perubahan konfigurasi pada perangkat, seperti rotasi layar.

## Hasil Tampilan

### 1. Halaman Silahkan Pesan Tiket

![Uploading halaman_awal.png…]()

### 2. Halaman Status Nama Masih Kosong

<img width="1080" height="2424" alt="halaman_nama_kosong" src="https://github.com/user-attachments/assets/296ab42e-ddaa-43ed-bcad-f61b4713c20f" />

### 3. Halaman Memproses Pesanan

<img width="1080" height="2424" alt="halama_pesanan_diproses" src="https://github.com/user-attachments/assets/04944b48-f252-4082-9bfe-d679b66da463" /><img width="1080" height="2424" alt="halaman_pesanan_berhasil" src="https://github.com/user-attachments/assets/fc7b7723-0e50-4d40-9887-dd7fcc8e9122" />


### 4. Halaman Tiket Telah Dipesan

![Uploading halaman_pesanan_berhasil.png…]()
