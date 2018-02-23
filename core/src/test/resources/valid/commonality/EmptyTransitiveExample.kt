import kotlin.String

data class EmptyTransitiveExample(
    val firstField: FirstField,
    val secondField: SecondField,
    val thirdField: ThirdField
)

data class FirstField(
    val a: String,
    val b: String,
    val c: String,
    val d: String,
    val e: String,
    val f: String
)

data class SecondField(
    val g: String,
    val h: String,
    val i: String,
    val j: String,
    val k: String,
    val l: String
)

class ThirdField
