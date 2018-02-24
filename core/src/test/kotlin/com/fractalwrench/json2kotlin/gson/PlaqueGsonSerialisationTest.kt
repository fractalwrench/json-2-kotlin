package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.GithubProjectExample
import com.fractalwrench.json2kotlin.valid.PlaqueExample
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class PlaqueGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "Plaque"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, PlaqueExample::class.java)
        Assert.assertNotNull(gson)
        Assert.assertNotNull(gson.meta)
        Assert.assertNotNull(gson.data)

        with(gson.meta.view) { // only serialise some, otherwise we'll be here all day...
            Assert.assertEquals("bsf5-k2w2", id)
            Assert.assertEquals("Open Plaques", name)
            Assert.assertEquals("OpenPlaques", attribution)
            Assert.assertEquals("http://openplaques.org/places/gb/areas/bath", attributionLink)
            Assert.assertEquals(0, averageRating.toInt())
            Assert.assertEquals("Heritage", category)
            Assert.assertEquals(1412965968, createdAt.toLong())
            Assert.assertEquals("A list of the commemorative plaques found around Bath, as recorded in the Open Plaques database. The dataset contains information on the location, title, inscription, and subjects for each plaque, along with a pointer to a JSON document that provides more detail about each plaque and its subjects.", description)
            Assert.assertEquals("table", displayType)
            Assert.assertEquals(76, downloadCount.toInt())
            Assert.assertEquals(false, hideFromCatalog)
            Assert.assertEquals(false, hideFromDataJson)
            Assert.assertEquals("fileId:1Bbr1-uyvhayzyY7QJjnXt1D7wOeYinuleYg8s9ba9Q", iconUrl)
            Assert.assertEquals(1412966520, indexUpdatedAt.toLong())
            Assert.assertEquals("PUBLIC_DOMAIN", licenseId)
            Assert.assertEquals(false, newBackend)
            Assert.assertEquals(0, numberOfComments.toInt())
            Assert.assertEquals(17070, oid.toInt())
            Assert.assertEquals("official", provenance)
            Assert.assertEquals(false, publicationAppendEnabled)
            Assert.assertEquals(1412966522, publicationDate.toLong())
            Assert.assertEquals(12018, publicationGroup.toInt())
            Assert.assertEquals("published", publicationStage)
            Assert.assertEquals("", rowClass)
            Assert.assertEquals(193351, rowIdentifierColumnId.toInt())
            Assert.assertEquals(1412965973, rowsUpdatedAt.toLong())
            Assert.assertEquals("22aw-9kr4", rowsUpdatedBy)
            Assert.assertEquals(12018, tableId.toInt())
            Assert.assertEquals(0, totalTimesRated.toInt())
            Assert.assertEquals(331, viewCount.toInt())
            Assert.assertEquals(1412966522, viewLastModified.toLong())
            Assert.assertEquals("tabular", viewType)

            Assert.assertEquals(1, grants.size)
            Assert.assertEquals(false, grants[0].inherited)
            Assert.assertEquals("viewer", grants[0].type)
            Assert.assertEquals("public", grants[0].flags[0])

            Assert.assertEquals("Public Domain", license.name)

            Assert.assertEquals("0", metadata.rdfSubject)
            Assert.assertEquals("", metadata.rdfClass)
            Assert.assertEquals("id", metadata.rowIdentifier)
            Assert.assertEquals("On change", metadata.custom_fields.Publication.Update_Frequency)
            Assert.assertEquals("http://openplaques.org/about/data", metadata.custom_fields.Additional_Licence_Detail.Re_user_Guidelines)
            Assert.assertEquals("Open Data Commons Public Domain Dedication (PDDL)", metadata.custom_fields.Additional_Licence_Detail.Additional_Licence_Information)
            Assert.assertEquals("http://opendatacommons.org/licenses/pddl/", metadata.custom_fields.Additional_Licence_Detail.Licence_URL)
            Assert.assertEquals(true, metadata.renderTypeConfig.visible.table)

            Assert.assertEquals(3, metadata.availableDisplayTypes.size)
            Assert.assertEquals("fatrow", metadata.availableDisplayTypes[1])

        }
    }
}