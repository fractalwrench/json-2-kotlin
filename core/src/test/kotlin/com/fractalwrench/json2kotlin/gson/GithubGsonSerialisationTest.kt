package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.GithubProjectExample
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GithubGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "GithubProject"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, GithubProjectExample::class.java)
        assertNotNull(gson)

        with (gson) {
            assertEquals("https://api.github.com/repos/api-playground/projects-test", owner_url)
            assertEquals("https://api.github.com/projects/1002604", url)
            assertEquals("https://github.com/api-playground/projects-test/projects/12", html_url)
            assertEquals("https://api.github.com/projects/1002604/columns", columns_url)
            assertEquals(1002604, id.toInt())
            assertEquals("Projects Documentation", name)
            assertEquals("Developer documentation project for the developer site.", body)
            assertEquals(1, number.toInt())
            assertEquals("open", state)
            assertEquals("2011-04-10T20:09:31Z", created_at)
            assertEquals("2014-03-03T18:58:10Z", updated_at)

            assertEquals("octocat", creator.login)
            assertEquals(1, creator.id.toInt())
            assertEquals("https://github.com/images/error/octocat_happy.gif", creator.avatar_url)
            assertEquals("", creator.gravatar_id)
            assertEquals("https://api.github.com/users/octocat", creator.url)
            assertEquals("https://github.com/octocat", creator.html_url)
            assertEquals("https://api.github.com/users/octocat/followers", creator.followers_url)
            assertEquals("https://api.github.com/users/octocat/following{/other_user}", creator.following_url)
            assertEquals("https://api.github.com/users/octocat/gists{/gist_id}", creator.gists_url)
            assertEquals("https://api.github.com/users/octocat/starred{/owner}{/repo}", creator.starred_url)
            assertEquals("https://api.github.com/users/octocat/subscriptions", creator.subscriptions_url)
            assertEquals("https://api.github.com/users/octocat/orgs", creator.organizations_url)
            assertEquals("https://api.github.com/users/octocat/repos", creator.repos_url)
            assertEquals("https://api.github.com/users/octocat/events{/privacy}", creator.events_url)
            assertEquals("https://api.github.com/users/octocat/received_events", creator.received_events_url)
            assertEquals("User", creator.type)
            assertEquals(false, creator.site_admin)
        }
    }

}