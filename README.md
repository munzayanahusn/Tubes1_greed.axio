# Tubes1_greed.axio
TUGAS BESAR 1 <br>
IF2211 STRATEGI ALGORITMA <br>
Pemanfaatan Algoritma Greedy dalam Aplikasi Permainan “Galaxio” <br>

## Daftar Isi

- [Penjelasan Ringkas Program](#penjelasan-ringkas-program)
- [Penjelasan Algoritma](#penjelasan-algoritma)
- [Pre-Requisite](#pre-requisite)
- [Cara Menjalankan Program](#cara-menjalankan-program)
- [Cara Visualisasi Hasil Permainan](#cara-visualisasi-hasil-permainan)
- [Screenshot Hasil Pengujian](#screenshot-hasil-pengujian)
- [Struktur Program](#struktur-program)
- [Kontributor](#kontributor)

## Penjelasan Ringkas Program

Galaxio adalah sebuah game battle royale yang mempertandingkan beberapa bot kapal secara bersamaan. Tujuan dari permainan adalah mempertahankan bot dari serangan lawan maupun obstacle pada peta hingga akhir permainan. Agar dapat memenangkan pertandingan, setiap bot harus mengimplementasikan strategi tertentu untuk dapat memenangkan permainan. Dalam program ini akan diimplementasikan strategi pergerakan bot kapal dalam permainan Galaxio dengan menggunakan strategi greedy. Strategi Greedy digunakan untuk menentukan aksi dan arah pergerakan bot pada setiap tick atau ronde. Aksi yang dapat dilakukan bot berupa pergerakan kapal, aksi untuk menyerang, maupun aksi untuk bertahan.

## Penjelasan Algoritma

Algoritma greedy yang digunakan mengombinasikan berbagai paradigma parameter, antara lain obstacle, defense, powerups, size, dan attacking other players. Algoritma akan menetapkan target dengan prioritas player terdekat, superfood terdekat, atau makanan terdekat terlebih dahulu. Kemudian, akan diperhatikan obstacle-obstacle yang dapat dihindari, dapat juga digunakan powerups untuk mempercepat penghindaran. Obstacle seperti supernova, asteroid fields, dan teleporter tidak akan diimplementasikan karena efek yang ditimbulkan hanya sedikit atau frekuensi kemunculan yang jarang. Obstacle gas cloud sangat perlu dihindari dengan berjalan ke target terdekat secepat mungkin memanfaatkan afterburner. Kemudian menghindari world bound diprioritaskan dengan mengubah arah player menjadi ke arah pusat menjauhi boundaries yang ada. Terakhir menghindari musuh yang jauh lebih besar dilakukan dengan mengubah arah menjadi searah tangen posisi musuh dan bot. Obstacle berupa serangan torpedo dari musuh lain dapat di-counter dengan melakukan attack torpedo balik. Apabila semua obstacle telah berhasil dihindari atau tidak ada obstacle yang perlu dihindari, selanjutnya dapat dilakukan targeting pada target yang telah ditentukan di awal.

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

## Cara Visualisasi Hasil Permainan

1. Lakukan ekstrak pada file zip Galaxio dalam folder “visualiser” sesuai dengan OS device yang digunakan
2. Jalankan aplikasi Galaxio
3. Buka menu “Options”
4. Salin path folder “logger-publish” kalian pada “Log Files Location”, lalu pilih “Save”
5. Buka menu “Load”
6. Pilih file JSON yang ingin diload pada “Game Log”, lalu “Start”
7. Setelah masuk ke visualisasinya, kalian dapat melakukan start, pause, rewind, dan reset
8. Silahkan buat bot terbaik kalian dan selamat menikmati permainan

## Screenshot Hasil Pengujian

![Galaxio greed.axio Program](./program.jpg)

## Struktur Program

```bash
└───Tubes_greed.axio
    ├───src/main/java
    │   ├───enums
    │   │   └───ObjectTypes.java
    │   │   └───PlayerActions.java
    │   └───models
    │   │   └───GameObject.java
    │   │   └───GameState.java
    │   │   └───GameStateDto.java
    │   │   └───PlayerAction.java
    │   │   └───Position.java
    │   │   └───World.java
    │   └───services
    │   │   └───BotServices.java
    │   └───Main.java
    ├───target
    │   └───greed.axio.jar
    ├───program.jpg
    └───README.md
```

## Kontributor

13521081 Bagas Aryo Seto
13521077 Husnia Munzayana
13521133 Cetta Reswara Parahita
