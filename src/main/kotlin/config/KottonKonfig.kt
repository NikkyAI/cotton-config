package config

import blue.endless.jankson.Comment
import io.github.cottonmc.cotton.config.annotations.ConfigFile
import io.github.cottonmc.cotton.config.annotations.RangeValidatorInt
import io.github.cottonmc.cotton.config.annotations.RegexValidator

@ConfigFile(name = "KottonConfig")
data class KottonKonfig(
    @RangeValidatorInt(min = 0)
    var number1: Short = 8,
    @RangeValidatorInt(min = 0)
    var number2: Int = 9,
    @RegexValidator(regex = "a+")
    var number3: Long = 10,
    var character: Char = 'A',
    var state: State = State.ONE,

    @Comment(value = "A list of mod ids, in order of preference for resource loading.")
    var namespacePreferenceOrder: MutableList<String> = mutableListOf(),
    var someStateList: MutableList<State> = mutableListOf(),

    var someIntSet: MutableSet<Int> = mutableSetOf(),
    var someStringSet: MutableSet<String> = mutableSetOf(),

    var someIntArray: Array<Int> = arrayOf(),

    var nested: NestedConfig = NestedConfig(),
    val nestedInner: NestedInner = NestedInner()
) {
    data class NestedInner(
        val innerData: String = "defaultInner"
    )
}

data class NestedConfig(
    val data: String = "defaultdata"
)

enum class State {
    ONE, TWO, THREE
}
