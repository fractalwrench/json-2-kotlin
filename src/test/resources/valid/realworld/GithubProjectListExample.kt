import kotlin.Array
import kotlin.Boolean
import kotlin.Number
import kotlin.String

data class GithubProjectListExampleContainer(val githubProjectListExampleField: Array<GithubProjectListExampleField>)

data class GithubProjectListExampleField(
    val owner_url: String,
    val url: String,
    val html_url: String,
    val columns_url: String,
    val id: Number,
    val name: String,
    val body: String,
    val number: Number,
    val state: String,
    val creator: Creator,
    val created_at: String,
    val updated_at: String
)

data class Creator(
    val login: String,
    val id: Number,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String,
    val site_admin: Boolean
)
