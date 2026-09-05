# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes: Bioscoop, Terras, Pretpark, Hackathon, Winkelcentrum, Douane, GVB/RET, Nachtmarkt, Lo-fi
- Auto-scene: Pathé/Kinepolis→bioscoop; Efteling/Walibi→pretpark; GVB/RET/HTM→gvb; Schiphol→douane
- Adaptive EQ-hints voor bioscoop, terras, pretpark, hackathon, mall, GVB, lo-fi
- Tip-chip, ExtraListeningScenes, speaker-crossfeed, pendel-advisor, BLE 0x1843/0x1844

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
