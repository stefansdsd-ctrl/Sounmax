# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Extra scenes in ALL + groepen: House, Techno, Dubstep, Ambient, Saver, Radio, TV, DJ-set, Liquid DnB, College, Pendelen regen
- Auto-scene: radio-apps (TuneIn, Radio.net, NPO Radio), liquid/house/techno/dubstep/dj-set
- Streamingvideo (Netflix/Disney/NPO/VRT) → TV-scene (dialoog voorop)
- Adaptive EQ blijft genre-specifiek

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
