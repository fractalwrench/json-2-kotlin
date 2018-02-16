import kotlin.String

data class NestedObjExample(val firstField: FirstField)

data class FirstField(val secondField: SecondField)

data class SecondField(val thirdField: ThirdField)

data class ThirdField(val foo: String)
