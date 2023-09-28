package com.khan.tickets.demo.config;

import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.model.UserRole;
import com.khan.tickets.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        }catch(Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();

        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()) {
            User registeredUser = new User(UserRole.USER, name, email);

            userRepository.save(registeredUser);
        }

        return oidcUser;
    }
}
