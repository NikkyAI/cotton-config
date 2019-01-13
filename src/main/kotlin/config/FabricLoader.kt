package config

import java.io.File

object FabricLoader {
    val configDirectory = File("run", "config").apply {
        mkdirs()
    }
}