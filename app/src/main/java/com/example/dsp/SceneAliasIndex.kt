package com.example.dsp

/** Extra zoek-aliassen uit recente batches. */
object SceneAliasIndex {
    val MAP: Map<String, Set<String>> =
        Batch120Aliases.MAP + Batch121Aliases.MAP + Batch122Aliases.MAP +
            Batch123Aliases.MAP + Batch124Aliases.MAP + Batch125Aliases.MAP +
            Batch126Aliases.MAP + Batch127Aliases.MAP + Batch128Aliases.MAP +
            Batch129Aliases.MAP + Batch130Aliases.MAP + Batch131Aliases.MAP +
            Batch132Aliases.MAP
}
