# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Widget/volume-cyclus gebruikt favorieten als er ≥2 zijn
- Dubbel volume-omhoog = volgende scene, omlaag = vorige
- Auto-oorpauze na 50 min ononderbroken (widget-tick)
- Scenes: Interview, Mix-check, Vlucht-slaap, Piano, Akoestisch
- Head-track kalibratie: kijk recht vooruit → nulpunt voor yaw/pitch
- Wear-complicatie tik wisselt naar de volgende scene
- Scenes Ochtend, Bass-check, Stereo-test, Referentie, Speaker, Studio, Nachtdienst, Markt
- Groepen Dag + Tools
- Werkdag 06–08 suggereert Ochtend; 00–05 Nachtdienst
- Auto veilig-volume vanaf 180 min vandaag
- Snelle chips: A/B, Oorpauze, Deel + sleep 90m
- Auto-Saver bij headset-accu ≤15%
- Weekdosis-label (rust na 600 min)
- Auto-scene: fitness-apps → Gym, navigatie → Pendelen

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
