import kotlin.Array
import kotlin.String

data class TypeClashExample(val typeClashExampleArray: Array<TypeClashExampleArray>)

data class TypeClashExampleArray(val cachedContents: CachedContents)

data class CachedContents(val top: Array<Top>?)

data class Top(val item: String) // FIXME

data class CachedContents(val top: Array<Top>)

data class Top(val item: Item) // FIXME

data class Item(val latitude: String, val longitude: String)
