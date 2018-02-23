import kotlin.Array
import kotlin.Boolean
import kotlin.String

data class NullableObjExample(val nullableObjExampleArray: Array<NullableObjExampleArray>)

data class NullableObjExampleArray(val foo: Foo?)

data class Foo(val animal: String, val edible: Boolean)
