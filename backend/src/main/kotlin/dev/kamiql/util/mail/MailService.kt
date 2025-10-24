package dev.kamiql.util.mail

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import jakarta.mail.*
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import java.util.*

object MailService {
    private val parser = Parser.builder().build()
    private val renderer = HtmlRenderer.builder().build()

    private val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
    }

    private val session: Session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(
                "kilian.aqua08@gmail.com", //System.getenv("SMTP_USER"),
                "payu pdxg zjlt ewik" //System.getenv("SMTP_PASS")
            )
        }
    })

    fun send(template: String, to: String, variables: Map<String, String>) {
        val templateStream = this::class.java.classLoader
            .getResourceAsStream("mail/templates/$template")
            ?: error("Template not found: $template")

        val mdContent = templateStream.bufferedReader().use { it.readText() }
        val replaced = variables.entries.fold(mdContent) { acc, (key, value) ->
            acc.replace("{{${key}}}", value)
        }

        val document = parser.parse(replaced)
        val htmlContent = renderer.render(document)

        val msg = MimeMessage(session).apply {
            setFrom(InternetAddress("kilian.aqua08@gmail.com"))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
            subject = variables["subject"] ?: "No Subject"

            val multipart = MimeMultipart("alternative").apply {
                addBodyPart(MimeBodyPart().apply {
                    setText(replaced, "utf-8")
                })
                addBodyPart(MimeBodyPart().apply {
                    setContent(htmlContent, "text/html; charset=utf-8")
                })
            }

            setContent(multipart)
        }

        Transport.send(msg)
    }
}
