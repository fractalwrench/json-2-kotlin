import kotlin.String

data class PlaqueExample(val meta: Meta)

data class Meta(val view: View)

data class View(
    val id: String,
    val name: String,
    val attribution: String,
    val attributionLink: String,
    val averageRating: Number,
    val category: String,
    val createdAt: Number,
    val description: String,
    val displayType: String,
    val downloadCount: Number,
    val hideFromCatalog: Boolean,
    val hideFromDataJson: Boolean,
    val iconUrl: String,
    val indexUpdatedAt: Number,
    val licenseId: String,
    val newBackend: Boolean,
    val numberOfComments: Number,
    val oid: Boolean,
    val provenance: String,
    val publicationAppendEnabled: Boolean,
    val publicationDate: Number,
    val publicationGroup: Number,
    val publicationStage: String,
    val rowClass: String,
    val rowIdentifierColumnId: Number,
    val rowsUpdatedAt: Number,
    val rowsUpdatedBy: String,
    val tableId: Number,
    val totalTimesRated: Number,
    val viewCount: Number,
    val viewLastModified: Number,
    val viewType: String,
    val columns: Array<Columns>
)

data class Columns(
    val id: Number,
    val name: String,
    val dataTypeName: String,
    val description: String?,
    val fieldName: String,
    val position: Number,
    val renderTypeName: String,
    val tableColumnId: Number?,
    val cachedContents: CachedContents?,
    val width: Number?,
    val format: Array<FormatObject>,
    val flags: Array<String>
)

data class CachedContents(
    val non_null: String,
    val average: String,
    val largest: String,
    val null: Number,
    val top: Array<TopField>,
    val smallest: String,
    val sum: String
)

data class TopField(
    val item: String,
    val count: Number
)

data class FormatObject(
    val precisionStyle: String,
    val noCommas: String,
    val align: String
)