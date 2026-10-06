# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-10-06 — batch 284)
- Start-cap: bij openen van de app, volume boven 70% → stapsgewijs naar 50%, één keer per processtart.
- Voorkomt een knal van een vergeten hoog volume. Hervatten knipt niet opnieuw.
- Banner: Start-cap actief (50%, vergeten luid volume).
- Zoeker: start, openen, opstarten, vergeten, luid.

## Nieuw (2026-10-06 — batch 283)
- SpikeGuard: muziek actief en volume springt meer dan 3 stappen → terug naar vorige + 1.
- Voorkomt een knal bij per ongeluk volume omhoog.
- Chip Sprong. Banner: Sprong gedempt (max +1 stap). Opnieuw bij start en hervatten.
- Zoeker: sprong, volume, knal, piek, dempen.

## Nieuw (2026-10-06 — batch 282)
- Route-cap: muziek actief, geen A2DP en geen kabel, boven 60% → 45%, stapsgewijs.
- Voorkomt een knal uit de telefoonspeaker als de headset wegvalt.
- Banner: Route-cap actief (45%, muziek op speaker). Opnieuw bij start en hervatten.
- Zoeker: route, speaker, a2dp, wegvalt, losgekoppeld.

## Volgende
- Auto-ANC veldtest
- Find-beep GATT-payload (hardware)
- Call-transparantie hardware-veldtest
