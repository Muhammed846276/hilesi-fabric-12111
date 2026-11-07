# Hilesi (Fabric 1.21.11) - Adim 1

Bu klasor, **sag Shift** ile acilan basit bir menu ve modul altyapisi iceren Fabric istemci modu iskeletidir.

## Su an neler var
- **Sag Shift (`Right Shift`)**: Menuyu acar/kapatir.
- **ClickGUI**: Modulleri listeler, sol tik ile **ON/OFF** yapar.
- **Kill Aura modulu**: Menuye eklidir ve acilip kapatilabilir; ancak bu iskelette **otomatik saldiri mantigi eklenmemistir** (TODO/placeholder). Bir sonraki adimda hedef secimi, menzil/FOV ve gecikme gibi ayarlari adim adim ekleyebiliriz.

## Surum notu (onemli)
`gradle.properties` icindeki `minecraft_version`, `yarn_mappings`, `loader_version` ve `fabric_version` degerleri **1.21.11** icin Fabric'in yayinladigi guncel degerlerle eslesmelidir. Dogru surumleri almak icin: https://fabricmc.net/develop/

Surumu degistirmek istediginde sadece `gradle.properties` ve gerekirse `fabric.mod.json` icindeki `depends.minecraft` alanini guncellemen yeterli olur.

## Derleme (genel)
Termux'ta Fabric mod derlemek pratikte zordur (JDK 21 + Gradle + bagimliliklar). Genelde PC'de:
```bash
./gradlew build
```
Uretilen jar `build/libs/` altina gelir.

## Yapi
- `src/client/java/com/example/hilesi/client/HilesiClient.java` - giris noktasi, Right Shift keybind
- `src/client/java/com/example/hilesi/screen/ClickGuiScreen.java` - menu/GUI
- `src/client/java/com/example/hilesi/module/` - `Module`, `ModuleManager`, `KillAuraModule`
