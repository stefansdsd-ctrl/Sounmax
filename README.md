# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- House / Techno / Dubstep / Ambient / Saver zitten in de scene-lijst
- Extra scenes: Radio, TV, DJ-set, Liquid DnB
- Widget bladert door favorieten als die er zijn
- Auto veilig-volume na 180 min luisteren vandaag
- Juistere LE Audio GATT-labels (volume, MCS, ASCS)

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
