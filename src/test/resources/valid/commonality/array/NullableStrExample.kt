import kotlin.Array
import kotlin.String

data class NullableStrExampleContainer(val nullableStrExampleField: Array<NullableStrExampleField>)

data class NullableStrExampleField(val foo: String?)
