import kotlin.Boolean
import kotlin.String

data class ReverseSortNestedExample(
    val anteater: Anteater,
    val bird: Bird,
    val cow: Cow,
    val lima: Lima
)

data class Anteater(val test4: String)

data class Bird(val alpha: Alpha)

data class Cow(val zoo: Zoo)

data class Lima(val meshuggah: Meshuggah)

data class Alpha(val awesome: Boolean)

data class Meshuggah(val description: String)

data class Zoo(val owner: String)
