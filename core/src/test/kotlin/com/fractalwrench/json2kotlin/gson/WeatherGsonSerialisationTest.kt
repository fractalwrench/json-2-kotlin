package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.WeatherExample
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class WeatherGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "Weather"

    @Test
    override fun testGsonSerialisation() {
        val weather = Gson().fromJson(json, WeatherExample::class.java)
        assertNotNull(weather)

        assertEquals(1, weather.weather.size)
        assertEquals(804, weather.weather[0].id.toInt())
        assertEquals("clouds", weather.weather[0].main)
        assertEquals("overcast clouds", weather.weather[0].description)
        assertEquals("04n", weather.weather[0].icon)

        with(weather) {
            assertEquals(139, coord.lon.toInt())
            assertEquals(35, coord.lat.toInt())

            assertEquals("JP", sys.country)
            assertEquals(1369769524, sys.sunrise.toInt())
            assertEquals(1369821049, sys.sunset.toInt())

            assertEquals(289.5, main.temp.toDouble(), 0.01)
            assertEquals(89, main.humidity.toInt())
            assertEquals(1013, main.pressure.toInt())
            assertEquals(287.04, main.temp_min.toDouble(), 0.01)
            assertEquals(292.04, main.temp_max.toDouble(), 0.01)

            assertEquals(7.31, wind.speed.toDouble(), 0.01)
            assertEquals(187.002, wind.deg.toDouble(), 0.01)

            assertEquals(5, rain._3h.toInt())

            assertEquals(92, clouds.all.toInt())

            assertEquals(1369824698, dt.toInt())
            assertEquals(1851632, id.toInt())
            assertEquals("Shuzenji", name)
            assertEquals(200, cod.toInt())
        }
    }

}