package com.fractalwrench.json2kotlin.valid

import kotlin.Array
import kotlin.Number
import kotlin.String


data class WeatherExample(
        val clouds: Clouds,
        val cod: Number,
        val coord: Coord,
        val dt: Number,
        val id: Number,
        val main: Main,
        val name: String,
        val rain: Rain,
        val sys: Sys,
        val weather: Array<Weather>,
        val wind: Wind
)

data class Clouds(val all: Number)

data class Coord(val lat: Number, val lon: Number)

data class Main(
        val humidity: Number,
        val pressure: Number,
        val temp: Number,
        val temp_max: Number,
        val temp_min: Number
)

data class Rain(val _3h: Number)

data class Sys(
        val country: String,
        val sunrise: Number,
        val sunset: Number
)

data class Wind(val deg: Number, val speed: Number)

data class Weather(
        val description: String,
        val icon: String,
        val id: Number,
        val main: String
)
