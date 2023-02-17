# Tubes1_greed.axio
TUGAS BESAR 1 <br>
IF2211 STRATEGI ALGORITMA <br>
Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan “Galaxio” <br>

## Daftar Isi

- [Penjelasan Ringkas Program](#penjelasan-ringkas-program)
- [Penjelasan Algoritma](#penjelasan-algoritma)
- [Pre-Requisite](#pre-requisite)
- [Cara Menjalankan Program](#cara-menjalankan-program)
- [Cara Menampilkan Hasil Permainan](#cara-menampilkan-hasil-permainan)
- [Screenshot program](#screenshot-program)
- [Struktur Program](#struktur-program)
- [Kontributor](#kontributor)

## Penjelasan Ringkas Program

Galaxio adalah sebuah game battle royale yang mempertandingkan beberapa bot kapal secara bersamaan. Tujuan dari permainan adalah mempertahankan bot dari serangan lawan maupun obstacle pada peta hingga akhir permainan. Agar dapat memenangkan pertandingan, setiap bot harus mengimplementasikan strategi tertentu untuk dapat memenangkan permainan. Dalam program ini akan diimplementasikan strategi pergerakan bot kapal dalam permainan Galaxio dengan menggunakan strategi greedy. Strategi Greedy digunakan untuk menentukan aksi dan arah pergerakan bot pada setiap tick atau ronde. Aksi yang dapat dilakukan bot berupa pergerakan kapal, aksi untuk menyerang, maupun aksi untuk bertahan.

## Penjelasan Algoritma

Galax

## Pre-Requisite
* Java (minimal Java 11), dapat diunduh melalui `https://www.oracle.com/java/technologies/downloads/#java`
* IntelIiJ IDEA, dapat diunduh melalui `https://www.jetbrains.com/idea/`
* NodeJS, dapat diunduh melalui `https://nodejs.org/en/download/`
* .Net Core 3.1, dapat diunduh melalui `https://dotnet.microsoft.com/en-us/download/dotnet/3.1`

## Cara Menjalankan Program
1. Pastikan jumlah bot yang tertuang pada program sama dengan jumlah bot yang ingin dimainkan. <br>
Konfigurasi jumlah bot dapat dilakukan dengan mengubah `Bot Count` pada file JSON `appsettings.json` dalam folder `runner-publish` dan `engine-publish`
2. Buka terminal baru pada folder runner-publish <br>
atau ketikkan `cd ./runner-publish/` pada terminal
3. Masukkan perintah `dotnet GameRunner.dll` pada terminal untuk menjalankan Runner
4. Buka terminal baru pada folder engine-publish <br>
atau ketikkan `cd ./engine-publish/` pada terminal
5. Masukkan perintah `dotnet Engine.dll` pada terminal untuk menjalankan Engine
6. Buka terminal baru pada folder logger-publish <br>
atau ketikkan `cd ./logger-publish/` pada terminal
7. Masukkan perintah `dotnet Logger.dll` pada terminal untuk menjalankan Logger
8. Buka terminal baru sejumlah bot yang ingin dimainkan
9. Jalankan seluruh bot yang ingin dimainkan dengan memasukkan perintah `java -jar {path-JAR-bot}` pada setiap terminal tersebut
10. Jika permainan berhasil diselenggarakan, setelah permainan selesai riwayat permainan akan tersimpan pada 2 file JSON `GameStateLog_{Timestamp}` dalam folder `logger-publish`.

## Cara Menampilkan Hasil Permainan

1. Pastikan sudah melakukan kompilasi pada program
2. Pada root directory jalankan command `./main`
3. Jika berhasil di run, akan muncul splash screen

## Screenshot Program

![BNMO Program](./program.jpg)

## Struktur Program

```bash
└───Tubes_greed.axio
    ├───adt
    │   ├───kulkas
    │   └───waktu
    ├───buy
    └───undoredo
```

## Kontributor

1. 13521081 Bagas Aryo Seto
2. 13521077 Husnia Munzayana
3. 13521133 Cetta Reswara Parahita
