package nhk.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.RawMessage
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest
import nhk.entity.News
import nhk.entity.Word
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.Properties
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

@Service
class KindleService {
    private val logger: Logger = LoggerFactory.getLogger(KindleService::class.java)

    fun sendToKindle(news: News, mailFrom: String, mailTo: String) {
        val message = buildMessage(news, mailFrom, mailTo)

        try {
            val client = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(Regions.US_WEST_2)
                    .build()

            val outputStream = ByteArrayOutputStream()
            message.writeTo(outputStream)

            val rawMessage = RawMessage(ByteBuffer.wrap(outputStream.toByteArray()))
            val rawEmailRequest = SendRawEmailRequest(rawMessage)
            val result = client.sendRawEmail(rawEmailRequest)
            val httpStatusCode = result.sdkHttpMetadata.httpStatusCode
            val requestId = result.sdkResponseMetadata.requestId

            when (httpStatusCode) {
                HttpStatus.OK.value() -> logger.info("Send to kindle ok, mailTo={}, requestId={}", mailTo, requestId)
                else -> logger.error("Send to kindle error, mailTo={}, requestId={}", mailTo, requestId)
            }
        } catch (e: Exception) {
            logger.error("Send to kindle error, mailTo={}", mailTo, e)
        }
    }

    private fun buildMessage(news: News, mailFrom: String, mailTo: String): MimeMessage {
        val attachment = MimeBodyPart()
        val byteArrayDataSource = ByteArrayDataSource(getAttachmentContent(news), "text/html")
        attachment.dataHandler = DataHandler(byteArrayDataSource)
        attachment.fileName = "${news.title}.html"

        val mixedPart = MimeMultipart("mixed")
        mixedPart.addBodyPart(attachment)

        val session = Session.getDefaultInstance(Properties())
        val message = MimeMessage(session)
        message.setSubject(news.title, Charsets.UTF_8.displayName())
        message.setFrom(InternetAddress(mailFrom))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo))
        message.setContent(mixedPart)

        return message
    }

    private fun getAttachmentContent(news: News): String {
        val document = Jsoup.parse(news.body)
        val paragraphs = document.select("p")

        paragraphs.forEach { p ->
            p.select("ruby").tagName("span")
            p.select("rt").tagName("sup")
        }

        val newsHtml = paragraphs.joinToString(separator = "")
        val wordsHtml = getWordsHtml(news.words)

        return getMailHtml(news.title, newsHtml, wordsHtml)
    }

    private fun getWordsHtml(words: Set<Word>): String {
        return words.joinToString(separator = "") { word ->
            val definitions = word.definitions
                    .joinToString("") { definition ->
                        val root = Jsoup.parse(definition.definitionWithRuby)

                        root.select("ruby").tagName("span")
                        root.select("rt").tagName("sup")

                        "<li>${root.outerHtml()}</li>"
                    }

            """
                <h3>${word.name}</h3>
                <ol>
                    $definitions
                </ol>
            """.trimIndent()
        }
    }

    private fun getMailHtml(title: String, news: String, words: String): String {
        return """
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                        <title>$title</title>
                        <style>
                            h1, h2 {
                                text-align: center;
                            }
                        </style>
                    </head>
                    <body>
                        <h1>$title</h1>
                        $news
                        <h2>単語</h2>
                        $words
                    </body>
                </html>
            """.trimIndent()
    }
}