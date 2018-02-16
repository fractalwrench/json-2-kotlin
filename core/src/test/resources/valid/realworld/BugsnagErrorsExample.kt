import kotlin.Array
import kotlin.Number
import kotlin.String

data class BugsnagErrorsExample(val bugsnagErrorsExampleArray: Array<BugsnagErrorsExampleArray>)

data class BugsnagErrorsExampleArray(
    val event_field_value: String,
    val events: Number,
    val fields: Fields,
    val first_seen: String,
    val last_seen: String,
    val proportion: Number
)

data class Fields(val user_email: String, val user_name: String)
