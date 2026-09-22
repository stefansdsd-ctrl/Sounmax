package com.example.dsp

/** Extra zoek-aliassen uit recente batches. */
object SceneAliasIndex {
    val MAP: Map<String, Set<String>> =
        Batch120Aliases.MAP + Batch121Aliases.MAP + Batch122Aliases.MAP + Batch123Aliases.MAP + Batch124Aliases.MAP
}
