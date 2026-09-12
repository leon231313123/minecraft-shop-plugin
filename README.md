# MinecraftShop - Modernes Shop-Plugin für Paper/Spigot

🎮 Ein hochperformantes, modernes Shop-Plugin mit schnellem Kauf-System und schöner GUI.

## Features ✨

- ✅ **Schnelle Käufe** - Shop bleibt offen für wiederholte Käufe
- ✅ **5 Kategorien** - End, Nether, Gear, Food, Shards & Chat
- ✅ **Vault Integration** - Volle Economy-Unterstützung
- ✅ **Schöne GUI** - Modern, übersichtlich und clean
- ✅ **Config-basiert** - Alle Preise und Items konfigurierbar
- ✅ **Performance** - Optimiert für viele gleichzeitige Spieler

## Installation 🔧

### Voraussetzungen:
- Java 11+
- Paper/Spigot 1.20.1+
- Vault Plugin
- Eine Economy-Plugin (z.B. EssentialsX, CMI)

### Installation:

1. **Maven kompilieren:**
```bash
mvn clean package
```

2. **JAR in plugins/ Ordner kopieren:**
```bash
cp target/MinecraftShop-1.0.0.jar /path/to/server/plugins/
```

3. **Server neustarten:**
```bash
/restart
```

4. **Config anpassen (optional):**
```bash
vim plugins/MinecraftShop/config.yml
```

## Verwendung 🛍️

### Spieler-Command:
```
/shop
```

### Permissions:
- `minecraftshop.use` - Erlaubt den Zugriff auf /shop (default: true)
- `minecraftshop.admin` - Admin-Befehle (default: op)

## Kategorien 📦

### 1. ⚔ Gear Shop
- Totem of Undying ($400)
- Ender Pearl ($50)
- Golden Apple ($150)
- Enchanted Golden Apple ($500)
- Experience Bottle ($100)
- End Crystal ($30.000)
- Respawn Anchor ($30.000)
- Glowstone ($100)
- Obsidian ($100)
- Ender Chest ($250)

### 2. 🌌 End Shop
- End Crystal ($30.000)
- Dragon Egg ($50.000)
- Shulker Shell ($300)

### 3. 🔥 Nether Shop
- Respawn Anchor ($30.000)
- Crying Obsidian ($500)
- Ancient Debris ($2.000)

### 4. 🍗 Food Shop
- Cooked Beef ($10)
- Golden Carrot ($50)
- Glow Berries ($15)

### 5. 💎 Shards & Chat
- Amethyst Shard ($200)

## Konfiguration ⚙️

Alle Preise und Items können in `config.yml` angepasst werden:

```yaml
shop:
  categories:
    gear:
      name: "⚔ Gear Shop"
      items:
        totem_of_undying:
          material: "TOTEM_OF_UNDYING"
          price: 400.0
          amount: 1
```

## Entwicklung 🛠️

### Projekt-Struktur:
```
src/main/java/com/shop/minecraft/
├── MinecraftShop.java          # Main Plugin Klasse
├── commands/
│   └── ShopCommand.java        # /shop Command
├── listeners/
│   └── InventoryListener.java  # Inventory Click Handler
└── manager/
    ├── ShopManager.java        # Shop-Logik
    └── ConfigManager.java      # Config-Management
```

### Maven Build:
```bash
mvn clean package
```

## Bugs & Support 🐛

Bei Fehlern oder Fragen: Issues auf GitHub erstellen!

## Lizenz 📄

MIT License - Frei nutzbar und erweiterbar!

---

**Viel Spaß mit dem Shop-Plugin! 🎉**
