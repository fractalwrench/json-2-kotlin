import kotlin.Array
import kotlin.Number
import kotlin.String

data class BugsnagErrorsExampleContainer(val bugsnagErrorsExampleField: Array<BugsnagErrorsExampleField>)

data class BugsnagErrorsExampleField(
      val event_field_value: String,
      val events: Number,
      val fields: Fields,
      val first_seen: String,
      val last_seen: String,
      val proportion: Number
)

data class Fields(val user.email: String, val user.name: String)