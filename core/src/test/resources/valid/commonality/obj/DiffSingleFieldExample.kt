import kotlin.String

data class DiffSingleFieldExample(val firstField: FirstField, val secondField: SecondField)

data class FirstField(val foo: String)

data class SecondField(val bar: String)
