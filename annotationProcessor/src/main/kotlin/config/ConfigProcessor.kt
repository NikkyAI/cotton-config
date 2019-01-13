package config

import blue.endless.jankson.Comment
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import java.io.PrintStream

@SupportedAnnotationTypes(
    "config.ConfigurationAnnotation",
    "blue.endless.jankson.Comment",
    "io.github.cottonmc.cotton.config.annotations.ConfigFile",
    "io.github.cottonmc.cotton.config.annotations.ValidRangeFloat",
    "io.github.cottonmc.cotton.config.annotations.ValidRangeInt",
    "io.github.cottonmc.cotton.config.annotations.ValidValuesString"
)
//@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(ConfigProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
class ConfigProcessor : AbstractProcessor() {
    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // Creating a File object that represents the disk file.
        val o = PrintStream(File("annotationProcessor.log.txt"))

        // Store current System.out before assigning a new value
        val console = System.out

        // Assign o to output stream
        System.setOut(o)

//        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: kotlin.run {
//            println("ERROR: Can't find the target directory for generated Kotlin files.")
//            return false
//        }

        println("starting log ${System.currentTimeMillis()}")

        println("roundEnv: $roundEnv")
        annotations.forEach {
            println("processing $it")
        }
        roundEnv.rootElements.forEach {
            println("rootElement: $it")
            println("enclosed elements: ${it.enclosedElements}")
            println("enclosing elements: ${it.enclosingElement}")
        }
        roundEnv.getElementsAnnotatedWith(Comment::class.java).forEach {
            println("element: $it")
        }

        val resources = File("build").resolve("resources").apply {
            mkdirs()
        }
        if(!resources.exists()) {
            println("ERROR")
        }
        val testFile = resources.resolve("test.json").apply {
            createNewFile()
        }

        testFile.writeText("""
            {
                "version": 1
            }
        """.trimIndent())

        // Use stored value for output stream
        System.setOut(console)
        println("This will be written on the console!")
        return false
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}