package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.BugsnagErrorsExampleArray
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BugsnagGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "BugsnagErrors"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, Array<BugsnagErrorsExampleArray>::class.java)
        assertNotNull(gson)
        assertEquals(2, gson.size)

        with(gson[0]) {
            assertEquals("10000005", event_field_value)
            assertEquals(14, events.toInt())
            assertEquals("2017-06-28T20:27:45+00:00", first_seen)
            assertEquals("2017-06-28T20:34:44+00:00", last_seen)
            assertEquals(0.43324, proportion.toDouble(), 0.01)
            assertEquals("user1@example.com", fields.user_email)
            assertEquals("User One", fields.user_name)
        }

        with(gson[1]) {
            assertEquals("10000025", event_field_value)
            assertEquals(5, events.toInt())
            assertEquals("2017-06-27T20:16:43+00:00", first_seen)
            assertEquals("2017-07-03T15:40:18+00:00", last_seen)
            assertEquals(0.15007, proportion.toDouble(), 0.01)
            assertEquals("user2@example.com", fields.user_email)
            assertEquals("User Two", fields.user_name)
        }
    }

}