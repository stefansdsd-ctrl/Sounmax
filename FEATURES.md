## Batch 375 (nieuw — 2026-11-27)
- AutoSoftwareAnc: automatisch Software ANC aan/uit op basis van noiseLevelDb van SoftwareAncController. Hysteresis (65/55 dB) om battery/mic te sparen. Start via AutoSoftwareAnc(context, controller).start().

## Batch 374 (nieuw — 2026-11-27)
- SoftwareAncBar verbeterd: runtime RECORD_AUDIO permission request via ActivityResultContracts. Toggle vraagt permissie indien nodig, status update.

## Batch 373 (nieuw — 2026-11-27)
- SoftwareAncController v2: robuustere adaptive noise floor, crest-factor speech detection, SNR metrics. Minder false triggers bij spraak.
- SoftwareAncBar: Compose UI met toggle, strength-slider, live noise/SNR/speech status. Klaar voor integratie in Home.

## Batch 372 (nieuw — 2026-11-25)
- SoftwareAncController: real-time mic-based noise reduction (Quietly-style). Adaptive noise floor, RMS/dB metrics, strength control. Requires RECORD_AUDIO. Feeds AmbientEqAdapter / volume ducking.

## Batch 371 (nieuw — 2026-11-25)
- NoiseDoseTracker: cumulative hearing dose (NIOSH/ISO-inspired, 85 dB/8h = 100%). Volume-based estimate + 80%/100% waarschuwingen. Resets daily.
- Integreer met volume-observer of MediaSession voor real-time updates.

## Batch 370 (nieuw — 2026-11-24)
- AmbientEqAdapter: dynamische EQ-aanpassing op basis van ambient noise (mids boost bij noise, clarity in stilte). Integreert met AdaptiveMicAnc.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Homescreen widgets voor snelle ANC/EQ toggle (partial)
- Geofence UI to set zones
- HearingTest UI + save to EqPersist
- Software ANC integratie in Home (bar klaar met permission)
