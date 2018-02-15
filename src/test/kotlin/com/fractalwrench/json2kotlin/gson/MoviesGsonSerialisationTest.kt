package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.MoviesAryExampleArray
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class MoviesGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "MoviesAry"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, Array<MoviesAryExampleArray>::class.java)
        Assert.assertNotNull(gson)
        Assert.assertEquals(3, gson.size)

        with(gson[0]) {
            Assert.assertEquals("Star Wars: Episode IV - A New Hope", Title)
            Assert.assertEquals("1977", Year)
            Assert.assertEquals("PG", Rated)
            Assert.assertEquals("25 May 1977", Released)
            Assert.assertEquals("121 min", Runtime)
            Assert.assertEquals("Action, Adventure, Fantasy", Genre)
            Assert.assertEquals("George Lucas", Director)
            Assert.assertEquals("George Lucas", Writer)
            Assert.assertEquals("Mark Hamill, Harrison Ford, Carrie Fisher, Peter Cushing", Actors)
            Assert.assertEquals("Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a Wookiee and two droids to save the galaxy from the Empire's world-destroying battle-station while also attempting to rescue Princess Leia from the evil Darth Vader.", Plot)
            Assert.assertEquals("English", Language)
            Assert.assertEquals("USA", Country)
            Assert.assertEquals("Won 6 Oscars. Another 50 wins & 28 nominations.", Awards)
            Assert.assertEquals("https://images-na.ssl-images-amazon.com/images/M/MV5BNzVlY2MwMjktM2E4OS00Y2Y3LWE3ZjctYzhkZGM3YzA1ZWM2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg", Poster)
            Assert.assertEquals("90", Metascore)
            Assert.assertEquals("8.7", imdbRating)
            Assert.assertEquals("1,029,576", imdbVotes)
            Assert.assertEquals("tt0076759", imdbID)
            Assert.assertEquals("movie", Type)
            Assert.assertEquals("21 Sep 2004", DVD)
            Assert.assertEquals("N/A", BoxOffice)
            Assert.assertEquals("20th Century Fox", Production)
            Assert.assertEquals("http://www.starwars.com/episode-iv/", Website)
            Assert.assertEquals("True", Response)

            Assert.assertEquals("Internet Movie Database", Ratings[0].Source)
            Assert.assertEquals("8.7/10", Ratings[0].Value)
            Assert.assertEquals("Rotten Tomatoes", Ratings[1].Source)
            Assert.assertEquals("93%", Ratings[1].Value)
            Assert.assertEquals("Metacritic", Ratings[2].Source)
            Assert.assertEquals("90/100", Ratings[2].Value)
        }

        with(gson[1]) {
            Assert.assertEquals("Rockwell Tools Bladerunner X2", Title)
            Assert.assertEquals("2015", Year)
            Assert.assertEquals("N/A", Rated)
            Assert.assertEquals("01 Jan 2015", Released)
            Assert.assertEquals("N/A", Runtime)
            Assert.assertEquals("N/A", Genre)
            Assert.assertEquals("N/A", Director)
            Assert.assertEquals("N/A", Writer)
            Assert.assertEquals("N/A", Actors)
            Assert.assertEquals("N/A", Plot)
            Assert.assertEquals("English", Language)
            Assert.assertEquals("USA", Country)
            Assert.assertEquals("N/A", Awards)
            Assert.assertEquals("N/A", Poster)
            Assert.assertEquals("N/A", Metascore)
            Assert.assertEquals("N/A", imdbRating)
            Assert.assertEquals("N/A", imdbVotes)
            Assert.assertEquals("tt5855480", imdbID)
            Assert.assertEquals("movie", Type)
            Assert.assertEquals("N/A", DVD)
            Assert.assertEquals("N/A", BoxOffice)
            Assert.assertEquals("N/A", Production)
            Assert.assertEquals("N/A", Website)
            Assert.assertEquals("True", Response)

            Assert.assertNotNull(Ratings)
            Assert.assertTrue(Ratings.isEmpty())
        }

        with(gson[2]) {
            Assert.assertEquals("The Lion King", Title)
            Assert.assertEquals("1994", Year)
            Assert.assertEquals("G", Rated)
            Assert.assertEquals("24 Jun 1994", Released)
            Assert.assertEquals("88 min", Runtime)
            Assert.assertEquals("Animation, Adventure, Drama", Genre)
            Assert.assertEquals("Roger Allers, Rob Minkoff", Director)
            Assert.assertEquals("Irene Mecchi (screenplay by), Jonathan Roberts (screenplay by), Linda Woolverton (screenplay by), Burny Mattinson (story), Barry Johnson (story), Lorna Cook (story), Thom Enriquez (story), Andy Gaskill (story), Gary Trousdale (story), Jim Capobianco (story), Kevin Harkey (story), Jorgen Klubien (story), Chris Sanders (story), Tom Sito (story), Larry Leker (story), Joe Ranft (story), Rick Maki (story), Ed Gombert (story), Francis Glebas (story), Mark Kausler (story), J.T. Allen (additional story material), George Scribner (additional story material), Miguel Tejada-Flores (additional story material), Jenny Tripp (additional story material), Bob Tzudiker (additional story material), Christopher Vogler (additional story material), Kirk Wise (additional story material), Noni White (additional story material), Brenda Chapman (story supervisor)", Writer)
            Assert.assertEquals("Rowan Atkinson, Matthew Broderick, Niketa Calame, Jim Cummings", Actors)
            Assert.assertEquals("Lion cub and future king Simba searches for his identity. His eagerness to please others and penchant for testing his boundaries sometimes gets him into trouble.", Plot)
            Assert.assertEquals("English, Swahili, Xhosa, Zulu", Language)
            Assert.assertEquals("USA", Country)
            Assert.assertEquals("Won 2 Oscars. Another 33 wins & 29 nominations.", Awards)
            Assert.assertEquals("https://images-na.ssl-images-amazon.com/images/M/MV5BYTYxNGMyZTYtMjE3MS00MzNjLWFjNmYtMDk3N2FmM2JiM2M1XkEyXkFqcGdeQXVyNjY5NDU4NzI@._V1_SX300.jpg", Poster)
            Assert.assertEquals("83", Metascore)
            Assert.assertEquals("8.5", imdbRating)
            Assert.assertEquals("740,390", imdbVotes)
            Assert.assertEquals("tt0110357", imdbID)
            Assert.assertEquals("movie", Type)
            Assert.assertEquals("07 Oct 2003", DVD)
            Assert.assertEquals("\$94,240,635", BoxOffice)
            Assert.assertEquals("Buena Vista", Production)
            Assert.assertEquals("http://disney.go.com/lionking/", Website)
            Assert.assertEquals("True", Response)

            Assert.assertEquals("Internet Movie Database", Ratings[0].Source)
            Assert.assertEquals("8.5/10", Ratings[0].Value)
            Assert.assertEquals("Rotten Tomatoes", Ratings[1].Source)
            Assert.assertEquals("92%", Ratings[1].Value)
            Assert.assertEquals("Metacritic", Ratings[2].Source)
            Assert.assertEquals("83/100", Ratings[2].Value)
        }
    }

}