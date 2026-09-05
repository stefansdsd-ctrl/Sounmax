# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Tip-chip: één tik past de voorgestelde scene toe
- ExtraListeningScenes nu écht gevuld: F1, Bruiloft, Uitvaart, NS Int, Thuisworkout, Podcast NL, Commentaar, Regen-auto, Museum-nacht, Supermarkt, A/B, Apotheek, IKEA, Bouw, Nachtbus, Stemboost, Kantoortuin, Horeca, Concertzaal, Voetbal thuis
- Auto-scene: AH/Jumbo	oversupermarkt; IKEA; F1TV; ESPN/DAZN	overcommentaar; Eurostar/Thalys/DB	overNS Int; NPO Luister
- Adaptive EQ-hints voor F1, commentaar, NS int, IKEA, apotheek, stemboost, kantoortuin, horeca
- Speaker-crossfeed, pendel-advisor, BLE 0x1843/0x1844

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
