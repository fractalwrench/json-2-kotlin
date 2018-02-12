import kotlin.Array
import kotlin.String

data class ArrayFieldExample(val firstField: FirstField, val secondField: FirstField)

data class FirstField(val another: String?, val foo: Array<String>)
