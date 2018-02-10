import kotlin.Array
import kotlin.String

data class ArrayExampleContainer(val arrayExampleField: Array<ArrayExampleField>)

data class ArrayExampleField(val third: String)

data class ArrayExampleField2(val second: String)

data class ArrayExampleField3(val first: String)