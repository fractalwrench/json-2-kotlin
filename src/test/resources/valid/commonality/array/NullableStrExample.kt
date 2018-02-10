import kotlin.Array
import kotlin.String

data class NullableStrExample(val nullableStrExampleField: Array<NullableStrExampleField>)

data class NullableStrExampleField(val foo: String?)
