import com.google.gson.annotations.SerializedName
import kotlin.Any
import kotlin.Array
import kotlin.Number
import kotlin.String

data class NestedObjObjExample(val nestedObjObjExampleArray: Array<NestedObjObjExampleArray>)

data class NestedObjObjExampleArray(val container: Container?, val format: Container)

data class Container(
        @SerializedName(value="null")
        val `null`: Number?,
        val align: String?,
        val bar: String?,
        val cat: Number?,
        val dog: Number?,
        val foo: Number?,
        val largest: Any?,
        val noCommas: String?,
        val non_null: Number?,
        val precisionStyle: String?,
        val smallest: Any?,
        val top: Array<Top>?,
        val view: String?,
        val whistle: String?
)

data class Largest(val latitude: String, val longitude: String)

data class Top(val count: Number, val item: Item)

data class Item(val latitude: String, val longitude: String)
