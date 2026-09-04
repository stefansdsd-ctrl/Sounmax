# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Speaker-crossfeed op het EQ-scherm (minder harde stereo-split)
- Pendel-advisor: werkdagen 06:20–09:30 en 16:20–19:30 → Pendelen / Pendelen regen
- BLE: Audio Input Control (0x1843) en Volume Control (0x1844) correct
- Scenes nu écht in ExtraListeningScenes (stonden alleen in groepen): F1, Bruiloft, Uitvaart, NS Int, Thuisworkout, Podcast NL, Commentaar, Regen-auto, Museum-nacht, Supermarkt, A/B, Apotheek, IKEA, Bouw, Nachtbus
- Extra: Stemboost, Kantoortuin, Horeca, Concertzaal, Voetbal thuis
- Auto-scene: AH/Jumbo→Supermarkt; IKEA; F1TV; Ziggo/ESPN/DAZN→Commentaar; Eurostar/Thalys/DB→NS Int; NPO Luister
- Widget + QS-tegel + now-playing gebruiken SceneLookup (alle extra scenes)
- Adaptive EQ-hints voor F1, commentaar, NS int, IKEA, apotheek, stemboost, kantoortuin, horeca

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
