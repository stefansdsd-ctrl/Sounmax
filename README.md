## Nieuw (2026-10-09 — batch 317)
- Roaming (mobiel, NET_CAPABILITY_NOT_ROAMING ontbreekt): Gemini blijft uit. Chip toont "· roaming". Cache of lokale curve.
- Knop "Toch Gemini (roaming)" forceert cloud. Wi-Fi telt niet als roaming.
- VPN blokkeert Gemini niet. Chip toont "· vpn". Geen extra permissie.

## Nieuw (2026-10-09 — batch 316)
- Captive portal (hotel, trein): Gemini blijft uit. Chip toont "· portal". Cache of lokale curve.
- Traag net (<150 kbps downstream): eerst cache. Knop "Toch Gemini (traag net)" forceert cloud.
- 0 kbps telt als onbekend, niet als traag. Geen extra permissie.

## Nieuw (2026-10-09 — batch 315)
- AI-tuner krijgt nu echt de app-context en de metered-bevestiging (batch 314 stond alleen in de tuner, niet in de knop).
- Offline of mobiel data: eerst de beste gecachte Gemini-curve, anders lokale curve.
- Wi-Fi zonder datalimiet blijft Gemini. Extra knop: "Toch Gemini (gebruikt data)".
- Net-chip toont "· data" bij metered Wi-Fi of mobiel. Geen extra permissie.

## Nieuw (2026-10-09 — batch 314)
- Metered-waarschuwing vóór Gemini: op mobiel of metered hotspot blijft de cloud uit tot je bevestigt.
- Hoofdknop wordt "Lokale curve (mobiel data)". Extra knop: "Toch Gemini (gebruikt data)".
- Tuner krijgt nu echt de app-context (offline-check werkte anders niet). Geen extra permissie.

## Nieuw (2026-10-09 — batch 313)
- AI-balk toont echte netstatus: Wi-Fi, Mobiel, Ethernet of Offline.
- Status volgt de actieve verbinding (validated internet, niet alleen radio aan).
- Offline: knop wordt "Lokale curve". Geen extra permissie.

## Nieuw (2026-10-08 — batch 312)
- AI-tuner checkt gevalideerd internet (niet alleen radio aan) vóór Gemini.
- Bij 404/429/5xx: één retry, daarna model-fallback (2.5-flash, 2.0-flash).
- Geen netwerk of lege key: lokale curve, met reden in de insight.
- Geen extra permissie. INTERNET en ACCESS_NETWORK_STATE stonden er al.

# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-08 — batch 310)
- Wacht-cap: volume +2 stappen terwijl muziek stil is, play binnen 25s boven 52% → 5s naar 50%.
- Vangt slotscherm-knal. SpikeGuard ziet alleen sprongen tijdens afspelen. Ochtend-cap blijft één keer per dag.
- Zit op DuckLane. Laagste cap wint (onder zoek 56).
- Chip Wacht in de omroep-balk. Banner: Wacht-cap actief (50%, volume vóór play).
- Zoeker: wacht, slotscherm, knal, pauze.
- Geen extra permissie.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
