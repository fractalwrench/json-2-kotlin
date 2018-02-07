import kotlin.String

data class NestedObjExample(val objField: FirstField)

data class FirstField(val objField: SecondField)

data class SecondField(val secondField: ThirdField)

data class ThirdField(val foo: String)
