## Nieuw (2026-11-12 — batch 359)
- EqSlotPicker: 5 named EQ slots (save/load/list) via SharedPreferences voor snelle profile switch.

## Nieuw (2026-11-12 — batch 358)
- AdaptiveMicAnc v2: crest factor + speech streak voor stabielere spraakdetectie, snellere response.

## Nieuw (2026-11-11 — batch 356)
- AdaptiveMicAnc: noise-floor tracking + hysteresis voor stabielere adaptive ANC.

## Nieuw (2026-11-08 — batch 352)
- AutoEqStrength: log-interpolatie naar 10-bands + veiligere preamp.
- Night loudness verbeterd (ISO 226-geïnspireerd).

## Nieuw (2026-11-06 — batch 350)
- Multipoint manager: detect + switch active devices, prefer phone for calls.
- Codec auto-prefer: probe LDAC/aptX and lock preferred on connect.
- Firmware check: compare version via BLE and notify of updates.
- App-specific profiles: auto-apply EQ/ANC based on foreground app.

## Nieuw (2026-11-05 — batch 349)
- Suggesties: Adaptive mic-ANC, AutoEQ import, on-device ML EQ, battery DSP optimizations, multi-device profiles.

# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- EQ-slot picker UI (basis klaar)
- On-device AI EQ suggesties (Gemini Nano indien beschikbaar)
- Adaptive mic-ANC (v2 verbeterd)
- AutoEQ import support
