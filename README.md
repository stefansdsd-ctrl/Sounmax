# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- Software-ANC (max/adaptief/transparantie/wind) via DSP tot GATT-dump
- Adaptive EQ: hardstyle, drill, nederhop
- Auto-scene: K-pop, afrobeat, nederpop, hardstyle
- Scenes: Deep work, HIIT, Live sport
- Weekdosis (WHO-achtige pauzes bij 10 uur/week)
- Accu ≤20%: automatisch LDAC 330 kbps (verbinding sparen)
- Fletcher–Munson loudness-contour
- Echte crossfeed + safe limiter via DynamicsProcessing
- Spraak-boost op podcast/call/kantoor-scenes
- Scene één oor schakelt nu echte mono
- Pauze BT: muziek pauzeert bij disconnect, speelt door bij reconnect
- Adaptive EQ: K-pop, afrobeat/amapiano, nederpop
- Software-ANC gekoppeld aan setAncMode + bass-offset + persist
- Auto-transparantie bij beltoon/gesprek (AudioManager-mode)
- Adaptive EQ: gabber, phonk, country, gospel, voetbal-commentaar
- Pendelen: ANC max + LDAC 660 + veilige limiter (accu ≤20% blijft 330 kbps)
- Chip Call-transparantie (aan/uit) in luister-scenes
- Trein/bus/tram/metro: LDAC 660 voor stabiele verbinding
- Pendelen/trein/bus/tram/metro/vliegtuig/station/luchthaven: preferredLdAC 660 toegepast in SceneController
- Adaptive EQ: reggae, latin, anime
- Auto-scene: hardstyle/gabber, K-pop, afrobeat, nederpop, drill/phonk
- Wear-tile: play/pauze naast ANC + accu

## Volgende
- Philips ANC via echte GATT-UUIDs (koppel headset → Deel GATT-dump)

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
Wear-app via `wearApp(project(":wear"))`.
