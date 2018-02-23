import kotlin.Array
import kotlin.Number
import kotlin.String

data class PlaqueItemExample(val plaqueItemExampleArray: Array<PlaqueItemExampleArray>)

data class PlaqueItemExampleArray(val cachedContents: CachedContents)

data class CachedContents(val top: Array<Top>)

data class Top(val count: Number, val item: String) // FIXME

data class CachedContents(val top: Array<Top>?)

data class Top(val count: Number, val item: Item) // FIXME

data class Item(val latitude: String, val longitude: String)
