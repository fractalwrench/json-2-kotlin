import com.google.gson.annotations.SerializedName
import kotlin.String

data class KeywordsExample(
    @SerializedName(value="!in")
    val `!in`: String,
    @SerializedName(value="!is")
    val `!is`: String,
    @SerializedName(value="as?")
    val `as?`: String,
    @SerializedName(value="as")
    val `as`: String,
    @SerializedName(value="break")
    val `break`: String,
    @SerializedName(value="class")
    val `class`: String,
    @SerializedName(value="continue")
    val `continue`: String,
    @SerializedName(value="do")
    val `do`: String,
    @SerializedName(value="else")
    val `else`: String,
    @SerializedName(value="false")
    val `false`: String,
    @SerializedName(value="for")
    val `for`: String,
    @SerializedName(value="fun")
    val `fun`: String,
    @SerializedName(value="if")
    val `if`: String,
    @SerializedName(value="in")
    val `in`: String,
    @SerializedName(value="interface")
    val `interface`: String,
    @SerializedName(value="is")
    val `is`: String,
    @SerializedName(value="null")
    val `null`: String,
    @SerializedName(value="object")
    val `object`: String,
    @SerializedName(value="package")
    val `package`: String,
    @SerializedName(value="return")
    val `return`: String,
    @SerializedName(value="super")
    val `super`: String,
    @SerializedName(value="this")
    val `this`: String,
    @SerializedName(value="throw")
    val `throw`: String,
    @SerializedName(value="try")
    val `try`: String,
    @SerializedName(value="typealias")
    val `typealias`: String,
    @SerializedName(value="val")
    val `val`: String,
    @SerializedName(value="var")
    val `var`: String,
    @SerializedName(value="when")
    val `when`: String,
    @SerializedName(value="while")
    val `while`: String
)
