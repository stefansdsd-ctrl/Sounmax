# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw
- **nRF-stijl GATT-dump** (`NrfStyleGattDump`): services/chars/values + share; mode-tags voor ANC-knop
- **Eén-tik profielen** (`OneTapProfiles`): Woon-werk, Focus, Slaap, Buiten, Gesprek, Uit
- **Echte ANC-modes** (`RealAncController`): hardware ANC/Normaal/Transparantie + soft-EQ
- **Wifi-RSSI-kaart**, wind-detectie, stereo-breedte, Drive-map, volume-cap, mic-RMS, genre-EQ

## nRF-dump (TAH6519)
1. Headset verbinden (GATT via `HeadsetStatusMonitor`)
2. `NrfStyleGattDump.share(context, "baseline")` of `monitor.exportNrfDump("baseline")`
3. Druk fysieke **ANC-modus-knop**
4. Opnieuw dump met label `"anc"` / `"off"` / `"awareness"`
5. Vergelijk `Value:`-regels of deel de .txt

Bestanden: `filesDir/nrf_gatt_dump.txt` + `nrf_gatt_history.txt`

## ANC
| App-modus | Hardware | Soft-EQ |
|-----------|----------|---------|
| OFF | Normaal | passief |
| STRONG / ADAPTIVE / WIND_GUARD | ANC | ja |
| AMBIENT | Transparantie | spraak-boost |

`SoftwareAnc.applyWithHardware(context, mode)`

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
