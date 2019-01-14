package config


// TODO: move to loader code, translate to java

data class Field(
    val name: CharSequence,
    val type: String,
    val comment: String? = null,
    val validators: Map<Int, List<ConfigValidator>>
)