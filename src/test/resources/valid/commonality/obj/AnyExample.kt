import kotlin.String

data class AnyExample(val firstField: FirstField, val secondField: FirstField)

data class FirstField(val foo: Any)
