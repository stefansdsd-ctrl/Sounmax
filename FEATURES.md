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
