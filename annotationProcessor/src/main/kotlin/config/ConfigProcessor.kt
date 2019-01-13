package config

import blue.endless.jankson.Comment
import java.io.IOException
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.FilerException
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.FileObject
import javax.tools.StandardLocation

//@SupportedAnnotationTypes(
//    "config.ConfigurationAnnotation",
//    "blue.endless.jankson.Comment",
//    "io.github.cottonmc.cotton.config.annotations.ConfigFile",
//    "io.github.cottonmc.cotton.config.annotations.ValidRangeFloat",
//    "io.github.cottonmc.cotton.config.annotations.ValidRangeInt",
//    "io.github.cottonmc.cotton.config.annotations.ValidValuesString"
//)
//@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(
    ConfigProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME,
    "debug",
    "verify"
)
class ConfigProcessor : AbstractProcessor() {
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            "config.ConfigurationAnnotation",
            "blue.endless.jankson.Comment",
            "io.github.cottonmc.cotton.config.annotations.ConfigFile",
            "io.github.cottonmc.cotton.config.annotations.ValidRangeFloat",
            "io.github.cottonmc.cotton.config.annotations.ValidRangeInt",
            "io.github.cottonmc.cotton.config.annotations.ValidValuesString"
        )
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        return try {
            processImpl(annotations, roundEnv)
        } catch (e: Exception) {
            // We don't allow exceptions of any kind to propagate to the compiler
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            log(writer.toString())
            true
        }
    }

    fun processImpl(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        openLog()
//        val generatedSourcesRoot: String = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME] ?: kotlin.run {
//            log("ERROR: Can't find the target directory for generated Kotlin files.")
//            "build/generated/resources"
//        }

        log("starting log ${System.currentTimeMillis()}")

        log("options: ${processingEnv.options}")

        log("annotations: $annotations")
        log("roundEnv: $roundEnv")

        if (annotations.isEmpty()) return false

        processingEnv.options.forEach { (key, value) ->
            log("""options["$key"] = $value""")
        }

        annotations.forEach {
            log("processing $it")
        }
        roundEnv.rootElements.forEach {
            log("rootElement: $it")
            log("enclosed elements: ${it.enclosedElements}")
            log("enclosing elements: ${it.enclosingElement}")
        }
        roundEnv.getElementsAnnotatedWith(Comment::class.java).forEach {
            log("element: $it")
        }

        try {
            val fileObj = processingEnv.filer.createResource(StandardLocation.CLASS_OUTPUT, "", "test2.json")

            fileObj.openOutputStream().use {
                it.bufferedWriter().use {
                    it.write("test2")
                }
            }
        } catch (e: FilerException) {
            log("test 2")
            val writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            log(writer.toString())
        }

//        val sourceFolder = File(generatedSourcesRoot).apply {
//            mkdirs()
//        }
//        val testFile = sourceFolder.resolve("test.json").apply {
//            createNewFile()
//        }
//
//        // Testing if i can write kt files
//        val sourceTest = File(generatedSourcesRoot).resolve("test.kt").apply {
//            createNewFile()
//        }
//        sourceTest.writeText(
//            """
//            const val version = 1
//        """.trimIndent()
//        )
//
//        testFile.writeText(
//            """
//            {
//                "version": 1
//            }
//        """.trimIndent()
//        )

        // Use stored value for output stream
        System.setOut(console)
        println("This will be written on the console!")
        return false
    }

    private fun log(msg: String) {
        if (processingEnv.options.containsKey("debug")) {
            processingEnv.messager.printMessage(Diagnostic.Kind.NOTE, msg)
        } else {
            println(msg)
        }
    }

    // Store current System.out before assigning a new value
    private val console = System.out

    fun openLog() {
        var logFile: FileObject? = null

        var n = 0
        do {
            try {
                logFile = processingEnv.filer.createResource(
                    StandardLocation.CLASS_OUTPUT,
                    "",
                    "annotationProcessor.log.$n.txt"
                )
            } catch (e: IOException) {
                val writer = StringWriter()
                e.printStackTrace(PrintWriter(writer))
                log(writer.toString())
                n++
                continue
            } catch (e: FilerException) {
                val writer = StringWriter()
                e.printStackTrace(PrintWriter(writer))
                log(writer.toString())
                n++
                continue
            }
        } while (logFile == null)

        val o = PrintStream(logFile.openOutputStream())

        // Assign o to output stream
        System.setOut(o)
    }

    fun closeLog() {
        System.setOut(console)
    }

    private fun error(msg: String, element: Element, annotation: AnnotationMirror) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg, element, annotation)
    }

    private fun fatalError(msg: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: $msg")
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }
}