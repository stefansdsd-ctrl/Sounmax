# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes: Regen, Spits, Thuisavond (+ Treinwerk, Drukke NS, OV-chip, Swapfiets, Collegezaal, Gesprek, Marktplein, Basic-Fit)
- Auto-scene: Buienradar/KNMI → regen; 9292/vertrektijden → spits
- Extra scenes zichtbaar in groepenfilter
- Adaptive EQ-hints voor regen, spits, thuisavond

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
