import com.google.gson.annotations.SerializedName
import kotlin.String

data class SanitiseExample(
    @SerializedName(value="1")
    val _1: String,
    @SerializedName(value="$a")
    val _a: String,
    @SerializedName(value="!f")
    val _f: String,
    @SerializedName(value="Test!word1")
    val Test_word1: String,
    @SerializedName(value="Test    word10")
    val Test_word10: String,
    @SerializedName(value="Test.word11")
    val Test_word11: String,
    @SerializedName(value="Test"word2")
    val Test_word2: String,
    @SerializedName(value="Test£word3")
    val Test_word3: String,
    @SerializedName(value="Test$word4")
    val Test_word4: String,
    @SerializedName(value="Test%word5")
    val Test_word5: String,
    @SerializedName(value="Test^word6")
    val Test_word6: String,
    @SerializedName(value="Test&word7")
    val Test_word7: String,
    @SerializedName(value="Test*word8")
    val Test_word8: String,
    @SerializedName(value="Test{word9")
    val Test_word9: String
)
