# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-02 — batch 244)
- Slot-cap: vergrendelscherm + muziek boven 72% → 54%. Echte KeyguardManager.isKeyguardLocked-check (chip Slot).
- Stil-cap: stille beltoon + muziek boven 64% → 46%. Echte RINGER_MODE_SILENT-check (chip Stil).

## Nieuw (2026-10-02 — batch 243)
- Scherm-uit-cap: scherm uit + muziek boven 76% → 58%. Echte PowerManager.isInteractive-check (chip Scherm).
- Hotspot-cap: hotspot aan + muziek boven 70% → 50%. Settings.Global wifi_ap_state + WifiManager-fallback (chip Hotspot).

## Nieuw (2026-10-02 — batch 242)
- Vliegtuig-cap: vliegtuigmodus + muziek boven 68% → 48%. Echte Settings.Global-check (chip Vliegtuig).
- Spaar-cap: batterijspaarstand + muziek boven 74% → 56%. Echte PowerManager-check (chip Spaar).

## Nieuw (2026-10-01 — batch 241)
- Nacht-cap: 23:00–06:00 + muziek boven 60% → 42%. Vult het gat na avond-cap; werkt ook zonder lader (chip Nacht).
- Niet-storen-cap: interruption filter niet ALL + muziek boven 65% → 45%. Echte NotificationManager-check (chip DND).

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Call-transparantie hardware-veldtest
