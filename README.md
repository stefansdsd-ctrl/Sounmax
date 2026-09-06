# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes: OV-overstap, Kapper, Museum, IKEA, Bouw/wegwerk
- Focus-25: 1-tap deep work + slot, daarna oorpauze
- Waarom-chip: toont welke advisor de scene koos (weer, pendel, agenda…)
- Undo laatste scene + 1-tap tip toepassen
- Soft-hold: handmatige scene pauzeert auto-scene 30 min
- Groepenfilter gebruikt alle extra scenes (niet alleen de basislijst)
- Tip-chip: voorgestelde scene in 1 tap
- Zoekbalk + favorieten (★ lang indrukken)
- Auto-scene: Buienradar/KNMI → regen; 9292/vertrektijden → spits
- Wifi-plaats + geofence + agenda + vliegtuigmodus
- RSSI → LDAC 330 bij zwak signaal (≤ −75 dBm)
- Wear-status: weekdosis + RSSI
- Favorieten-widget: 4 scenes in 1 tap (30 min soft-hold)

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Wear-complicatie: status-sync fine-tunen

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
