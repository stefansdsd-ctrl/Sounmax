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
- Metered-waarschuwing vóór Gemini op mobiel
