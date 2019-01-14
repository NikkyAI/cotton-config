package config

import io.github.cottonmc.cotton.config.ConfigManager
import io.github.cottonmc.cotton.config.CottonConfig
import mu.KLogging

object Main : KLogging() {
    @JvmStatic
    fun main(vararg args: String) {
        val config = ConfigManager.loadConfig(CottonConfig::class.java)
        val config2 = ConfigManager.loadConfig(KottonKonfig::class.java)

        println(config2!!.nested.data)
    }
}