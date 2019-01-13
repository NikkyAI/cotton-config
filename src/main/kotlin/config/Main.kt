package config

import io.github.cottonmc.cotton.config.ConfigManager
import io.github.cottonmc.cotton.config.CottonConfig
import io.github.cottonmc.cotton.logging.ModLogger
import mu.KLogging

object Main : KLogging() {
//    @JvmStatic
//    var logger = ModLogger("config-test", "COTTON")
    @JvmStatic
    fun main(vararg args: String) {

        val config = ConfigManager.loadConfig(CottonConfig::class.java)
        val config2 = ConfigManager.loadConfig(KottonKonfig::class.java)


    }
}