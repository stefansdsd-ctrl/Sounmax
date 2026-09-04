# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes F1/Bruiloft/Uitvaart/NS Int/Thuisworkout/Podcast NL/Commentaar/Regen-auto/Museum-nacht/Supermarkt/A-B écht in ExtraListeningScenes
- Extra: Apotheek, Bouw, IKEA, Nachtbus
- UI-groepen + filter gebruiken SceneGroups.LABELS (niet de oude GROUPS)
- Recap/A-B-scene wisselen naar vorige scene
- Auto-scene + Adaptive EQ voor F1, commentaar, internationale trein, IKEA, apotheek
- Groepen tonen nu alle extra scenes (SceneGroups-fix)
- Auto-scene: F1TV→Formule 1; Ziggo Sport/ESPN/DAZN→Commentaar; Eurostar/Thalys/DB→NS Internationaal; NPO Luister→Podcast NL

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
