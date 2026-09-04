# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Groepen UI: yoga/nav/taal/cardio/code + wind/vlucht-slaap/interview/piano in Onderweg/Werk/Sport/Media
- Tip-chip: tik de voorgestelde scene om direct te activeren
- Auto-scene: X/Reddit → Nieuws; Agenda/Gmail/Outlook/LinkedIn → Kantoor; ChatGPT/Claude → Code; Headspace/Calm → Meditatie
- Adaptive EQ: navigatie, wind, vlucht
- Groepen: yoga/nav/taal/cardio/code + extra onderweg/media-scenes
- Auto-scene: WhatsApp/Telegram/Signal → Bel; Notion/Obsidian → Code; Uber/Bolt → Auto; KLM/Booking → Vliegtuig
- Adaptive EQ: yoga, taal, livesport, café, code/lofi
- Focus 25: QS-tegel Deep work + scene-lock; na 25 min automatische oorpauze
- Auto-scene: laatst gebruikte scene per app eerst
- Extra app-map: Teams/Zoom/Meet/Slack → Vergadering; Discord → Voicechat; NS/9292 → Pendelen; NOS/NU → Nieuws; YouTube/Plex/Videoland → TV
- Scenes Yoga, Navigatie, Taal (Duolingo/Babbel)
- Auto-scene: Strava/Nike/Garmin-run → Cardio; Peloton/Zwift → Gym; Maps/Waze → Nav; Coursera/Udemy → College
- Scene per app onthouden
- Sleep-timer 120 min
- STILL op werkdag 9–17 → Kantoor; hardlopen → Cardio; spitstijd in auto → Pendelen
- SceneLookup toont extra-scenes in Alles + groepen
- Adaptive EQ: yoga / navigatie / taal
- Scenes Code + Cardio; Recap/Wind/Piano/Akoestisch nu ook in ALL + groepen
- Auto-scene: Bandcamp, Castbox, Podcast Addict, Apple Podcasts
- Adaptive EQ: akoestisch + cardio
- Oorpauze: melding + sessie reset na 50 min (gelijkgetrokken)
- Auto-scene volgt Now Playing (podcast/film/game/genre), weer/vlucht/accu blijven leidend
- Extra podcast-apps: Podcast Addict, Castbox
- Scenes: Wind, Fiets-regen, Interview, Mix-check, Vlucht-slaap, Piano, Akoestisch, Recap
- Weer: spitstijd + regen → Pendelen regen; buiten + wind → Wind
- Recente scenes worden onthouden
- QS-tegel Oorpauze (scene Rust)
- Adaptive EQ voor piano/akoestisch
- Widget/volume-cyclus gebruikt favorieten als er ≥2 zijn
- Dubbel volume-omhoog = volgende scene, omlaag = vorige
- Auto-oorpauze na 50 min ononderbroken (widget-tick)
- Head-track kalibratie: kijk recht vooruit → nulpunt voor yaw/pitch
- Wear-complicatie tik wisselt naar de volgende scene
- Auto veilig-volume vanaf 180 min vandaag
- Auto-Saver bij headset-accu ≤15%

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
