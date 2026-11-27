# MLBB Draft

App Android untuk baca draft: cadangan pick/counter, build, anggaran peluang menang, cadangan ban, dan analisis komposisi. Tiada benda lain.

## Build APK (tanpa Android Studio)
1. Push projek ni ke repo GitHub (branch `main`).
2. Buka tab **Actions** > **Build APK** (jalan sendiri bila push, atau tekan *Run workflow*).
3. Bila siap, download artifact `mlbb-draft-apk`, unzip, pasang `app-debug.apk` kat phone.

## Data
`app/src/main/assets/heroes.json` ialah DATA CONTOH. Jana semula dari `gen_heroes.py`, atau edit JSON terus.
Medan: id, name, roles, damage, cc, tank, early, late, wr, strong, weak, build, note.

## Belum ada
- Role/CC/build untuk kebanyakan hero
- Auto detect draft dari skrin (fasa 2)
- Floating overlay masa main
