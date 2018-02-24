import kotlin.String

data class TransientStrExample(
        val firstField: FirstField,
        val secondField: FirstField,
        val thirdField: FirstField
)

data class FirstField(val bar: String?, val foo: String?)
