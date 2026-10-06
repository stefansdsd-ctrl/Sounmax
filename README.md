# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-06 — batch 282)
- Route-cap: muziek actief, geen A2DP en geen kabel, boven 60% → 45%, stapsgewijs.
- Voorkomt een knal uit de telefoonspeaker als de headset wegvalt.
- Banner: Route-cap actief (45%, muziek op speaker). Opnieuw bij start en hervatten.
- Zoeker: route, speaker, a2dp, wegvalt, losgekoppeld.

## Nieuw (2026-10-06 — batch 281)
- OfflineGuard: geen gevalideerd internet → AI-tuner slaat Gemini over en gebruikt meteen de lokale curve.
- Geen 30s timeout in vliegtuigmodus of zonder data.
- Chip Net. Banner: Offline: AI lokaal, cloud uit.
- Zoeker: offline, net, internet, cloud, gemini, vliegtuig.

## Nieuw (2026-10-06 — batch 280)
- Accu-cap: telefoonaccu ≤ 20% en muziek actief, boven 65% → 50%, stapsgewijs. Chip Accu.
- Werkt los van Spaar-cap (die kijkt naar spaarstand, dit naar batterijpercentage).
- Banner toont de cap. Opnieuw bij hervatten van de app.
- Zoeker: accu, batterij, laag, leeg, opladen.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Call-transparantie hardware-veldtest
