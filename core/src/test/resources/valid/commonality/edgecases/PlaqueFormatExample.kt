import kotlin.Any
import kotlin.Array
import kotlin.Number
import kotlin.String

data class PlaqueFormatExample(val view: View)

data class View(val columns: Array<Columns>)

data class Columns(
    val format: Format,
)

data class Format(
    val align: String?,
    val noCommas: String?,
    val precisionStyle: String?
)
