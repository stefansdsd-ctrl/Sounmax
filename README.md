# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Genre-groep + scenes: Hardstyle, Gabber, Phonk, K-pop, Afrobeat, Nederpop, Reggae, Latin, Anime, Country, Gospel, Boom bap, Metalcore, Drill, Trance, UK garage, Natuur
- Auto-scene herkent die genres nu éérst uit titel/genre
- Adaptive EQ: UK garage + trance
- Recente scenes + groep Genre
- preferredLdac + één-oor-mono bij scene-wissel
- Call-transparantie + dosiswaarschuwing
- Software-ANC tot GATT-dump

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
