# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes: Treinwerk, Drukke NS, OV-chip, Swapfiets, Collegezaal, Gesprek, Marktplein, Basic-Fit
- Auto-scene: NS/9292/OV-chip → trein/ovchip; Swapfiets/Donkey → swapfiets; Basic-Fit/TrainMore → gym
- Extra scenes nu zichtbaar in groepenfilter (SceneGroups i.p.v. oude GROUPS)
- Adaptive EQ-hints voor treinwerk, drukke NS, gesprek, collegezaal, marktplein

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
