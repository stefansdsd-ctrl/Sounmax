# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- SceneController terug (was leeg op main — app compileerde niet)
- Scene-zoekbalk in de hoofd-UI
- Slaaptimer plant nu ook SleepFade-alarm (widget/service)
- Recente scenes, weekdosis, auto/weer/adaptive EQ

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Fine-tune DynamicsProcessing per scene

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
