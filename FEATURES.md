## Batch 357 (nieuw)
- AdaptiveMicAnc: zero-crossing rate (ZCR) voor spraakdetectie → auto-transparantie bij praten.
- Lagere sample rate (8 kHz) + langere delay voor batterijbesparing.
- Aangescherpte thresholds en noise-floor tracking.

## Batch 356 (nieuw)
- AdaptiveMicAnc: verbeterde ambient noise monitoring met noise-floor tracking, hysteresis en real-time ANC niveau (0-3).

## Batch 355 (nieuw)
- Harman Focus preset: minder bass-masking + heldere mids voor concentratie/werk.
- Harman Night preset: zacht/warm met gereduceerde highs voor avond/slaap.
- Harman Commute preset: gebalanceerd met presence voor rumoerige omgevingen.
- Extra presets direct beschikbaar via allPresets() en AI-tuner.

## Batch 354 (nieuw)
- ConversationDetector: spraakdetectie via mic RMS + zero-crossing → auto-transparantie bij praten.

## Batch 353 (nieuw)
- HearingHealth: dagelijkse dB-exposure tracking + volume-limiet (WHO/ISO-geïnspireerd).
- Spatial audio prep: Android Spatializer / virtual surround toggle.
- Low-latency mode: game/video prioriteit + sidetone voor calls.
- Wear detection: auto-pause/resume via BLE sensor (indien beschikbaar).
- Quick Settings tile: snelle ANC/EQ/volume controls.

## Batch 352 (nieuw)
- AutoEqStrength: log-interpolatie naar 10 standaard EQ-bands voor soepelere toepassing.
- Night loudness: betere equal-loudness contour (ISO 226-geïnspireerd) met volume-adaptieve bass/treble boost.
- Veiligere preamp (0.8x) om clipping te voorkomen.

## Batch 351 (nieuw)
- AutoEqStrength: schaalbare AutoEQ correctie (0-100%), graph data (measured/target/corrected), safe preamp.
- Live spectrum analyzer prep: FFT visualizer voor realtime feedback bij EQ tweaks.
- Night loudness: equal-loudness contour (ISO 226) voor zachte volumes, bass/treble boost.
- Bass tuner: controlled sub boost + mild compression.
- Find-beep verbeterd: GATT payload test + volume ramp.

## Batch 349 (nieuw)
- Adaptive mic-ANC: ambient noise detectie via microfoon voor real-time ANC niveau aanpassing (geen extra permissie beyond RECORD_AUDIO).
- AutoEQ import: support voor AutoEQ profiles (autoeq.app) voor TAH6519 en andere modellen. Auto-apply op connect.
- On-device ML EQ: Gemini Nano / ML Kit suggesties zonder cloud voor privacy en offline.
- Battery DSP: dynamische EQ complexiteit reduceren bij lage batterij (minder bands/virtualizer).
- Multi-device profiles: automatische switch tussen meerdere headphones met per-device EQ/ANC.
