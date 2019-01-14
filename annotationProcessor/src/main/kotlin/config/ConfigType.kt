package config

// TODO: move to loader code, translate to java

sealed class ConfigType {
    data class Primitive(val primitive: String)

    data class Class(
        val name: String,
        // TODO: use more complex object for field
        val fields: Map<String, Field>
    )
}