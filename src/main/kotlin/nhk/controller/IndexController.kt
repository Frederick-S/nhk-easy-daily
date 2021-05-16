package nhk.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class IndexController : BaseController() {
    @GetMapping("/")
    fun index() = "index.html"
}
