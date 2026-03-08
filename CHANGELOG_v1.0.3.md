# 📜 Stick to Slick v1.0.3 - Changelog

Modun v1.0.3 versiyonu, **"Görsel Mükemmellik ve Premium His"** temasıyla geliştirildi. Bu güncelleme ile modun RPG atmosferi, yeni nesil görsel efektler ve modern UI tasarımlarıyla tamamen yenilendi.

---

### 🎨 Görsel ve Arayüz Devrimi (Premium UI/UX)
*   **Modern Glassmorphism GUI:** Silah yükseltme arayüzü (`/ss gui`) artık tamamen şeffaf, parlayan kenarlı **"Glass"** tasarımına sahip.
*   **3D Silah Sergisi:** Yükseltme ekranının ortasına, silahın kendi ekseni etrafında dönen ve yavaşça yüzen 3 boyutlu bir sergi katmanı eklendi.
*   **Dinamik Katman Sistemi:** GUI yerleşimi, metinlerin ve butonların birbirine binmesini önleyecek şekilde kolon sistemine geçirilerek optimize edildi.
*   **Düzeltilmiş Pivot Ekseni:** Silah modellerinin GUI içerisindeki dönüş ekseni (pivot point) tam merkeze alındı, yalpalamalar giderildi.

### ⚔️ Silah Kademe Sistemi (Weapon Tiers)
Silah gelişimini görselleştirmek için 7 farklı kademe eklendi:
1.  **Starter (0-5):** Klasik gri tema.
2.  **Primal Age (5+):** {@color #27AE60}Zümrüt yeşili doğa teması.
3.  **Iron Age (10+):** {@color #ECF0F1}Parlak gümüş/beyaz tema.
4.  **Specialization (15+):** {@color #3498DB}Safir mavisi elektrik teması.
5.  **Masterworks (20+):** {@color #9B59B6}Ametist moru büyü teması.
6.  **Dark Age (25+):** {@color #8E44AD}Kan kırmızısı karanlık tema.
7.  **Legendary (30+):** {@color #F1C40F}Altın ve alevli efsanevi tema.

### 🌟 Epik Görsel Efektler (VFX)
*   **Tier Particles:** Silahlar elinizde tutulurken kademesine özel ambiyans partikülleri yayar (Mavi şimşekler, altın parıltılar, yeşil doğa efektleri vb.).
*   **Seviye Atlama Fanfarı:** Seviye atlandığında tüm ekran kademe renginde parlar (**Vignette flash**) ve ekranda hareketli **"SEVİYE ATLADI!"** yazısı belirir.
*   **XP HUD:** Ekranın alt-orta kısmında, silahın kademe adını ve seviyesini gösteren, akıcı dolan şık bir XP çubuğu eklendi.

### 🛠 Teknik İyileştirmeler ve Hata Giderimleri
*   **Gelişmiş Tooltips:** Eşya açıklama kutuları (Tooltip) renkli başlıklar ve görsel `[▎▎▎▎]` ilerleme çubuklarıyla daha okunaklı hale getirildi.
*   **Text Wrapping:** Arayüzdeki uzun yetenek açıklamaları artık otomatik olarak alt satıra geçer, butonlarla çakışmaz.
*   **Ağ Optimizasyonu:** Seviye atlama efektlerinin sunucudan tüm istemcilere kusursuz senkronize olması için `S2CLevelUpVfxPacket` eklendi.
*   **Performans:** Partikül sistemi FPS dostu olacak şekilde optimize edildi.

---

**Build Modeli:** `sticktoslick-1.0.3.jar`  
**Hedef Sürüm:** Minecraft 1.20.1 (Forge)
