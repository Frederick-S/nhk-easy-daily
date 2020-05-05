package nhk.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "html2pdf")
open class Html2PdfConfig {
    var host = ""
}