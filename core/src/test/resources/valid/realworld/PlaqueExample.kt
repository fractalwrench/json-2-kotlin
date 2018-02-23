import kotlin.Any
import kotlin.Array
import kotlin.Boolean
import kotlin.Number
import kotlin.String

data class PlaqueExample(val data: Array<Any>, val meta: Meta)

data class Meta(val view: View)

data class View(
    val attribution: String,
    val attributionLink: String,
    val averageRating: Number,
    val category: String,
    val columns: Array<Columns>,
    val createdAt: Number,
    val description: String,
    val displayType: String,
    val downloadCount: Number,
    val flags: Array<String>,
    val grants: Array<Grants>,
    val hideFromCatalog: Boolean,
    val hideFromDataJson: Boolean,
    val iconUrl: String,
    val id: String,
    val indexUpdatedAt: Number,
    val license: License,
    val licenseId: String,
    val metadata: Metadata,
    val name: String,
    val newBackend: Boolean,
    val numberOfComments: Number,
    val oid: Number,
    val owner: Owner,
    val provenance: String,
    val publicationAppendEnabled: Boolean,
    val publicationDate: Number,
    val publicationGroup: Number,
    val publicationStage: String,
    val query: License,
    val rights: Array<String>,
    val rowClass: String,
    val rowIdentifierColumnId: Number,
    val rowsUpdatedAt: Number,
    val rowsUpdatedBy: String,
    val tableAuthor: Owner,
    val tableId: Number,
    val totalTimesRated: Number,
    val viewCount: Number,
    val viewLastModified: Number,
    val viewType: String
)

data class License(val name: String?)

data class Metadata(
    val availableDisplayTypes: Array<String>,
    val custom_fields: Custom_fields,
    val rdfClass: String,
    val rdfSubject: String,
    val renderTypeConfig: RenderTypeConfig,
    val rowIdentifier: String
)

data class Owner(
    val displayName: String,
    val flags: Array<String>,
    val id: String,
    val profileImageUrlLarge: String,
    val profileImageUrlMedium: String,
    val profileImageUrlSmall: String,
    val screenName: String,
    val type: String
)

data class Columns(
    val cachedContents: CachedContents?,
    val dataTypeName: String,
    val description: String?,
    val fieldName: String,
    val flags: Array<String>?,
    val format: Format,
    val id: Number,
    val name: String,
    val position: Number,
    val renderTypeName: String,
    val subColumnTypes: Array<String>?,
    val tableColumnId: Number?,
    val width: Number?
)

data class CachedContents(
    val `null`: Number,
    val average: String,
    val largest: String,
    val non_null: Number,
    val smallest: String,
    val sum: String,
    val top: Array<Top>?
)

data class Format(
    val align: String?,
    val noCommas: String?,
    val precisionStyle: String?
)

data class Top(val count: Number, val item: String) // TODO handle this (weird) scenario

data class Top(val count: Number, val item: Item) // TODO handle this scenario

data class Item(val latitude: String, val longitude: String)

data class Grants(
    val flags: Array<String>,
    val inherited: Boolean,
    val type: String
)

data class Custom_fields(val Additional_Licence_Detail: Additional_Licence_Detail, val Publication: Publication)

data class RenderTypeConfig(val visible: Visible)

data class Additional_Licence_Detail(
    val Additional_Licence_Information: String,
    val Licence_URL: String,
    val Re_user_Guidelines: String
)

data class Publication(val Update_Frequency: String)

data class Visible(val table: Boolean)
