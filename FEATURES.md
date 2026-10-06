# Sounmax — volgende features

## Batch 281 (klaar — code)
1. OfflineGuard: ConnectivityManager + NET_CAPABILITY_VALIDATED. Geen aanname dat internet er is.
2. AI-tuner slaat cloud over als de guard aan staat en het netwerk niet gevalideerd is.
3. FeatureFinder-chip Net. Zoek op offline, net, internet, cloud, gemini, vliegtuig.
4. Banner toont offline-status. Refresh bij resume.

## Batch 280 (klaar — code)
1. WifiVolumeMemory: per SSID muziekvolume onthouden, alleen terugzetten op A2DP-headset terwijl muziek speelt, stappen van 2.
2. Leert niet stilletjes: cycle/save slaat het huidige volume op. Onbekend SSID of geen locatie-recht → geen actie.
3. FeatureFinder-chip Wi-Fi. Zoek op wifi, ssid, thuis, netwerk, volume.
4. ActiveLimitBanner toont het netwerk als de herinnering actief is. Opnieuw bij resume.

## Hardware (blijft open)
- Find-beep payload na TAH6519 GATT-dump
- ANC-GATT + notify-leren
- Auto-ANC veldtest
- Codec-probe veldtest op echte TAH6519
