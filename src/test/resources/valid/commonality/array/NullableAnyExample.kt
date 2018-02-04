import kotlin.String

data class NullableAnyExample(val firstField: Foo, val secondField: Foo)

data class Foo(val foo: Any?)
