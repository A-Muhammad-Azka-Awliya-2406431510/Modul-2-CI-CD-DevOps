## Refleksi (Module 1 - Coding Standards)

Bagian ini adalah refleksi saya setelah mengecek ulang source code dan menilai penerapan standar dari modul.

### 1) Clean Code

- **Meaningful names**: Saya memakai penamaan yang eksplisit seperti `ProductController`, `ProductService`, `ProductRepository`, dan method seperti `findById`, `findAll`, `update`.
- **Function dan Abstraksi yang kecil**: Saya memisahkan layer `controller/service/repository` agar setiap method punya tanggung jawab jelas (seperti routing dan penyimpanan).
- **Comments**: Saya menghindari komentar yang hanya mengulang isi kode. Untuk memperjelas maksud kode, saya menggunakan nama variabel/method yang mudah dipahami.
- **Objek dan Struktur Data**: Saya memodelkan domain sederhana lewat `Product` sebagai data object, sedangkan behavior/logic dipusatkan di service/repository.
- **Error handling**: Saya menangani kasus produk tidak ditemukan (mis. saat edit) dengan _redirect_ ke halaman list.

**Yang perlu saya perbaiki**
- Saya masih mengembalikan `null/false` untuk error/ketidakadaan data. Ke depan, saya ingin mengganti pola ini menjadi exception yang jelas (mis. `ProductNotFoundException`) + `@ControllerAdvice` agar alur error lebih rapi dan konsisten.
- Saya ingin merapikan konsistensi penamaan variabel koleksi (mis. `allProduct` → `allProducts`) supaya lebih mudah dibaca.

### 2) Git Flow

Dalam mengerjakan tutorial dan _exercise_ modul, saya akan menggunakan **feature branch workflow**:
- Saya membuat branch per fitur (Untuk refleksi 1, baru `feature/edit-product`, `feature/delete-product`).
- Saya melakukan code review untuk kode di setiap branch sebelum merge ke `main`.
- Saya menjaga commit kecil, fokus, dan mudah dipahami.

### 3) Secure Coding

Mengacu pada kategori praktik secure coding di modul (Authentication, Authorization, Input Validation, Output Encoding), refleksi saya:

- **Authentication & Authorization**: Aplikasi ini belum memiliki login/role. Jika aplikasi berkembang, saya akan menambahkan autentikasi dan pembatasan akses.
- **Input data validation**: Saat ini validasi belum ketat (mis. `productName` kosong, `productQuantity` negatif). Nantinya, saya akan menambahkan Validation (`@Valid`) pada model + constraint seperti `@NotBlank` dan `@Min(0)`, lalu menampilkan pesan error di form.
- **Output data encoding**: Saya memakai `th:text` pada Thymeleaf untuk menampilkan data sehingga output di-escape (mengurangi risiko XSS) dibanding mencetak HTML mentah.

**Yang perlu saya perbaiki**
- Saya akan mengganti operasi hapus menjadi non-GET + proteksi CSRF.
- Saya akan menambahkan pembatasan/normalisasi input (trimming nama, batas panjang, dan tipe angka).
