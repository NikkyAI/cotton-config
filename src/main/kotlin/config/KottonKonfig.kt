package config

import blue.endless.jankson.Comment
import io.github.cottonmc.cotton.config.annotations.ConfigFile

@ConfigFile(name = "KottonKonfig")
data class KottonKonfig(
    var number1: Int = 8,
    @Comment(value = "A list of mod ids, in order of preference for resource loading.")
    var namespacePreferenceOrder: MutableList<String> = mutableListOf()
)
