# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-12e — talk-through)
- Chip “Talk-through”: bij harde spraak (mic-RMS) 8s ConversationBoost, daarna terug.
- Mic-RMS moet aan staan voor detectie.

## Nieuw (2026-09-12d — weer-scenes)
- Open-Meteo hint: regen → Pendelen regen, wind → Wind, ≥28°C → Tuin.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519
- Wear-complication: alleen favorieten (module nog koppelen)

## Volgende software
- Home-balken inklapbaar (nu te vol)
- Wear-complication favorieten
- Mic-RMS → TalkThrough.onRms live in probe-loop

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
