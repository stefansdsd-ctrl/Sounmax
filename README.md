# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Groepen tonen nu alle extra scenes (SceneGroups-fix)
- Scenes Formule 1, Bruiloft, Uitvaart, NS Internationaal, Thuisworkout, Podcast NL, Commentaar, Regen-auto, Museum-nacht, Supermarkt, A/B vorige
- Auto-scene: F1TV→Formule 1; Ziggo Sport/ESPN/DAZN→Commentaar; Eurostar/Thalys/DB→NS Internationaal; NPO Luister→Podcast NL
- A/B-scene wisselt naar de vorige scene
- Scenes Zwembad, Schoolplein, Fotoshoot, Callcenter, Livestream, E-reader
- Groepen tonen extra scenes; QS-tegel Alert; scene-tegel fietst favorieten
- Auto-scene: camera→Fotoshoot, Kindle/Kobo→E-reader, zwem-apps→Zwembad, Kick→Livestream, Zendesk→Callcenter
- Scenes Camping, Tentamen, Karaoke, Baby, Alert
- Adaptive EQ: festival, veerboot, klus, cowork, tandarts, sauna, stadion, audiotour, camping, tentamen, karaoke, baby
- Auto-scene: Crunchyroll → Anime; Smule → Karaoke; Forest/Brain.fm → Tentamen; babyfoon → Baby; bijbel-apps → Kerk
- Shorts-apps mappen naar Shorts i.p.v. Party
- Weer/vlucht/accu wint nog steeds van media bij tandarts/ziekenhuis/baby
- Scenes Stiltecoupé, Park, Videobellen, Esports, Filmavond, Ski, NPO Radio
- Auto-scene: NPO/TuneIn/Shazam→Radio; Viaplay/Ziggo/Apple TV→TV; Genshin/Clash→Esports; Meet/Duo→Videobellen; Buienradar→Regen; Ski-apps→Ski

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
