package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.WeatherExample
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class WeatherGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "Weather"

    @Test
    override fun testGsonSerialisation() {
        val weather = Gson().fromJson(json, WeatherExample::class.java)
        Assert.assertNotNull(weather)

        Assert.assertEquals(139, weather.coord.lon.toInt())
        Assert.assertEquals(35, weather.coord.lat.toInt())

        Assert.assertEquals("JP", weather.sys.country)
        Assert.assertEquals(1369769524, weather.sys.sunrise.toInt())
        Assert.assertEquals(1369821049, weather.sys.sunset.toInt())

        Assert.assertEquals(1, weather.weather.size)
        Assert.assertEquals(804, weather.weather[0].id.toInt())
        Assert.assertEquals("clouds", weather.weather[0].main)
        Assert.assertEquals("overcast clouds", weather.weather[0].description)
        Assert.assertEquals("04n", weather.weather[0].icon)

        Assert.assertEquals(289.5, weather.main.temp.toDouble(), 0.01)
        Assert.assertEquals(89, weather.main.humidity.toInt())
        Assert.assertEquals(1013, weather.main.pressure.toInt())
        Assert.assertEquals(287.04, weather.main.temp_min.toDouble(), 0.01)
        Assert.assertEquals(292.04, weather.main.temp_max.toDouble(), 0.01)

        Assert.assertEquals(7.31, weather.wind.speed.toDouble(), 0.01)
        Assert.assertEquals(187.002, weather.wind.deg.toDouble(), 0.01)

        Assert.assertEquals(5, weather.rain._3h.toInt())

        Assert.assertEquals(92, weather.clouds.all.toInt())

        Assert.assertEquals(1369824698, weather.dt.toInt())
        Assert.assertEquals(1851632, weather.id.toInt())
        Assert.assertEquals("Shuzenji", weather.name)
        Assert.assertEquals(200, weather.cod.toInt())
    }

}