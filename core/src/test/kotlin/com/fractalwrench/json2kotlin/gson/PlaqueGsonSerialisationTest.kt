package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.PlaqueExample
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class PlaqueGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "Plaque"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, PlaqueExample::class.java)
        assertNotNull(gson)
        assertNotNull(gson.meta)
        assertNotNull(gson.data)

        with(gson.meta.view) { // only serialise some, otherwise we'll be here all day...
            assertEquals("bsf5-k2w2", id)
            assertEquals("Open Plaques", name)
            assertEquals("OpenPlaques", attribution)
            assertEquals("http://openplaques.org/places/gb/areas/bath", attributionLink)
            assertEquals(0, averageRating.toInt())
            assertEquals("Heritage", category)
            assertEquals(1412965968, createdAt.toLong())
            assertEquals("A list of the commemorative plaques found around Bath, as recorded in the Open Plaques database. The dataset contains information on the location, title, inscription, and subjects for each plaque, along with a pointer to a JSON document that provides more detail about each plaque and its subjects.", description)
            assertEquals("table", displayType)
            assertEquals(76, downloadCount.toInt())
            assertEquals(false, hideFromCatalog)
            assertEquals(false, hideFromDataJson)
            assertEquals("fileId:1Bbr1-uyvhayzyY7QJjnXt1D7wOeYinuleYg8s9ba9Q", iconUrl)
            assertEquals(1412966520, indexUpdatedAt.toLong())
            assertEquals("PUBLIC_DOMAIN", licenseId)
            assertEquals(false, newBackend)
            assertEquals(0, numberOfComments.toInt())
            assertEquals(17070, oid.toInt())
            assertEquals("official", provenance)
            assertEquals(false, publicationAppendEnabled)
            assertEquals(1412966522, publicationDate.toLong())
            assertEquals(12018, publicationGroup.toInt())
            assertEquals("published", publicationStage)
            assertEquals("", rowClass)
            assertEquals(193351, rowIdentifierColumnId.toInt())
            assertEquals(1412965973, rowsUpdatedAt.toLong())
            assertEquals("22aw-9kr4", rowsUpdatedBy)
            assertEquals(12018, tableId.toInt())
            assertEquals(0, totalTimesRated.toInt())
            assertEquals(331, viewCount.toInt())
            assertEquals(1412966522, viewLastModified.toLong())
            assertEquals("tabular", viewType)

            assertEquals(1, grants.size)
            assertEquals(false, grants[0].inherited)
            assertEquals("viewer", grants[0].type)
            assertEquals("public", grants[0].flags[0])

            assertEquals("Public Domain", license.name)

            with (metadata) {
                assertEquals("0", rdfSubject)
                assertEquals("", rdfClass)
                assertEquals("id", rowIdentifier)
                assertEquals(true, renderTypeConfig.visible.table)

                assertEquals(3, availableDisplayTypes.size)
                assertEquals("fatrow", availableDisplayTypes[1])

                with (custom_fields) {
                    assertEquals("On change", Publication.Update_Frequency)

                    with (Additional_Licence_Detail) {
                        assertEquals("http://openplaques.org/about/data", Re_user_Guidelines)
                        assertEquals("Open Data Commons Public Domain Dedication (PDDL)", Additional_Licence_Information)
                        assertEquals("http://opendatacommons.org/licenses/pddl/", Licence_URL)
                    }
                }
            }


        }
    }
}