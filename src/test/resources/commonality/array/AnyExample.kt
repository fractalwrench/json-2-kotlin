import kotlin.String

data class AnyExample(val firstField: Foo, val secondField: Foo)

data class Foo(val foo: Any?)
