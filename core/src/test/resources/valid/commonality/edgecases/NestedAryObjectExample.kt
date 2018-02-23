import kotlin.Array
import kotlin.Number
import kotlin.String

data class NestedAryObjectExample(val nestedAryObjectExampleArray: Array<NestedAryObjectExampleArray>)

data class NestedAryObjectExampleArray(val cachedContents: CachedContents?, val id: Number)

data class CachedContents(
    val average: String?,
    val largest: String,
    val top: Array<Top>?
)

data class Top(val count: Number, val item: String)
