# Sounmax — volgende features

## Batch 280 (klaar — code)
1. WifiVolumeMemory: per SSID muziekvolume onthouden, alleen terugzetten op A2DP-headset terwijl muziek speelt, stappen van 2.
2. Leert niet stilletjes: cycle/save slaat het huidige volume op. Onbekend SSID of geen locatie-recht → geen actie.
3. FeatureFinder-chip Wi-Fi. Zoek op wifi, ssid, thuis, netwerk, volume.
4. ActiveLimitBanner toont het netwerk als de herinnering actief is. Opnieuw bij resume.

## Batch 279 (klaar — code)
1. SilentRingerCap: ringerMode != NORMAL (tril/stil) + muziek actief, boven 68% → 52%, stapsgewijs.
2. Geen overlap met FocusQuietCap (interruption filter) of NightQuietCap (klok).
3. FeatureFinder-chip Stil. Zoek op tril, stil, silent, ringer, vibrate, beltoon.
4. ActiveLimitBanner toont de cap. Opnieuw bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
