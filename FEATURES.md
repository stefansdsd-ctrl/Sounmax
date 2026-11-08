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

## Batch 348 (nieuw)
- HarmanTargetEq: Pop/Chart preset (gebalanceerde mids, lichte punch, heldere highs).
- GeminiAudioTuner: offline fallback herkent pop/chart/dance/hit.

## Batch 347 (nieuw)
- HarmanTargetEq: Hip-Hop/Trap preset (zware sub, punchy mids).
- GeminiAudioTuner: offline fallback herkent hiphop/trap/rap/bass.
- ListeningModeSuggest: recommendPreset() voor directe Harman Soft/Neutral/Warm.

## Batch 346 (nieuw)
- HarmanTargetEq: Rock/Metal preset (punchy mids, controlled sub, air voor gitaren).
- GeminiAudioTuner: offline fallback herkent rock/metal/hardrock/guitar/riff + ANC maximaal.

## Batch 345 (nieuw)
- HarmanTargetEq: Classical (natuurlijke balans, lucht) en Electronic (krachtige sub, minder vermoeidheid) presets.
- GeminiAudioTuner: offline fallback herkent klassiek/orkest/strijk en edm/techno/house/trance.

## Batch 344 (nieuw)
- HarmanTargetEq: nieuwe Jazz/Acoustic preset (warm mids, natuurlijke dynamiek).
- GeminiAudioTuner: betere offline fallback met jazz/acoustic/piano keywords + ANC medium.

## Batch 343 (nieuw)
- GeminiAudioTuner: verbeterde lokale fallback met extra keywords (jazz, night/soft, fps, etc.) + slimmere ANC-aanbeveling.
- Strakkere system prompt, lagere temperature, snellere timeouts, gains ±10 dB.

## Batch 342 (nieuw)
- HarmanTargetEq: verfijnde curves + nieuwe Podcast en Gaming presets. Betere lokale startpunten.
- GeminiAudioTuner: slimme lokale fallback gebruikt nu HarmanTargetEq (warm/podcast/clarity/movie/gaming/neutral) op basis van prompt-keywords. Geen cloud nodig voor basis suggesties.

## Batch 341 (nieuw)
- HarmanTargetEq: verfijnde neutral/warm/vocal curves. Nieuwe Movie (meer virtualizer/spatial) en Clarity presets. Betere startpunten voor AI-tuner en handmatig.

## Batch 340 (nieuw)
- ListeningModeSuggest: echte checks voor battery (trend/percent), media (isMusicActive), wind, + nacht logica (22-6u Soft EQ). Betere context-adaptatie. Geen extra permissie.

## Batch 339 (nieuw)
- TunerNet.shouldAllowCloud(): centrale cloud-beslissing (portal/roaming/metered/slow/dataSaver/etc).
- qualityScore() 0-100 + bandwidthInfo() (down/up kbps).
- Label toont score + kbps. GeminiAudioTuner gebruikt de nieuwe check.

## Batch 338 (nieuw)
- ListeningModeSuggest: context-based suggestie ANC/Transparency/EQ (wind, battery, media). Chip-ready, geen extra permissie.

## Batch 337 (nieuw)
- HarmanTargetEq: neutrale / warme / vocal base curves (Harman-achtig) als betere startpunt voor AI-tuner of handmatig. Geschikt voor TAH6519 closed-back ANC.
