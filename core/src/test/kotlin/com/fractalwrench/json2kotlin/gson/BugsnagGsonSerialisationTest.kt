package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.BugsnagErrorsExampleArray
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class BugsnagGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "BugsnagErrors"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, Array<BugsnagErrorsExampleArray>::class.java)
        Assert.assertNotNull(gson)
        Assert.assertEquals(2, gson.size)

        with(gson[0]) {
            Assert.assertEquals("10000005", event_field_value)
            Assert.assertEquals(14, events.toInt())
            Assert.assertEquals("2017-06-28T20:27:45+00:00", first_seen)
            Assert.assertEquals("2017-06-28T20:34:44+00:00", last_seen)
            Assert.assertEquals(0.43324, proportion.toDouble(), 0.01)
            Assert.assertEquals("user1@example.com", fields.user_email)
            Assert.assertEquals("User One", fields.user_name)
        }

        with(gson[1]) {
            Assert.assertEquals("10000025", event_field_value)
            Assert.assertEquals(5, events.toInt())
            Assert.assertEquals("2017-06-27T20:16:43+00:00", first_seen)
            Assert.assertEquals("2017-07-03T15:40:18+00:00", last_seen)
            Assert.assertEquals(0.15007, proportion.toDouble(), 0.01)
            Assert.assertEquals("user2@example.com", fields.user_email)
            Assert.assertEquals("User Two", fields.user_name)
        }
    }

}