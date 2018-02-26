package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.MoviesAryExampleArray
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class MoviesGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "MoviesAry"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, Array<MoviesAryExampleArray>::class.java)
        assertNotNull(gson)
        assertEquals(3, gson.size)

        with(gson[0]) {
            assertEquals("Star Wars: Episode IV - A New Hope", Title)
            assertEquals("1977", Year)
            assertEquals("PG", Rated)
            assertEquals("25 May 1977", Released)
            assertEquals("121 min", Runtime)
            assertEquals("Action, Adventure, Fantasy", Genre)
            assertEquals("George Lucas", Director)
            assertEquals("George Lucas", Writer)
            assertEquals("Mark Hamill, Harrison Ford, Carrie Fisher, Peter Cushing", Actors)
            assertEquals("Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a Wookiee and two droids to save the galaxy from the Empire's world-destroying battle-station while also attempting to rescue Princess Leia from the evil Darth Vader.", Plot)
            assertEquals("English", Language)
            assertEquals("USA", Country)
            assertEquals("Won 6 Oscars. Another 50 wins & 28 nominations.", Awards)
            assertEquals("https://images-na.ssl-images-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg", Poster)
            assertEquals("90", Metascore)
            assertEquals("8.7", imdbRating)
            assertEquals("1,029,576", imdbVotes)
            assertEquals("tt0076759", imdbID)
            assertEquals("movie", Type)
            assertEquals("21 Sep 2004", DVD)
            assertEquals("N/A", BoxOffice)
            assertEquals("20th Century Fox", Production)
            assertEquals("http://www.starwars.com/episode-iv/", Website)
            assertEquals("True", Response)

            assertEquals("Internet Movie Database", Ratings[0].Source)
            assertEquals("8.7/10", Ratings[0].Value)
            assertEquals("Rotten Tomatoes", Ratings[1].Source)
            assertEquals("93%", Ratings[1].Value)
            assertEquals("Metacritic", Ratings[2].Source)
            assertEquals("90/100", Ratings[2].Value)
        }

        with(gson[1]) {
            assertEquals("Rockwell Tools Bladerunner X2", Title)
            assertEquals("2015", Year)
            assertEquals("N/A", Rated)
            assertEquals("01 Jan 2015", Released)
            assertEquals("N/A", Runtime)
            assertEquals("N/A", Genre)
            assertEquals("N/A", Director)
            assertEquals("N/A", Writer)
            assertEquals("N/A", Actors)
            assertEquals("N/A", Plot)
            assertEquals("English", Language)
            assertEquals("USA", Country)
            assertEquals("N/A", Awards)
            assertEquals("N/A", Poster)
            assertEquals("N/A", Metascore)
            assertEquals("N/A", imdbRating)
            assertEquals("N/A", imdbVotes)
            assertEquals("tt5855480", imdbID)
            assertEquals("movie", Type)
            assertEquals("N/A", DVD)
            assertEquals("N/A", BoxOffice)
            assertEquals("N/A", Production)
            assertEquals("N/A", Website)
            assertEquals("True", Response)

            assertNotNull(Ratings)
            assertTrue(Ratings.isEmpty())
        }

        with(gson[2]) {
            assertEquals("The Lion King", Title)
            assertEquals("1994", Year)
            assertEquals("G", Rated)
            assertEquals("24 Jun 1994", Released)
            assertEquals("88 min", Runtime)
            assertEquals("Animation, Adventure, Drama", Genre)
            assertEquals("Roger Allers, Rob Minkoff", Director)
            assertEquals("Irene Mecchi (screenplay by), Jonathan Roberts (screenplay by), Linda Woolverton (screenplay by), Burny Mattinson (story), Barry Johnson (story), Lorna Cook (story), Thom Enriquez (story), Andy Gaskill (story), Gary Trousdale (story), Jim Capobianco (story), Kevin Harkey (story), Jorgen Klubien (story), Chris Sanders (story), Tom Sito (story), Larry Leker (story), Joe Ranft (story), Rick Maki (story), Ed Gombert (story), Francis Glebas (story), Mark Kausler (story), J.T. Allen (additional story material), George Scribner (additional story material), Miguel Tejada-Flores (additional story material), Jenny Tripp (additional story material), Bob Tzudiker (additional story material), Christopher Vogler (additional story material), Kirk Wise (additional story material), Noni White (additional story material), Brenda Chapman (story supervisor)", Writer)
            assertEquals("Rowan Atkinson, Matthew Broderick, Niketa Calame, Jim Cummings", Actors)
            assertEquals("Lion cub and future king Simba searches for his identity. His eagerness to please others and penchant for testing his boundaries sometimes gets him into trouble.", Plot)
            assertEquals("English, Swahili, Xhosa, Zulu", Language)
            assertEquals("USA", Country)
            assertEquals("Won 2 Oscars. Another 33 wins & 29 nominations.", Awards)
            assertEquals("https://images-na.ssl-images-amazon.com/images/M/MV5BYTYxNGMyZTYtMjE3MS00MzNjLWFjNmYtMDk3N2FmM2JiM2M1XkEyXkFqcGdeQXVyNjY5NDU4NzI@._V1_SX300.jpg", Poster)
            assertEquals("83", Metascore)
            assertEquals("8.5", imdbRating)
            assertEquals("740,390", imdbVotes)
            assertEquals("tt0110357", imdbID)
            assertEquals("movie", Type)
            assertEquals("07 Oct 2003", DVD)
            assertEquals("\$94,240,635", BoxOffice)
            assertEquals("Buena Vista", Production)
            assertEquals("http://disney.go.com/lionking/", Website)
            assertEquals("True", Response)

            assertEquals("Internet Movie Database", Ratings[0].Source)
            assertEquals("8.5/10", Ratings[0].Value)
            assertEquals("Rotten Tomatoes", Ratings[1].Source)
            assertEquals("92%", Ratings[1].Value)
            assertEquals("Metacritic", Ratings[2].Source)
            assertEquals("83/100", Ratings[2].Value)
        }
    }

}