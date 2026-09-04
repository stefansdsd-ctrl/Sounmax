# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Scenes Shorts, Tuin, Wachten, Nacht-auto, Regenwandeling
- Auto-scene: TikTok/Insta/Snap → Shorts; Steam/Epic/Xbox/Roblox/Twitch → Game; AH/Jumbo/Lidl/Picnic → Winkel; Thuisbezorgd/Deliveroo → Koken; Flitsmeister/ANWB → Auto; Pokémon GO → Wandelen; Docs/Sheets/Figma → Kantoor/Code
- Adaptive EQ: shorts, koken, kids, bieb, buiten, regen, nacht-auto
- Circadian EQ: ochtend warmer, avond/nacht zachter hoog
- Groepen UI: yoga/nav/taal/cardio/code + wind/vlucht-slaap/interview/piano in Onderweg/Werk/Sport/Media
- Tip-chip: tik de voorgestelde scene om direct te activeren
- Auto-scene: X/Reddit → Nieuws; Agenda/Gmail/Outlook/LinkedIn → Kantoor; ChatGPT/Claude → Code; Headspace/Calm → Meditatie
- Adaptive EQ: navigatie, wind, vlucht
- Focus 25: QS-tegel Deep work + scene-lock; na 25 min automatische oorpauze
- Sleep-timer 120 min
- Widget/volume-cyclus gebruikt favorieten als er ≥2 zijn
- Dubbel volume-omhoog = volgende scene, omlaag = vorige
- Wear-complicatie tik wisselt naar de volgende scene
- Auto veilig-volume vanaf 180 min vandaag
- Auto-Saver bij headset-accu ≤15%

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)
- Scene-groepen in UI: Shorts/Tuin/Wachten in Media/Onderweg/Dag

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
