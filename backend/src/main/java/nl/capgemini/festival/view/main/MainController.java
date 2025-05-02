package nl.capgemini.festival.view.main;

import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    Logger logger = LogManager.getLogger(MainController.class);
    @GetMapping("/main")
    public String addUserAttribute(HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getName().equals("anonymousUser")) {
            logger.warn("User has not logged in! sending to the login page.");
            return "redirect:/login";
        }
        Object principal = authentication.getPrincipal();
        session.setAttribute("user", principal);
        return "view/main";
    }

    @GetMapping("/")
    public String redirectToLoginPage(HttpSession session){
        return "redirect:/login";
    }
}
