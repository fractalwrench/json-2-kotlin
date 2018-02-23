import kotlin.Array
import kotlin.Number
import kotlin.String

data class PlaqueCachedContentsExample(val plaqueCachedContentsExampleArray: Array<PlaqueCachedContentsExampleArray>)

data class PlaqueCachedContentsExampleArray(val cachedContents: CachedContents?, val id: Number)

data class CachedContents(
    val `null`: Number,
    val average: String?,
    val largest: String,
    val non_null: Number,
    val smallest: String,
    val sum: String?,
    val top: Array<Top>?
)

data class Top(val count: Number, val item: String)
