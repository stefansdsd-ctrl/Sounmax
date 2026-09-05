# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Soft-hold: handmatige scene pauzeert auto-scene 30 min
- Groepenfilter gebruikt alle extra scenes (niet alleen de basislijst)
- Tip-chip: voorgestelde scene in 1 tap
- Scenes: Podcastwandeling, Avondwandeling, Open kantoor, Wachtrij, Stiltecoupé
- Scenes: Regen, Spits, Thuisavond, Nachttrein, Intercity, Koffietent, Huisarts, Ziekenhuis, Thuis+kids, Regenfiets
- Auto-scene: Buienradar/KNMI → regen; 9292/vertrektijden → spits
- Adaptive EQ-hints voor regen, spits, thuisavond en de nieuwe scenes
- Extra scenes zichtbaar in groepenfilter (Onderweg / Dag / Nacht)
- Wifi-plaats: pin huidige SSID als thuis/werk → wfh / office / thuisavond
- Vliegtuigmodus → flightsleep/quietcar
- Agenda: lopend event → meeting of videocall (Meet/Zoom/Teams)
- Geofence: pin thuis/werk slaat SSID + GPS op; bij onzichtbare SSID scene binnen 180 m
- Weekend-advisor: zaterdag markt/terras, zondag ochtend/avond
- RSSI → LDAC 330 bij zwak signaal (≤ −75 dBm)
- Nieuwe scenes: Weekendmarkt, Terrasavond, Zondagochtend, Bibliotheek stil, Wasstraat, Kerk
- Wear-status: weekdosis + RSSI
- Weer: onweer / sneeuw / hittegolf-scenes via Open-Meteo
- Schoolochtend-advisor: ma–vr 07:00–08:30 → alert + verkeer
- Avondrust-advisor: 22:00 thuisavond, 23:00–06:00 sleep
- Telefoonaccu ≤15% → saver + LDAC 330
- Activiteit: avondwandeling / avondfiets / nightdrive / homeworkout
- Scenes: Avondfiets, File, Sportschool, Avondrust

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Wear-complicatie: status-sync fine-tunen

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
