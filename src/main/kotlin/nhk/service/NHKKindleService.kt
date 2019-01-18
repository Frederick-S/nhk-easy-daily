package nhk.service

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.RawMessage
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest
import nhk.domain.NHKNews
import org.jsoup.Jsoup
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
class NHKKindleService {
    private val logger: Logger = LoggerFactory.getLogger(NHKKindleService::class.java)

    fun sendToKindle(nhkNews: NHKNews, mailTo: String) {
        val textPart = MimeBodyPart()
        textPart.setContent("NHK Easy", "text/plain; charset=UTF-8")

        val attachment = MimeBodyPart()
        val byteArrayDataSource = ByteArrayDataSource(getAttachmentContent(nhkNews), "text/html")
        attachment.dataHandler = DataHandler(byteArrayDataSource)
        attachment.fileName = "${nhkNews.title}.html"

        val mixedPart = MimeMultipart("mixed")
        mixedPart.addBodyPart(textPart)
        mixedPart.addBodyPart(attachment)

        val session = Session.getDefaultInstance(Properties())
        val message = MimeMessage(session)
        message.setSubject("NHK", "UTF-8")
        message.setFrom(InternetAddress("mao_xiaodan@hotmail.com"))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo))
        message.setContent(mixedPart)

        try {
            val client = AmazonSimpleEmailServiceClientBuilder.standard()
                    .withRegion(Regions.US_WEST_2)
                    .build()

            val outputStream = ByteArrayOutputStream()
            message.writeTo(outputStream)

            val rawMessage = RawMessage(ByteBuffer.wrap(outputStream.toByteArray()))
            val rawEmailRequest = SendRawEmailRequest(rawMessage)

            client.sendRawEmail(rawEmailRequest)

            logger.info("Send to kindle ok")
        } catch (e: Exception) {
            logger.error("Send to kindle error", e)
        }
    }

    private fun getAttachmentContent(nhkNews: NHKNews): String {
        val document = Jsoup.parse(nhkNews.body)

        return document.text()
    }
}