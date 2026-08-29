# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Functies
- 10-band equalizer + custom presets (Room)
- Favoriete en recente presets (snelbalk)
- Luister-scenes (pendelen, sport, nacht, fiets, bibliotheek, vergadering, rust, trein, koken, kids)
- Auto-scene op tijdstip
- Veilig volume (60/70/80%) + nachtwacht na 22:00
- Crossfeed, mono-mix, EQ-lock, A/B-vergelijking
- Slaaptimer 15/30/60/90/120 min met fade-out
- Luisterdosis-tracker met pauzewaarschuwing
- Gehoortest → L+R gemiddelde EQ-compensatie + echte score
- Gemini AI akoestische tuner
- YouTube Music web/native
- EQ delen / importeren via klembord of Android share-sheet
- Backup/restore van alle custom presets als één JSON
- Per-app EQ (Spotify vs YT Music vs andere spelers)
- BT-headset detectie + codec hints (LDAC / aptX Adaptive)
- Now Playing auto-EQ
- Adaptieve loudness op basis van mediavolume
- Quick Settings-tegels: DSP + scene
- Persistente DSP-notificatie
- Homescreen-widget: DSP toggle + volgende scene
- Zoek koptelefoon (L/R pieptonen)
- Headset-batterij in de statusbalk
- Auto-pauze DSP bij Bluetooth-disconnect
- Weekdosis naast dagelijkse luisterminuten
- EQ-geheugen per headset
- Stereo-breedte / spatializer-slider
- Hardware Spatializer (Android 12/13+)
- Live RSSI stuurt LDAC 990/660/330 automatisch
- DSP pauzeert tijdens een telefoongesprek en hervat daarna
- EQ-undo (laatste 12 curves)
- Batterij-saver: bij ≤18% automatisch LDAC 330 + lagere loudness
- Scenes: Thuiswerk, Auto, Inslapen

## Volgende verbeteringen
- Wear OS-tegel voor scene + DSP
- GATT/LE Audio RSSI i.p.v. discovery-fallback
- Spatializer-head-tracking waar de OEM dat toestaat
- Widget met batterij + slaaptimer

## Bouwen
Android Studio + JDK 17. Open de root en sync Gradle.
API-sleutel voor Gemini: zie `.env.example`.
