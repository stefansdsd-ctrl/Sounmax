package com.example.dsp

object SceneLookup {
    val ALL: List<ListeningScene> =
        (ListeningScenes.ALL + ExtraListeningScenes.ALL + NewNlScenes.ALL +
            Batch69Scenes.ALL + Batch70Scenes.ALL + Batch71Scenes.ALL + Batch72Scenes.ALL +
            Batch73Scenes.ALL + Batch74Scenes.ALL + Batch75Scenes.ALL + Batch76Scenes.ALL +
            Batch77Scenes.ALL + Batch78Scenes.ALL + Batch79Scenes.ALL + Batch80Scenes.ALL +
            Batch81Scenes.ALL + Batch82Scenes.ALL + Batch83Scenes.ALL + Batch84Scenes.ALL + Batch85Scenes.ALL + Batch86Scenes.ALL + Batch87Scenes.ALL + Batch88Scenes.ALL + Batch89Scenes.ALL + Batch90Scenes.ALL + Batch91Scenes.ALL + Batch92Scenes.ALL + Batch93Scenes.ALL + Batch94Scenes.ALL + Batch95Scenes.ALL + Batch96Scenes.ALL + Batch97Scenes.ALL + Batch98Scenes.ALL + Batch99Scenes.ALL + Batch100Scenes.ALL + Batch101Scenes.ALL + Batch102Scenes.ALL + Batch103Scenes.ALL + Batch104Scenes.ALL + Batch105Scenes.ALL + Batch106Scenes.ALL + Batch107Scenes.ALL + Batch108Scenes.ALL + Batch109Scenes.ALL + Batch110Scenes.ALL + Batch111Scenes.ALL + Batch112Scenes.ALL + Batch113Scenes.ALL + Batch114Scenes.ALL + Batch115Scenes.ALL + Batch116Scenes.ALL + Batch117Scenes.ALL + Batch118Scenes.ALL + Batch119Scenes.ALL + Batch120Scenes.ALL + Batch121Scenes.ALL + Batch122Scenes.ALL + Batch123Scenes.ALL + Batch124Scenes.ALL + Batch125Scenes.ALL + Batch126Scenes.ALL + Batch127Scenes.ALL + Batch128Scenes.ALL + Batch129Scenes.ALL + Batch130Scenes.ALL)
            .distinctBy { it.id }

    fun byId(id: String?): ListeningScene? =
        if (id.isNullOrBlank()) null else ALL.firstOrNull { it.id == id }
}
