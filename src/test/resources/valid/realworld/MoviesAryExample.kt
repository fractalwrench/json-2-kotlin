import kotlin.Array
import kotlin.String

data class MoviesAryExample(val moviesAryExampleArray: Array<Movies>)

data class MoviesAryExampleArray(
    val Actors: String,
    val Awards: String,
    val BoxOffice: String,
    val Country: String,
    val Director: String,
    val DVD: String,
    val Genre: String,
    val imdbID: String,
    val imdbRating: String,
    val imdbVotes: String,
    val Language: String,
    val Metascore: String,
    val Plot: String,
    val Poster: String,
    val Production: String,
    val Rated: String,
    val Ratings: Array<Ratings>,
    val Released: String,
    val Response: String,
    val Runtime: String,
    val Title: String,
    val Type: String,
    val Website: String,
    val Writer: String,
    val Year: String
)

data class Ratings(val Source: String, val Value: String)
