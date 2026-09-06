# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- SceneController hersteld (bestand was leeg)
- Werk/weekend-pin per weekdag (ma-vr + za/zo), fallback op oud slot
- Schema-uren instelbaar: lang indrukken op Pin schema
- Tijdschema: weekend-slot en nacht-slot
- Pin schema-chip + Focus 25 + schema-label in waarom-regel
- Agenda: echte meetings, 5 min pre-roll
- Geofence PASSIVE, wifi-plaats, pendel, vliegtuigmodus
- Scenes: OV-overstap, Kapper, Museum, IKEA, Bouw/wegwerk
- Undo, tip-chip, zoek + favorieten, soft-hold
- RSSI naar LDAC 330 bij zwak signaal
- Wear-status + favorieten-widget

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset, deel GATT-dump)
- Wear-complicatie: status-sync fine-tunen
- ListeningScenesBar long-press uren koppelen als build klaar is

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
