# Sounmax

Android companion-app voor Philips TAH6519 / Bluetooth-headsets.
10-bands EQ, luister-scenes, AI-tuner, gehoortest, YT Music en LDAC-hulp.

## Nieuw (2026-09-11b)
- **OffEarPause**: A2DP idle + geen media → pauze-key na 25s.
- **FindHeadset QS-tegel**: chirp vanuit snelle instellingen.
- **HearingReminder**: na 90 dagen of ~50 luisteruren naar gehoortest.
- **WeeklyListenBar** + reminder op home.

## Nieuw (2026-09-11)
- **READ_PHONE_STATE** in manifest voor CallModeGuard.
- **headset_address** persist in HeadsetStatusMonitor (BtDisconnectPause).
- **FindHeadset.ping** + L/R helper vanuit MainViewModel.
- **WeeklyListenBar** op home.
- Extra podcast-apps: Castbox, Podcast Addict, Podimo, Podbean, Google Podcasts.

## Nog open (hardware)
- Philips ANC-GATT bevestigen met dump op TAH6519
- Notify-leren testen op TAH6519

## Bouwen
Android Studio + JDK 17. API-sleutel Gemini: `.env.example`.
