package config

import blue.endless.jankson.Comment
import com.google.auto.service.AutoService
import com.sun.tools.javac.code.Type
import io.github.cottonmc.cotton.config.annotations.ConfigFile
import io.github.cottonmc.cotton.config.annotations.RangeValidatorFloat
import io.github.cottonmc.cotton.config.annotations.RangeValidatorInt
import io.github.cottonmc.cotton.config.annotations.RangeValidatorLong
import io.github.cottonmc.cotton.config.annotations.RegexValidator
import io.github.cottonmc.cotton.config.annotations.SetValidatorString
import java.io.IOException
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.FilerException
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.PrimitiveType
import javax.tools.Diagnostic
import javax.tools.FileObject
import javax.tools.StandardLocation

@SupportedAnnotationTypes(
    "config.ConfigurationAnnotation",
    "blue.endless.jankson.Comment",
    "io.github.cottonmc.cotton.config.annotations.ConfigFile",
    "io.github.cottonmc.cotton.config.annotations.ValidRangeFloat",
    "io.github.cottonmc.cotton.config.annotations.ValidRangeInt",
    "io.github.cottonmc.cotton.config.annotations.ValidValuesString"
)
@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(
    ConfigProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME,
    "debug",
    "verify"
)
class ConfigProcessor : AbstractProcessor() {
//    override fun getSupportedAnnotationTypes(): MutableSet<String> {
//        return mutableSetOf(
//            "config.ConfigurationAnnotation",
//            "blue.endless.jankson.Comment",
//            "io.github.cottonmc.cotton.config.annotations.ConfigFile",
//            "io.github.cottonmc.cotton.config.annotations.ValidRangeFloat",
//            "io.github.cottonmc.cotton.config.annotations.ValidRangeInt",
//            "io.github.cottonmc.cotton.config.annotations.ValidValuesString"
//        )
//    }

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
        processingEnv.options.forEach { (key, value) ->
            log("""options["$key"] = $value""")
        }

        log("annotations: $annotations")
        log("roundEnv: $roundEnv")

        if (annotations.isEmpty()) return false

        val configFiles: MutableList<Element> = mutableListOf()
        roundEnv.getElementsAnnotatedWith(ConfigFile::class.java).forEach { configFileElement ->
            configFiles += configFileElement
            // TODO: set rootElement type name
            log("configFileElement: $configFileElement")
            log("configFileElement::class: ${configFileElement::class}")
            log("  kind: ${configFileElement.kind}")

            val typeElement = processingEnv.elementUtils.getTypeElement(configFileElement.toString())
            addType(typeElement)
        }
        annotations.forEach { annotation ->
            log("processing annotation '$annotation'")
            roundEnv.getElementsAnnotatedWith(annotation).forEach { element ->
                log("  element: $element")
                log("    kind: ${element.kind}")

            }
            log("")
        }
        roundEnv.rootElements.forEach { rootElement ->
            log("rootElement: $rootElement")
            log("  kind: ${rootElement.kind}")
            log("  enclosed elements: ${rootElement.enclosedElements}")
            log("  enclosing elements: ${rootElement.enclosingElement}")
        }
        roundEnv.getElementsAnnotatedWith(Comment::class.java).forEach { comment ->
            log("element: $comment")
            val configFile = configFiles.find { configFile ->
                comment.enclosingElement == configFile
            }
            log("  configFile = $configFile")
            log("")
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

        types.forEach { key, fields ->
            log("type: $key")
            fields.forEach { field ->
                log("  field: $field")
            }
        }

        // Use stored value for output stream
        System.setOut(console)
        println("This will be written on the console!")
        return false
    }

    val types: MutableMap<String, List<Field>> = mutableMapOf()
    fun addType(typeElement: TypeElement) {
        // TODO: pparse type
        if (types.containsKey(typeElement.toString())) {
            log("type: $typeElement is already registered")
            log("${types[typeElement.toString()]}")
            return
        }
        if (typeElement.toString().startsWith("java.lang.")) {
            log("not processing builtin types")
            return
        }
        log(">>> processing $typeElement")
        val fields: MutableList<Field> = mutableListOf()

//        val clazz = javaClass.classLoader.loadClass(typeElement.qualifiedName.toString())
//        val instance = clazz.constructors[0].newInstance()
//        log("default instance: $instance")

        enclosedLoop@ for (enclosedElement in typeElement.enclosedElements) {
            when (enclosedElement.kind) {
                ElementKind.METHOD, ElementKind.CONSTRUCTOR -> {
                    // ignore constructors
                    continue@enclosedLoop
                }
                else -> {
                }
            }
            log("    enclosed: $enclosedElement")
            log("      kind: ${enclosedElement.kind}")

            when (enclosedElement.kind) {
                ElementKind.METHOD -> {
                    // ignore getters and setters
                    continue@enclosedLoop
                }
                ElementKind.CLASS -> {
                    // ignore classes for now
                    continue@enclosedLoop
                }
                ElementKind.CONSTRUCTOR -> {
                    // ignore constructors
                    continue@enclosedLoop
                }
                ElementKind.FIELD -> {
                    val type = enclosedElement.asType()
                    log("      type: $type")
                    log("      type.kind: ${type.kind}")
                    log("      type::class: ${type::class}")
                    val subTypeElement = processingEnv.elementUtils.getTypeElement(type.toString()) ?: null
                    log("      typeElement: $subTypeElement")
                    if (subTypeElement != null) {
                        log("      typeElement::class: ${subTypeElement::class}")
                        log("      typeElement.superClass: ${subTypeElement.superclass}")
                    }

                    val stringType = processingEnv.elementUtils.getTypeElement("java.lang.String").asType()
                    val booleanType = processingEnv.elementUtils.getTypeElement("java.lang.Boolean").asType()
                    val byteType = processingEnv.elementUtils.getTypeElement("java.lang.Byte").asType()
                    val shortType = processingEnv.elementUtils.getTypeElement("java.lang.Short").asType()
                    val integerType = processingEnv.elementUtils.getTypeElement("java.lang.Integer").asType()
                    val longType = processingEnv.elementUtils.getTypeElement("java.lang.Long").asType()
                    val floatType = processingEnv.elementUtils.getTypeElement("java.lang.Float").asType()
                    val doubleType = processingEnv.elementUtils.getTypeElement("java.lang.Double").asType()
                    val enumType = processingEnv.elementUtils.getTypeElement(Enum::class.java.canonicalName).asType()

                    val validators: MutableMap<Int, MutableList<ConfigValidator>> = mutableMapOf()

                    val fieldType = when (type) {
                        is Type.ArrayType -> {
                            log("        array type: $type")
                            type.toString()

                            when {
                                processingEnv.typeUtils.isAssignable(type.elemtype, stringType) -> {
                                    assertMatchingAnnotationType(enclosedElement, 0,"String")

                                    val validatorList = validators.getOrPut(0) { mutableListOf() }
                                    validatorList += getStringValidator(enclosedElement, 0)
                                }
                                processingEnv.typeUtils.isAssignable(type.elemtype, byteType) -> {
                                    assertMatchingAnnotationType(enclosedElement, 0,"byte")
                                }
                            }
                            type.toString()

                            // TODO: check all primitive + String + enum arrays
                            // TODO: apply validators
                        }
//                        is Type.UnionClassType -> {
//                            log("        union class type: $type")
//
//                            type.toString()
//                        }
                        is Type.ClassType -> {
                            log("        class type: $type")

                            val typeParams = type.typarams_field
                            log("          type params: $typeParams")

//                            val isList = type.toString().startsWith("java.util.List")
//                            val isSet = type.toString().startsWith("java.util.Set")
                            when {
                                processingEnv.typeUtils.isAssignable(type, stringType) -> {
                                    // load valid string values
                                    assertMatchingAnnotationType(enclosedElement, 0, "String")

                                    val validatorList = validators.getOrPut(0) { mutableListOf() }
                                    validatorList += getStringValidator(enclosedElement, 0)

                                    type.toString()
                                }
                                processingEnv.typeUtils.isAssignable(type, byteType) -> {
                                    //TODO: load min/max byte values
                                    type.toString()
                                }
                                processingEnv.typeUtils.isAssignable(type, shortType) -> {
                                    //TODO: load min/max short values
                                    type.toString()
                                }
                                processingEnv.typeUtils.isAssignable(type, integerType) -> {
                                    //TODO: load min/max int values
                                    type.toString()
                                }
                                type.isParameterized -> {
                                    var index = 0
                                    for (parameter in typeParams) {
                                        log("parameter: $parameter")
                                        if (processingEnv.typeUtils.isAssignable(parameter, stringType)) {
                                            // load valid string values
                                            assertMatchingAnnotationType(enclosedElement, index,"String")

                                            continue
                                        }

                                        // TODO: check for all other builtins (java.lang.*)
                                        // TODO: parse all validators

                                        val parameterElement =
                                            processingEnv.elementUtils.getTypeElement(parameter.toString())
                                        log("parameter::class: ${parameter::class}")
                                        log("parameterElement::class: ${parameterElement::class}")
                                        addType(parameterElement)
                                        index++
                                    }
                                    type.toString()
                                }
                                subTypeElement?.kind == ElementKind.ENUM -> {
                                    log("is enum type: $type")
                                    // TODO: process enum

                                    type.toString()
                                }
                                subTypeElement != null -> {
                                    log("is not a list, set or string")

                                    // TODO: add to extra types
                                    addType(subTypeElement)
                                    type.toString()
                                }
                                else -> {
                                    require(false) {
                                        "unhandled state"
                                    }
                                }
                            }

                            type.toString()
                        }
                        is Type.JCPrimitiveType -> {

                            log("constValue: ${type.constValue()}")
//                            log("stringValue: ${type.stringValue()}")
                            when (type.toString()) {
                                "boolean" -> {
                                    log("is boolean")
                                }
                                "byte" -> {
                                    log("is byte")
                                }
                                "short" -> {
                                    log("is short")
                                }
                                "int" -> {
                                    log("is int")
                                }
                                "long" -> {
                                    log("is long")
                                }
                                "char" -> {
                                    log("is char")
                                }
                                "float" -> {
                                    log("is float")
                                }
                                "double" -> {
                                    log("is double")
                                }
                                else -> {
                                    TODO(type.toString())
                                }
                            }

                            type.toString()
                        }
                        is PrimitiveType -> {
                            log("        primitve type: $type")
                            type.toString()
                        }
                        else -> TODO()
                    }

                    // comment
                    val comment = enclosedElement.getAnnotation(Comment::class.java)?.value

                    fields += Field(
                        name = enclosedElement.simpleName,
                        type = fieldType,
                        comment = comment,
                        validators = validators
                    )

                    log("      annotationMirrors: ${enclosedElement.annotationMirrors}")
                }
                ElementKind.ENUM_CONSTANT -> {
                    // ignore enum constants
                }
                else -> {
                    TODO(enclosedElement.kind.name)
                }
            }
            //                enclosedElement.annotationMirrors
        }
        types[typeElement.toString()] = fields
    }

    fun getStringValidator(element: Element, typeIndex: Int): List<StringValidator> {
        val setValidator = element.getAnnotation(SetValidatorString::class.java)
            ?.takeIf { it.typeIndex.contains(typeIndex) }
            ?.let {
                config.SetValidatorString(it.values.toSet())
            }
        val regexValidator = element.getAnnotation(RegexValidator::class.java)
            ?.takeIf { it.typeIndex.contains(typeIndex) }
            ?.let {
                config.RegexValidator(it.regex)
            }
        return listOfNotNull(setValidator, regexValidator)
    }

    fun assertMatchingAnnotationType(element: Element, typeIndex: Int, type: String) {
        listOf(
            *element.getAnnotationsByType(RangeValidatorInt::class.java).filter { it.typeIndex.contains(typeIndex) }.toTypedArray(),
            *element.getAnnotationsByType(RangeValidatorFloat::class.java).filter { it.typeIndex.contains(typeIndex) }.toTypedArray(),
            *element.getAnnotationsByType(RangeValidatorLong::class.java).filter { it.typeIndex.contains(typeIndex) }.toTypedArray(),
            *element.getAnnotationsByType(RegexValidator::class.java).filter { it.typeIndex.contains(typeIndex) }.toTypedArray(),
            *element.getAnnotationsByType(SetValidatorString::class.java).filter { it.typeIndex.contains(typeIndex) }.toTypedArray()
        ).forEach {
            assertMatchingAnnotationType(it, type)
        }
    }

    fun assertMatchingAnnotationType(annotation: Annotation, type: String) {
        val expectedType = when (annotation) {
            is RangeValidatorInt -> "int"
            is RangeValidatorFloat -> "floar"
            is RangeValidatorLong -> "long"
            is RegexValidator, is SetValidatorString -> "String"
            else -> {
                log("unhandled annotaion: $annotation")
                return
            }
        }

        if (type != expectedType) {
            log("annotation '$annotation' should only be applied to $expectedType, but was applied to $type")
        }
        require(type == expectedType) {
            "annotation '$annotation' should only be applied to $expectedType, but was applied to $type"
        }
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