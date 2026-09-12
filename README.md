# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-12f — home + talk-through live)
- Talk-through samplet mic-RMS elke 1,2s als chip + mic-RMS aan staan.
- Home: scenes / LDAC / suggest / talk-through / favorieten zichtbaar; rest achter “Meer”.
- Wear: status stuurt fav_ids; next/prev cycle alleen pins als die er zijn.

## Nieuw (2026-09-12e — talk-through)
- Chip “Talk-through”: bij harde spraak 8s ConversationBoost.

## Nieuw (2026-09-12d — weer-scenes)
- Open-Meteo hint: regen → Pendelen regen, wind → Wind, ≥28°C → Tuin.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519
- Wear-complication UI-module (data-pad klaar)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
