/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.commons.notifications.model

import com.fasterxml.jackson.core.JsonParseException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opensearch.commons.utils.createObjectFromJsonString
import org.opensearch.commons.utils.getJsonString
import org.opensearch.commons.utils.recreateObject

internal class EmailGroupTests {

    @Test
    fun `EmailGroup serialize and deserialize transport object should be equal`() {
        val sampleEmailGroup = EmailGroup(
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            )
        )
        val recreatedObject = recreateObject(sampleEmailGroup) { EmailGroup(it) }
        assertEquals(sampleEmailGroup, recreatedObject)
    }

    @Test
    fun `EmailGroup serialize and deserialize using json object should be equal`() {
        val sampleEmailGroup = EmailGroup(
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            )
        )
        val jsonString = getJsonString(sampleEmailGroup)
        val recreatedObject = createObjectFromJsonString(jsonString) { EmailGroup.parse(it) }
        assertEquals(sampleEmailGroup, recreatedObject)
    }

    @Test
    fun `EmailGroup should deserialize json object using parser`() {
        val sampleEmailGroup = EmailGroup(
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            )
        )
        val jsonString = """
            {
                "recipient_list":[
                    {"recipient":"${sampleEmailGroup.recipients[0].recipient}"},
                    {"recipient":"${sampleEmailGroup.recipients[1].recipient}"}
                ]
             }"
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { EmailGroup.parse(it) }
        assertEquals(sampleEmailGroup, recreatedObject)
    }

    @Test
    fun `EmailGroup should throw exception when invalid json object is passed`() {
        val jsonString = "sample message"
        assertThrows<JsonParseException> {
            createObjectFromJsonString(jsonString) { EmailGroup.parse(it) }
        }
    }

    @Test
    fun `EmailGroup should throw exception when recipients is replaced with recipients2 in json object`() {
        val sampleEmailGroup = EmailGroup(
            listOf(
                EmailRecipient("email1@email.com"),
                EmailRecipient("email2@email.com")
            )
        )
        val jsonString = """
            {
                "recipient_list2":[
                    {"recipient":"${sampleEmailGroup.recipients[0]}"},
                    {"recipient":"${sampleEmailGroup.recipients[1]}"}
                ]
             }"
        """.trimIndent()
        assertThrows<IllegalArgumentException> {
            createObjectFromJsonString(jsonString) { EmailGroup.parse(it) }
        }
    }

    @Test
    fun `EmailGroup should safely ignore extra field in json object`() {
        val sampleEmailGroup = EmailGroup(listOf(EmailRecipient("email1@email.com")))
        val jsonString = """
            {
                "recipient_list":[
                    {"recipient":"${sampleEmailGroup.recipients[0].recipient}"}
                ],
                "extra_field_1":["extra", "value"],
                "extra_field_2":{"extra":"value"},
                "extra_field_3":"extra value 3"
             }"
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { EmailGroup.parse(it) }
        assertEquals(sampleEmailGroup, recreatedObject)
    }
}
