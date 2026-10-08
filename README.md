# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-08 — batch 304)
- Toegang-duck: TalkBack of toegankelijkheidsklik terwijl muziek loopt → stapsgewijs naar 33%.
- Alleen USAGE_ASSISTANCE_ACCESSIBILITY. TTS, bel en alarm blijven eigen usages.
- Zit op DuckLane. Laagste cap wint (onder TTS 34, boven bel 32).
- Chip Toegang. Banner: Toegang-duck actief (33%, TalkBack hoorbaar).
- Zoeker: toegang, talkback, toegankelijkheid, screenreader.
- Alleen Android 8+. Geen extra permissie.

## Nieuw (2026-10-08 — batch 303)
- Spraak-duck: audioboek, podcast of voice-note terwijl muziek loopt → stapsgewijs naar 43%.
- Alleen CONTENT_TYPE_SPEECH op media/unknown/game. Chat en ping blijven eigen usages.
- Zit op DuckLane. Laagste cap wint (onder nav 42, boven chat 44).
- Chip Spraak. Banner: Spraak-duck actief (43%, audioboek hoorbaar).
- Zoeker: spraak, audioboek, podcast, voice-note, voorlezen.
- Alleen Android 8+. Geen extra permissie.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Toegang-duck veldtest: TalkBack-klik terwijl muziek loopt
