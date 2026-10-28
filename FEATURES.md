## Batch 332 (nieuw)
- NoiseBasedAnc: suggestie volle/zachte ANC of transparantie op basis van WindNoiseDetector RMS + batterij/trend.
- Betere integratie software ANC hints.

## Batch 331 (nieuw)
- WindNoiseDetector: 3x short samples averaged for stability, threshold 0.10.
- BatteryHistory: more points (20), stricter slope filter (-0.05) for reliable ETA.

## Batch 330 (nieuw)
- WindNoiseDetector: echte korte AudioRecord RMS sample (200ms, permission check).
- BatteryHistory: ETA via lineaire regressie op recente samples, gecapt op 48u.
