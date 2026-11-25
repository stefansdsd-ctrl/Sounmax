## Batch 371 (nieuw — 2026-11-25)
- SoftwareAncController: Quietly-style software ANC via phone mic. Energy VAD + noise floor subtraction, lightweight DSP. Werkt met elke Bluetooth-headset. Start/stop via controller.

## Batch 370 (nieuw — 2026-11-24)
- AmbientEqAdapter: dynamische EQ-aanpassing op basis van ambient noise (mids boost bij noise, clarity in stilte). Integreert met AdaptiveMicAnc.

## Batch 369 (nieuw — 2026-11-24)
- HearingTest verbeterd: correcte L/R channel panning (pan=-1/1) + iets hogere boost (4dB) bij niet-gehoord. Betere stereo test.

## Batch 368 (nieuw — 2026-11-23)
- HearingTest: pure-tone test (31-16kHz) per ear. Genereert gepersonaliseerde 10-band EQ compensatie op basis van audibility. Simpele boost bij niet-gehoord.

## Batch 367 (nieuw — 2026-11-23)
- GeofenceReceiver: BroadcastReceiver voor enter/exit transitions. PendingIntent gekoppeld in GeofenceProfiles.
- Zones passen nu EQ-slot + ANC toe bij binnenkomst.

## Batch 366 (nieuw — 2026-11-22)
- GeofenceProfiles: location-based EQ/ANC switching (home/work zones via GeofencingClient). Auto-apply on enter/exit.

## Batch 365 (nieuw — 2026-11-22)
- BatteryHistory ETA: blend lineaire regressie met nominale capaciteit (ANC on ~40h / off ~80h voor TAH6519). Robuuster bij weinig data.

## Batch 364 (nieuw — 2026-11-20)
- OnDeviceEqSuggester: Gemini Nano via ML Kit Prompt API voor offline/privacy EQ-suggesties.
- GeminiAudioTuner: probeert eerst on-device Nano, dan cloud, dan lokale fallback.

## Batch 363 (nieuw — 2026-11-20)
- Verbeterde lokale AI-fallbacks in GeminiAudioTuner: meer genre-detectie (drill, edm, podcast, cinema, gaming) + preciezere Harman-presets.
- AutoEqStrength: soepelere log-interpolatie + veiliger preamp (0.75x) tegen clipping.
- SpatialAudio: batterij-aware auto-uit bij <15% + head-tracking status in WhyNow.
- Nieuwe functie: QuickSpatialToggle — één-tik spatial + tracking aan/uit met persistentie.

## Batch 362 (nieuw — 2026-11-16)
- GeminiVoiceController: natural language commands for ANC (hoog/laag/transparantie), EQ presets, Spatial on/off, scenes (Focus/Night/Commute). Fallback to GeminiAudioTuner.
- Klaar voor Gemini AppFunctions / voice overlay integratie.

## Batch 361 (nieuw — 2026-11-14)
- SpatialAudioController v2: OnSpatializerStateChangedListener + OnHeadTrackerAvailableListener, preference-persistent toggle (spatializer_on / head_tracking), battery-aware auto-disable via applyBatterySaver.
- SpatialToggleBar: Compose UI switches voor spatial audio en head tracking + status.
- Integratie met bestaande HeadTrackerHolder (phone rotation-vector).

## Batch 360 (nieuw)
- AutoEqImport: parse AutoEQ GraphicEQ / point lists → 10-band EqPreset.
- Strength 0–100%, safe preamp, EqPersist save, sample TAH6519 curve.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Software ANC UI toggle + integratie met scenes
- Homescreen widgets voor snelle ANC/EQ toggle (partial)
- Geofence receiver + UI to set zones
- HearingTest UI + save to EqPersist
