import kotlin.Boolean
import kotlin.String

data class NullableObjExample(
    val firstField: FirstField,
    val fourthField: FirstField,
    val secondField: FirstField,
    val thirdField: FirstField
)

data class FirstField(val foo: Foo?)

data class Foo(val animal: String, val edible: Boolean)
