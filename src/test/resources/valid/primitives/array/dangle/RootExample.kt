import kotlin.Array
import kotlin.String

data class RootExampleContainer(val rootExampleField: Array<RootExampleFieldObject>)

data class RootExampleFieldObject(val foo: String)
