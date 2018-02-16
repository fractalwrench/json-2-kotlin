package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.GithubProjectExample
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class GithubGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "GithubProject"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, GithubProjectExample::class.java)
        Assert.assertNotNull(gson)

        Assert.assertEquals("https://api.github.com/repos/api-playground/projects-test", gson.owner_url)
        Assert.assertEquals("https://api.github.com/projects/1002604", gson.url)
        Assert.assertEquals("https://github.com/api-playground/projects-test/projects/12", gson.html_url)
        Assert.assertEquals("https://api.github.com/projects/1002604/columns", gson.columns_url)
        Assert.assertEquals(1002604, gson.id.toInt())
        Assert.assertEquals("Projects Documentation", gson.name)
        Assert.assertEquals("Developer documentation project for the developer site.", gson.body)
        Assert.assertEquals(1, gson.number.toInt())
        Assert.assertEquals("open", gson.state)
        Assert.assertEquals("2011-04-10T20:09:31Z", gson.created_at)
        Assert.assertEquals("2014-03-03T18:58:10Z", gson.updated_at)

        Assert.assertEquals("octocat", gson.creator.login)
        Assert.assertEquals(1, gson.creator.id.toInt())
        Assert.assertEquals("https://github.com/images/error/octocat_happy.gif", gson.creator.avatar_url)
        Assert.assertEquals("", gson.creator.gravatar_id)
        Assert.assertEquals("https://api.github.com/users/octocat", gson.creator.url)
        Assert.assertEquals("https://github.com/octocat", gson.creator.html_url)
        Assert.assertEquals("https://api.github.com/users/octocat/followers", gson.creator.followers_url)
        Assert.assertEquals("https://api.github.com/users/octocat/following{/other_user}", gson.creator.following_url)
        Assert.assertEquals("https://api.github.com/users/octocat/gists{/gist_id}", gson.creator.gists_url)
        Assert.assertEquals("https://api.github.com/users/octocat/starred{/owner}{/repo}", gson.creator.starred_url)
        Assert.assertEquals("https://api.github.com/users/octocat/subscriptions", gson.creator.subscriptions_url)
        Assert.assertEquals("https://api.github.com/users/octocat/orgs", gson.creator.organizations_url)
        Assert.assertEquals("https://api.github.com/users/octocat/repos", gson.creator.repos_url)
        Assert.assertEquals("https://api.github.com/users/octocat/events{/privacy}", gson.creator.events_url)
        Assert.assertEquals("https://api.github.com/users/octocat/received_events", gson.creator.received_events_url)
        Assert.assertEquals("User", gson.creator.type)
        Assert.assertEquals(false, gson.creator.site_admin)
    }

}