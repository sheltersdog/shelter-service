package com.sheltersdog.core.mail

import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.nio.charset.Charset

@Component
class SheltersdogMailSender @Autowired constructor(
    private val javaMailSender: JavaMailSender,
    private val resourceLoader: ResourceLoader,
) {

    suspend fun sendMail(
        type: SheltersdogMailType,
        email: String,
        params: Map<String, String>,
    ): Boolean {
        val template = this.convertEmailTemplate(
            filename = type.filename,
            params = params,
        )

        val message = javaMailSender.sheltersdogMimeMessage(
            email = email,
            template = template,
            subject = type.subject,
        )

        return kotlin.runCatching { javaMailSender.send(message) }.isSuccess
    }

    private fun convertEmailTemplate(
        filename: String,
        params: Map<String, String>,
    ): String {
        var template = resourceLoader.readEmailTemplate(filename)
            .ifBlank { return "" }

        params.forEach { (key, value) ->
            while (template.indexOf(key) != -1) {
                template = template.replaceFirst(key, value)
            }
        }
        return template
    }


}

fun JavaMailSender.sheltersdogMimeMessage(
    email: String,
    template: String,
    subject: String,
): MimeMessage {
    val message = this.createMimeMessage()
    message.setSubject(subject, "UTF-8")
    message.setText(template, Charset.defaultCharset().name(), "html")
    message.addRecipient(
        MimeMessage.RecipientType.TO,
        InternetAddress(email),
    )
    return message
}

fun ResourceLoader.readEmailTemplate(htmlFileName: String): String {
    val resource = this.getResource(htmlFileName)

    try {
        FileReader(resource.file).use { fileReader ->
            BufferedReader(fileReader).use { reader ->
                var line: String?
                val stringBuilder = StringBuilder()
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                return stringBuilder.toString()
            }
        }
    } catch (e: IOException) { /* do nothing */
    }
    return ""
}
