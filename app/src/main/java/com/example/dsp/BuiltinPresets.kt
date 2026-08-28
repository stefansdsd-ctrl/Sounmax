package com.example.dsp

object BuiltinPresets {
    val FREQUENCY_LABELS = listOf("31 Hz", "62 Hz", "125 Hz", "250 Hz", "500 Hz", "1 kHz", "2 kHz", "4 kHz", "8 kHz", "16 kHz")
    val FREQUENCY_CENTERS = listOf(31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000)

    val HEADPHONE_DEVICES = listOf(
        HeadphoneDevice(
            id = "philips_tah6519",
            name = "Philips TAH6519 ANC Over-Ear",
            brand = "Philips Sound",
            hasAnc = true,
            supportedCodecs = listOf(BluetoothCodec.AAC, BluetoothCodec.SBC, BluetoothCodec.LDAC),
            batteryPercent = 92,
            impedanceOhms = 32,
            soundProfileSummary = "Over-Ear Active Noise Canceling met 32mm Neodymium drivers. Geoptimaliseerd voor strakke sub-bass en brede soundstage.",
            defaultPresetName = "Philips TAH6519 Pro ANC"
        ),
        HeadphoneDevice(
            id = "philips_fidelio",
            name = "Philips Fidelio L3 / L4",
            brand = "Philips Fidelio",
            hasAnc = true,
            supportedCodecs = listOf(BluetoothCodec.LDAC, BluetoothCodec.AAC, BluetoothCodec.APTX_HD),
            batteryPercent = 85,
            impedanceOhms = 32,
            soundProfileSummary = "Premium Hi-Res Audiophile over-ear. Natuurlijk akoestisch timbre en sublieme micro-dynamiek.",
            defaultPresetName = "Philips Fidelio Studio Sound"
        ),
        HeadphoneDevice(
            id = "sony_wh1000xm5",
            name = "Sony WH-1000XM5 / XM4",
            brand = "Sony Audio",
            hasAnc = true,
            supportedCodecs = listOf(BluetoothCodec.LDAC, BluetoothCodec.AAC, BluetoothCodec.SBC),
            batteryPercent = 78,
            impedanceOhms = 48,
            soundProfileSummary = "Toonaangevende ANC met diepe basweergave en DSEE Extreme upscaling.",
            defaultPresetName = "Audiophile Harman Target"
        ),
        HeadphoneDevice(
            id = "bose_qc_ultra",
            name = "Bose QuietComfort Ultra",
            brand = "Bose",
            hasAnc = true,
            supportedCodecs = listOf(BluetoothCodec.APTX_ADAPTIVE, BluetoothCodec.AAC, BluetoothCodec.SBC),
            batteryPercent = 90,
            impedanceOhms = 32,
            soundProfileSummary = "Bose Immersive Audio met fluisterstille CustomTune ANC kalibratie.",
            defaultPresetName = "Live Concert & Spatial 3D"
        ),
        HeadphoneDevice(
            id = "sennheiser_momentum4",
            name = "Sennheiser Momentum 4 Wireless",
            brand = "Sennheiser",
            hasAnc = true,
            supportedCodecs = listOf(BluetoothCodec.APTX_ADAPTIVE, BluetoothCodec.AAC, BluetoothCodec.SBC),
            batteryPercent = 95,
            impedanceOhms = 60,
            soundProfileSummary = "Audiophile 42mm Transducer systeem met dynamische bas en kristalhelder hoog.",
            defaultPresetName = "Audiophile Harman Target"
        ),
        HeadphoneDevice(
            id = "apple_airpods_max",
            name = "Apple AirPods Max / Pro 2",
            brand = "Apple",
            hasAnc = true,
            supportedCodecs = listOf(BluetoothCodec.AAC, BluetoothCodec.SBC),
            batteryPercent = 88,
            impedanceOhms = 32,
            soundProfileSummary = "Adaptive EQ met computational audio en gepersonaliseerde ruimtelijke audio.",
            defaultPresetName = "Vocal & Acoustic Clarity"
        ),
        HeadphoneDevice(
            id = "generic_bluetooth",
            name = "Algemene Bluetooth Koptelefoon",
            brand = "Universal Bluetooth",
            hasAnc = false,
            supportedCodecs = listOf(BluetoothCodec.AAC, BluetoothCodec.SBC),
            batteryPercent = 100,
            impedanceOhms = 32,
            soundProfileSummary = "Universeel Bluetooth audio profiel met dynamische versterking en virtuele soundstage.",
            defaultPresetName = "YouTube Music Bass Monster"
        )
    )

    val PRESETS = listOf(
        // Bluetooth Koptelefoon Tunings
        EqPreset(
            id = 1,
            name = "Philips TAH6519 Pro ANC",
            bandGains = listOf(4.5f, 3.8f, 2.0f, -0.5f, 0.5f, 2.0f, 3.2f, 4.0f, 3.5f, 2.5f),
            bassBoost = 650,
            virtualizer = 420,
            loudness = 500,
            clarity = 7.5f,
            category = "Koptelefoons",
            description = "Gecalibreerd voor Philips TAH6519 32mm Neodymium drivers. Compenseert ANC sub-bass demping en opent de soundstage."
        ),
        EqPreset(
            id = 2,
            name = "Philips Fidelio Studio",
            bandGains = listOf(2.0f, 1.5f, 0.5f, 0.0f, 0.0f, 1.0f, 2.0f, 2.5f, 3.0f, 3.5f),
            bassBoost = 300,
            virtualizer = 550,
            loudness = 350,
            clarity = 8.5f,
            category = "Koptelefoons",
            description = "Strakke studio referentiecurve voor Philips Fidelio L3/L4 met natuurlijk akoestisch timbre en Hi-Res LDAC finesse."
        ),
        EqPreset(
            id = 3,
            name = "Sony WH-1000XM5 Clear Bass",
            bandGains = listOf(2.5f, 1.0f, -1.0f, -0.5f, 0.5f, 2.0f, 3.0f, 2.5f, 3.5f, 4.0f),
            bassBoost = 450,
            virtualizer = 400,
            loudness = 400,
            clarity = 8.0f,
            category = "Koptelefoons",
            description = "Tuint de Sony XM5 signature: vermindert modderig mid-laag en brengt kristalheldere vocalen en sprankelend hoog naar voren."
        ),
        EqPreset(
            id = 4,
            name = "Bose QC Ultra Spatial",
            bandGains = listOf(3.5f, 2.5f, 1.0f, 0.0f, 0.5f, 1.5f, 2.5f, 3.0f, 2.0f, 1.5f),
            bassBoost = 400,
            virtualizer = 750,
            loudness = 450,
            clarity = 7.5f,
            category = "Koptelefoons",
            description = "Brede ruimtelijke immersie geoptimaliseerd voor Bose QuietComfort CustomTune drivers."
        ),
        EqPreset(
            id = 5,
            name = "Sennheiser Momentum 4 Hi-Fi",
            bandGains = listOf(3.0f, 2.0f, 0.5f, 0.0f, 0.0f, 1.5f, 2.5f, 3.5f, 4.0f, 4.5f),
            bassBoost = 350,
            virtualizer = 450,
            loudness = 350,
            clarity = 8.5f,
            category = "Koptelefoons",
            description = "Audiophile 42mm transducer tuning met snelle basrespons en gedetailleerde micro-dynamiek."
        ),
        EqPreset(
            id = 6,
            name = "Apple AirPods Max Acoustic",
            bandGains = listOf(2.5f, 1.5f, 0.0f, 0.5f, 1.0f, 2.0f, 2.5f, 2.0f, 1.5f, 1.0f),
            bassBoost = 250,
            virtualizer = 500,
            loudness = 400,
            clarity = 8.0f,
            category = "Koptelefoons",
            description = "Gelijke tonale balans met zachte sub-bass en open vocalen voor Apple AirPods Max / Pro 2."
        ),
        EqPreset(
            id = 7,
            name = "Universele BT Bass Booster",
            bandGains = listOf(6.0f, 5.0f, 3.5f, 1.0f, 0.0f, 1.0f, 2.5f, 3.5f, 4.0f, 3.5f),
            bassBoost = 700,
            virtualizer = 400,
            loudness = 600,
            clarity = 6.5f,
            category = "Koptelefoons",
            description = "Universeel krachtig profiel dat compacte Bluetooth oortjes en koptelefoons een warme, diepe klank geeft."
        ),

        // Muziekgenres voor YouTube Music
        EqPreset(
            id = 8,
            name = "YouTube Music Bass Monster",
            bandGains = listOf(8.0f, 7.0f, 5.0f, 1.5f, -1.0f, 0.5f, 2.0f, 3.5f, 4.5f, 5.0f),
            bassBoost = 850,
            virtualizer = 300,
            loudness = 700,
            clarity = 6.0f,
            category = "Muziekgenres",
            description = "Diepe, voelbare sub-bass voor Trap, Hip-Hop, Drill, Phonk en Drum & Bass op YouTube Music."
        ),
        EqPreset(
            id = 9,
            name = "Electronic & Festival EDM",
            bandGains = listOf(6.5f, 5.5f, 3.0f, 0.0f, -0.5f, 1.5f, 3.5f, 5.0f, 5.5f, 4.5f),
            bassBoost = 750,
            virtualizer = 550,
            loudness = 600,
            clarity = 7.0f,
            category = "Muziekgenres",
            description = "Strakke kick-punch, pompende sidechain effecten en sprankelende synths voor festival club bangers."
        ),
        EqPreset(
            id = 10,
            name = "Hip-Hop & Urban R&B",
            bandGains = listOf(6.5f, 5.0f, 3.0f, 0.5f, 1.0f, 2.5f, 3.0f, 3.5f, 4.0f, 3.0f),
            bassBoost = 650,
            virtualizer = 350,
            loudness = 550,
            clarity = 7.5f,
            category = "Muziekgenres",
            description = "Vette 808 sub-bassen gecombineerd met gepolijste vocale articulatie en scherpe hi-hats."
        ),
        EqPreset(
            id = 11,
            name = "Rock & Metal Punch",
            bandGains = listOf(4.5f, 3.5f, 2.0f, 1.0f, 2.0f, 3.0f, 3.5f, 4.0f, 3.0f, 2.0f),
            bassBoost = 550,
            virtualizer = 300,
            loudness = 500,
            clarity = 7.5f,
            category = "Muziekgenres",
            description = "Rauwe gitaardefinitie, agressieve oversturing, strakke dubbele bass-drum en snijdende bekkens."
        ),
        EqPreset(
            id = 12,
            name = "Pop & Top 40 Radio Hits",
            bandGains = listOf(3.5f, 3.0f, 1.5f, 0.5f, 1.5f, 3.0f, 3.5f, 3.0f, 2.5f, 2.0f),
            bassBoost = 450,
            virtualizer = 400,
            loudness = 500,
            clarity = 8.0f,
            category = "Muziekgenres",
            description = "Radio-vriendelijke dynamiek met warme onderlaag en sprankelende zangstemmen die door de mix snijden."
        ),
        EqPreset(
            id = 13,
            name = "Audiophile Harman Target",
            bandGains = listOf(3.5f, 3.0f, 1.5f, 0.5f, 0.0f, 1.5f, 3.0f, 2.5f, 1.0f, 0.5f),
            bassBoost = 400,
            virtualizer = 350,
            loudness = 400,
            clarity = 7.0f,
            category = "Muziekgenres",
            description = "De befaamde akoestische Harman referentiecurve; wetenschappelijk afgestemd op natuurlijk gehoor."
        ),
        EqPreset(
            id = 14,
            name = "Vocal & Acoustic Warmth",
            bandGains = listOf(-1.0f, -0.5f, 0.5f, 1.0f, 2.5f, 4.5f, 4.0f, 3.0f, 2.0f, 1.5f),
            bassBoost = 150,
            virtualizer = 250,
            loudness = 450,
            clarity = 9.0f,
            category = "Muziekgenres",
            description = "Gedetailleerde zang en akoestische instrumenten (gitaar, piano); ook ideaal voor podcasts en audioboeken."
        ),
        EqPreset(
            id = 15,
            name = "Jazz, Blues & Vinyl",
            bandGains = listOf(2.5f, 2.0f, 1.5f, 1.0f, 1.5f, 2.0f, 2.0f, 1.5f, 1.0f, 0.5f),
            bassBoost = 300,
            virtualizer = 400,
            loudness = 300,
            clarity = 8.0f,
            category = "Muziekgenres",
            description = "Warme koperblazers, houtachtige contrabas en organische analoge diepte zonder digitale scherpte."
        ),
        EqPreset(
            id = 16,
            name = "Classical & Live Concert 3D",
            bandGains = listOf(4.0f, 3.0f, 1.0f, 0.5f, 1.0f, 2.0f, 3.0f, 4.5f, 5.5f, 6.0f),
            bassBoost = 500,
            virtualizer = 850,
            loudness = 600,
            clarity = 8.0f,
            category = "Muziekgenres",
            description = "Uitgestrekte concertzaal akoestiek voor orkesten, live YouTube Music optredens en symfonieën."
        ),
        EqPreset(
            id = 17,
            name = "Night Chill & Lo-Fi Relax",
            bandGains = listOf(3.0f, 2.5f, 1.5f, 1.0f, 0.5f, 0.0f, -1.0f, -2.0f, -3.0f, -4.0f),
            bassBoost = 350,
            virtualizer = 400,
            loudness = 200,
            clarity = 4.0f,
            category = "Muziekgenres",
            description = "Zachte, omhullende klankkleur zonder scherpe s-klanken voor ontspannen studeren of slapen."
        ),
        EqPreset(
            id = 18,
            name = "Flat Studio Monitor (0 dB)",
            bandGains = listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
            bassBoost = 0,
            virtualizer = 0,
            loudness = 0,
            clarity = 0f,
            category = "Muziekgenres",
            description = "Onbewerkte vlakke doorgifte voor kritische audioproductie en neutrale monitoring."
        )
    )
}
