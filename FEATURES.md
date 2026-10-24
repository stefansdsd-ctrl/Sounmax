## Batch 330 (nieuw)
- WindNoiseDetector: echte korte AudioRecord RMS sample (200ms, permission check).
- BatteryHistory: ETA via lineaire regressie op recente samples, gecapt op 48u.

## Batch 329 (nieuw)
- WindNoiseDetector: stub voor mic RMS wind/noise detectie (Software ANC).
- BatteryHistory: verbeterde trend + ETA (alleen bij ontladen, min samples).
- Voorbereiding widget implementatie.

## Batch 328 (nieuw)
- BatteryHistory: trend detectie (CHARGING/DISCHARGING/STABLE). ETA alleen bij ontladen.

## Batch 327 (nieuw)
- Home screen App Widget: toont battery % + ETA uren. Quick tap naar app.
- Betere battery history: trend detectie (opladen vs ontladen).
- Voorbereiding wind-noise detectie via mic RMS (SoftwareAnc uitbreiding).

## Batch 326 (nieuw)
- Battery ETA: geschatte resterende uren op basis van recente ontladingssnelheid. Chip toont ~Xu.

## Batch 325 (nieuw)
- Battery chip: gemiddelde laatste 5 levels.

## Batch 324 (nieuw)
- BatteryHistory: track laatste 50 levels + average. Integreer met HeadphoneBattery.
- Album art in MediaNowPlaying chip (Coil).
- EQ-slot picker UI (0-4) + bevestig auto-apply.
- LowBatteryDuck: soft volume onder 15%.
- Voorbereiding Auto-ANC veldtest (GATT notify logging).

## Batch 323
- BatteryHistory basis.

## Batch 322
1. Album art in dashboard chip.
2. EQ-slot picker in UI (0-4) + auto-apply bij connect.
3. Low-battery duck/soft volume (onder 15%).

## Batch 321
- EqPersist multi-slot (0-4) via JSON.
- Auto-apply slot 0 (ConnectEqApplier).

## Batch 320
- EqPersist.kt: save/load custom curves.

## Batch 319
1. HeadphoneBattery: reflection + HF-indicator + notify.
2. MediaNowPlaying: title/artist/progress.
3. Auto-apply EqPersist bij connect.

## Hardware (open)
- Find-beep GATT-payload
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Widget implementatie
