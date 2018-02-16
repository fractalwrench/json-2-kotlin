import kotlin.Any

data class NullableAnyExample(
    val firstField: FirstField,
    val secondField: FirstField,
    val thirdField: FirstField
)

data class FirstField(val foo: Any?)
