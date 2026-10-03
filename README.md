# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-03 — batch 258)
- Slaap-cap: Calm/Headspace/Insight Timer/Sleep Cycle boven 58% → 36%, in stapjes van 2. Geen overlap met Boek. Chip Slaap.
- Eten-cap: Thuisbezorgd/Uber Eats/Deliveroo boven 62% → 42%. Geen overlap met Shop. Chip Eten.
- Boek- en Camera-cap hangen nu ook aan volumewijziging en FeatureFinder (waren alleen in WhyNow).
- Studie-cap deelt Audible/Kindle/Libby/Books niet meer met Boek-cap (geen dubbele stap).

## Nieuw (2026-10-03 — batch 257)
- Boek-cap: Audible/Storytel/Nextory/Kobo/Libby boven 66% → 50%, in stapjes van 2. Geen overlap met Podcast. Chip Boek.
- Camera-cap: Google/Samsung/Open Camera/Photos boven 55% → 32%. Sluiter en straat blijven hoorbaar. Geen overlap met Video. Chip Camera.

## Nieuw (2026-10-03 — batch 256)
- Shop-cap: Bol/Amazon/AH/Jumbo/Marktplaats boven 64% → 44%, in stapjes van 2. Geen overlap met Betaal-cap. Chip Shop.
- Nieuws-cap: NOS/NU/BBC/NYT boven 70% → 50%. Geen overlap met Podcast of Video. Chip Nieuws.
- Meet- en Mail-chips staan nu ook in FeatureFinder (waren al actief bij volumewijziging).

## Nieuw (2026-10-03 — batch 255)
- Meet-cap: Zoom/Meet/Teams/Webex/Slack boven 68% → 46%, in stapjes van 2. Voorgrond via UsageStats als toegestaan, anders playback/sessie. Chip Meet.
- Mail-cap: Gmail/Outlook/Spark/Proton/FairEmail boven 72% → 54%. Geen WhatsApp (Spraak) of Zoom (Meet). Chip Mail.
- Beide in WhyNow. Standaard aan.

## Nieuw (2026-10-03 — batch 254)
- Betaal-cap: ING/Rabobank/ABN/Bunq/Tikkie/PayPal/Revolut/Wallet boven 60% → 42%, in stapjes van 2. Voorgrond via UsageStats als toegestaan, anders playback/sessie. Chip Betaal.
- Kind-cap: YouTube Kids/Disney+/Nick/Zappelin/Duolingo boven 70% → 48%. Geen Netflix/YouTube (Video-cap). Chip Kind.
- Beide in WhyNow. Standaard aan.

## Nieuw (2026-10-03 — batch 253)
- Game-cap: Fortnite/Roblox/CoD/Genshin boven 80% → 66% (chip Game-cap). Playback-fallback.
- OV-cap: NS/9292/Citymapper/DB boven 70% → 52% (chip OV). Geen overlap met Nav-cap.
- Sport-cap staat nu ook in WhyNow.

## Nieuw (2026-10-03 — batch 252)
- Sport-cap: Strava/Nike/Fit/Peloton/Zwift boven 78% → 64% (chip Sport). Playback-fallback.
- Nav-cap en Offline-cap staan nu in FeatureFinder (waren al actief bij volumewijziging).
- Offline-cap gebruikt ConnectivityManager (internet + validated), geen aanname dat Wi-Fi online is.

## Nieuw (2026-10-03 — batch 251)
- Social-cap: TikTok/Instagram/Snap/Reddit/Facebook boven 74% → 56% (chip Social). Playback-fallback.
- Spraak-cap: WhatsApp/Telegram/Signal/Messenger boven 70% → 48% (chip Spraak).
- Video- en Studie-cap staan nu ook in FeatureFinder.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Call-transparantie hardware-veldtest
