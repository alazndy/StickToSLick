# 📜 Stick to Slick v1.1.1 - Changelog

Version v1.1.1 is the **"Weapon Evolution Overhaul"**. This massive update fundamentally changes how the core Stick to Slick weapon tree works, increasing the level cap, adding 30 brand new historical and mythological weapons, and introducing a converging "Hybrid" evolution tree!

---

### 🌟 Massive Arsenal Expansion (30 New Weapons)
*   **Completely New Roster:** Replaced the previous 24 disjointed weapons with a carefully curated list of 30 historical and mythological weapons.
*   **9 Distinct Tiers:** Weapons are now categorized into 9 distinct tiers: Root (Lv1), Base (Lv5), Tier 1 (Lv10), Hybrid 1 (Lv15), Specialization (Lv20), Heavy Hybrid (Lv25), Historical (Lv30), Mythological (Lv40), and Godly (Lv50).
*   **Godly Weapons:** Witness the ultimate power of the Level 50 capstones: *Genesis*, *Spear of Longinus*, and *Atlas' Burden*.
*   **Custom Traits:** Every new weapon has been bestowed with unique, over-the-top Custom Traits (e.g., God Slayer logic, active Earthquake abilities, Void Black Holes).
*   **Placeholder Models:** Added temporary fallback JSON models mapped to vanilla items (Iron Sword, Trident, Bedrock, etc.) to eliminate missing texture issues.

### 🧬 Hybrid Evolution Tree (DAG System)
*   **Converging Paths:** Evolution is no longer a strict 1-to-1 linear tree. Weapons can now branch out and converge later down the line. For example, both *Dirk* and *Arming Sword* can evolve into a *Saber*.
*   **DAG Rendering UI:** The in-game `EvolutionTreeScreen` GUI has been entirely rewritten using a Directed Acyclic Graph (DAG) layout algorithm. It now correctly visually links converging branches seamlessly without repeating weapon nodes on-screen.

### ⚙️ Mechanics & Level Cap
*   **Level 50 Cap:** The maximum weapon level has been raised from 30 to 50, providing a much longer and more rewarding endgame grind.
*   **Rebalanced Stats:** All base stats (Damage, Attack Speed, Durability, Enchant Slots) have been completely rebalanced from the ground up to support the new 1-50 progression curve.

### 🌐 Localization
*   **Full Translation:** The mod is now fully localized. Added complete translation keys, naming, and tooltips for all 30 new weapons in both English (`en_us.json`) and Turkish (`tr_tr.json`).

---

# 📜 Stick to Slick v1.0.3 - Changelog

Version v1.0.3 is centered around **"Visual Excellence and Premium Feel"**. This update completely renovates the mod's RPG atmosphere with next-gen visual effects and modern UI designs.

---

### 🎨 Visual & Interface Revolution (Premium UI/UX)
*   **Modern Glassmorphism GUI:** The weapon upgrade interface (`/ss gui`) now features a completely transparent, glowing-edged **"Glass"** design.
*   **3D Weapon Showcase:** A 3D display layer has been added to the center of the upgrade screen, featuring the weapon rotating around its own axis and floating gently.
*   **Dynamic Layer System:** The GUI layout has been optimized using a column system to prevent text and buttons from overlapping.
*   **Corrected Pivot Axis:** The rotation axis (pivot point) for weapon models within the GUI has been centered, eliminating wobbling.

### ⚔️ Weapon Tier System
7 distinct tiers have been introduced to visualize weapon progression:
1.  **Starter (0-5):** Classic gray theme.
2.  **Primal Age (5+):** Emerald green nature theme.
3.  **Iron Age (10+):** Bright silver/white theme.
4.  **Specialization (15+):** Sapphire blue electric theme.
5.  **Masterworks (20+):** Amethyst purple magic theme.
6.  **Dark Age (25+):** Blood red dark theme.
7.  **Legendary (30+):** Golden and flaming legendary theme.

### 🌟 Epic Visual Effects (VFX)
*   **Tier Particles:** Weapons now emit unique ambient particles based on their tier while held (Blue lightning, golden sparkles, green nature effects, etc.).
*   **Level Up Fanfare:** Upon leveling up, the entire screen flashes with the tier's color (**Vignette flash**) and an animated **"LEVEL UP!"** message appears on screen.
*   **XP HUD:** A sleek, animated XP bar has been added to the bottom-center of the screen, showing the weapon's tier name and level in real-time.

### 🛠 Technical Improvements & Bug Fixes
*   **Enhanced Tooltips:** Item description boxes (Tooltips) have been made more readable with tier-colored headers and visual `[▎▎▎▎]` progress bars.
* **Text Wrapping:** Long trait descriptions in the interface now automatically wrap to the next line, preventing overlaps with buttons.
* **Network Optimization:** Added `S2CLevelUpVfxPacket` to ensure level-up effects are perfectly synchronized from the server to all clients.
* **Performance:** The particle system has been optimized to be FPS-friendly.
* **AOE Fix:** Implemented actual Area-of-Effect logic for Dreadnought Axe (*Fear Aura*), Greatsword (*Explosion*), and Void Crusher. Previously, these effects only targeted a single enemy; they now correctly damage and debuff nearby mobs.

---

**Build Artifact:** `sticktoslick-1.0.3.jar`  
**Target Version:** Minecraft 1.20.1 (Forge)
