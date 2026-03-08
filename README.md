# 🗡️ Stick to Slick

**Minecraft'a derin bir RPG silah ilerleme sistemi ekler.**

Bu modda sandıklardan kılıç aramazsın veya elmas bulur bulmaz en iyi silaha sahip olamazsın. Silahın seninle birlikte savaşır, kan döker, tecrübe kazanır ve senin seçimlerine göre şekillenerek oyun sonu bir **Tanrısal Silaha** dönüşür.

---

## ⚔️ Temel Mekanikler

### 1. Başlangıç Çubuğu (Starter Stick)
Oyuna ilk girdiğinde envanterinde basit bir **Starter Stick** bulunur. Zayıf ama içinde gizli bir potansiyel barındırır.

### 2. XP & Seviye Sistemi
| Yaratık | XP |
|---------|-----|
| 🐲 Ender Dragon | 100 |
| 💀 Wither | 80 |
| 🛡️ Warden | 40 |
| 👁️ Enderman | 15 |
| 🧙 Witch | 10 |
| 💣 Creeper | 5 |
| 🧟 Zombie | 3 |
| 🐄 Pasif | 1 |

### 3. Evrim Ağacı (30 Seviye, 28 Silah Sınıfı)

```
                        Starter Stick (Lv.1)
                     /      |       |       \
               Hand Axe   Spear   Club    Dagger (Lv.5)
                  |         |       |        |
              Battle Axe   Pike    Mace  Shortsword (Lv.10)
               /    \      / \     / \       |
          Falchion D.Axe Glaive Halb Flail W.Ham  Longsword (Lv.15)
              \     /      \   /    \  /       /  |  \
              Katana      Lance   War Hammer  Rapier Greatsword (Lv.20)
                |           |                   |        |
          Cursed Odachi  Ignis Halb         Windpiercer  Dread.Axe (Lv.25)
                |           |                   |        |
            Muramasa  Spear of Heavens     Excalibur  Void Crusher (Lv.30)
```

### 4. Stat Yükseltme
Her seviyede materyallerle silahını özelleştir:

| Materyal | Stat |
|----------|------|
| 💎 Lapis Lazuli | +Hasar |
| 🔴 Redstone | +Saldırı Hızı |
| 🍬 Şeker | +Koşma Hızı |
| 🟢 Slime Ball | +Geri İtme |
| ⭐ Nether Star | +Büyü Kapasitesi |

### 5. Benzersiz Trait Sistemi
Her silahın kendine özgü bir pasif yeteneği var:

| Silah | Trait | Etki |
|-------|-------|------|
| Dagger | Kritik Vuruş | +%10 kritik şansı/lvl |
| Double Axe | Kasırga | +%20 sweeping hasarı/lvl |
| Katana | Kan Akışı | +%5 lifesteal/lvl |
| Ignis Halberd | Yanma | +2 tick ateş/lvl |
| Excalibur | Kutsama | +1 iyileşme/vuruş/lvl |
| Muramasa | Kan Bedeli | +%20 hasar ama -%5 can/lvl |

Trait'ler silaha özel materyallerle yükseltilebilir.

### 6. Aktif Yetenekler (Lv.25+)
Sağ tık ile güçlü özel saldırılar:
- 🔥 **Ignis Halberd**: Ateş topu fırlatır
- 💨 **Windpiercer**: Hızlı atılma (Dash)
- 🌑 **Void Crusher**: Alan hasarı + yerçekimi darbesi
- ⚡ **Spear of Heavens**: İmlecin olduğu yere yıldırım düşürür

### 7. Ponder GUI
Envanterinde silahının üzerine gel ve **T tuşuna 3 saniye basılı tut** — Yükseltme ekranı açılır:
- 📊 Tüm statlar detaylı gösterim
- ✦ Trait seviyesi ve açıklaması
- ★ Evrim seçenekleri (hangi materyal → hangi silah)
- 🎬 Materyal animasyonu

---

## 📦 Kurulum
1. [Minecraft Forge 1.20.1](https://files.minecraftforge.net/) kur
2. `sticktoslick-1.0.jar` dosyasını `mods/` klasörüne koy
3. Oyunu başlat!

## 🔧 Komutlar
| Komut | Açıklama |
|-------|----------|
| `/ss gui` | Yükseltme ekranını açar |
| `/give @s sticktoslick:starter_stick` | Başlangıç çubuğu verir |

## 🤝 Uyumluluk
- **Better Combat**: Özel silah animasyonları desteklenir
- **Minecraft 1.20.1** (Forge)

---

## 📜 Lisans
MIT License — Özgürce kullan, değiştir, dağıt.
