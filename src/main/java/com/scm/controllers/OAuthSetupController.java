package com.scm.controllers;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ConditionalOnMissingBean(ClientRegistrationRepository.class)
public class OAuthSetupController {

    @GetMapping("/oauth2/authorization/{provider}")
    public String oauthNotConfigured(@PathVariable String provider, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(
                "oauthMessage",
                "Add real " + provider + " OAuth credentials in LOCAL_SETUP.md, then restart the app.");
        return "redirect:/login";
    }
}
