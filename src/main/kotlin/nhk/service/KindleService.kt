package nhk.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.RawMessage
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest
import nhk.entity.News
import nhk.entity.Word
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
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
        val okHttpClient = OkHttpClient()
        val requestBody = FormBody.Builder()
                .add("html", getMailHtml(news))
                .add("format", "A6")
                .build()
        val request = Request.Builder()
                .url("https://html2pdf101.azurewebsites.net/pdfs")
                .post(requestBody)
                .build()
        val call = okHttpClient.newCall(request)
        val response = call.execute()

        if (response.code != 201) {
            logger.error("Failed to generate pdf from html, statusCode={}, body={}", response.code, response.body?.string())

            throw RuntimeException("Service unavailable")
        }

        val attachment = MimeBodyPart()
        val byteArrayDataSource = ByteArrayDataSource(response.body?.bytes(), "application/pdf")
        attachment.dataHandler = DataHandler(byteArrayDataSource)
        attachment.fileName = "${news.title}.pdf"

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

    private fun getWordsHtml(words: Set<Word>): String {
        return words.joinToString(separator = "") { word ->
            val definitions = word.definitions
                    .joinToString("") { definition ->
                        "<li>${definition.definitionWithRuby}</li>"
                    }

            """
                <h3>${word.name}</h3>
                <ol>
                    $definitions
                </ol>
            """.trimIndent()
        }
    }

    private fun getMailHtml(news: News): String {
        val image = when (StringUtils.isEmpty(news.imageUrl)) {
            true -> ""
            false -> "<img class=\"news-image\" src=\"${news.imageUrl}\" />"
        }
        val wordsHtml = getWordsHtml(news.words)

        return """
                <!DOCTYPE html>
                <html>
                    <head>
                        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
                        <title>${news.title}</title>
                        <style>
                            h1, h2 {
                                text-align: center;
                            }
                            .news-image {
                                display: block;
                                margin: 0 auto;
                                max-width: 100%;
                            }
                            @media print {
                                p, li {
                                    page-break-inside: avoid;
                                }
                            }
                            @page {
                                margin: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        $image
                        <h1>${news.title}</h1>
                        ${news.body}
                        <h2>単語</h2>
                        $wordsHtml
                    </body>
                </html>
            """.trimIndent()
    }
}