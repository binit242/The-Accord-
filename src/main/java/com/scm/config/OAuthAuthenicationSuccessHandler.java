package com.scm.config;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.util.List;
import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repsitories.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenicationSuccessHandler implements AuthenticationSuccessHandler {
    Logger logger = LoggerFactory.getLogger(OAuthAuthenicationSuccessHandler.class);
    @Autowired
    private UserRepo userRepo;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        logger.info("OAuthAuthenicationSuccessHandler");
        var oauth2AuthenicationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenicationToken.getAuthorizedClientRegistrationId();
        logger.info(authorizedClientRegistrationId);
        var oauthUser = (OAuth2User) authentication.getPrincipal();
        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info(key + " : " + value);
        });
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("dummy");
        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {

            user.setEmail(attributeAsString(oauthUser, "email"));
            user.setProfilePic(attributeAsString(oauthUser, "picture"));
            user.setName(firstNonBlank(attributeAsString(oauthUser, "name"), user.getEmail()));
            user.setProviderUserId(oauthUser.getName());
            user.setProvider(Providers.GOOGLE);
            user.setGender("This account is created using google.");
        } else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {

            String login = attributeAsString(oauthUser, "login");
            String email = firstNonBlank(attributeAsString(oauthUser, "email"), login + "@github.local");
            String picture = attributeAsString(oauthUser, "avatar_url");
            String name = firstNonBlank(attributeAsString(oauthUser, "name"), login, email);
            String providerUserId = oauthUser.getName();

            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviderUserId(providerUserId);
            user.setProvider(Providers.GITHUB);

            user.setGender("This account is created using github");
        }
        else if (authorizedClientRegistrationId.equalsIgnoreCase("linkedin")) {
        }
        else {
            logger.info("OAuthAuthenicationSuccessHandler: Unknown provider");
        }
        if (user.getProfilePic() == null || user.getProfilePic().isBlank()) {
            user.setProfilePic(user.getDisplayProfilePic());
        }
        User existingUser = userRepo.findByEmail(user.getEmail()).orElse(null);
        if (existingUser == null) {
            userRepo.save(user);
            System.out.println("user saved:" + user.getEmail());
        } else {
            if (existingUser.getName() == null || existingUser.getName().isBlank()) {
                existingUser.setName(user.getName());
            }
            if (isBlankOrDefaultProfilePic(existingUser.getProfilePic())) {
                existingUser.setProfilePic(user.getProfilePic());
            }
            existingUser.setProvider(user.getProvider());
            existingUser.setProviderUserId(user.getProviderUserId());
            existingUser.setEmailVerified(true);
            existingUser.setEnabled(true);
            if (existingUser.getRoleList() == null || existingUser.getRoleList().isEmpty()) {
                existingUser.setRoleList(List.of(AppConstants.ROLE_USER));
            }
            userRepo.save(existingUser);
        }
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }


    private boolean isBlankOrDefaultProfilePic(String profilePic) {
        if (profilePic == null || profilePic.isBlank()) {
            return true;
        }
        String trimmed = profilePic.trim();
        return "/images/accord.png".equals(trimmed)
                || "/images/default-avatar.svg".equals(trimmed)
                || "/images/default-male-avatar.svg".equals(trimmed)
                || "/images/default-female-avatar.svg".equals(trimmed);
    }
    private String attributeAsString(OAuth2User oauthUser, String key) {
        Object value = oauthUser.getAttribute(key);
        return value == null ? null : value.toString();
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }
}
