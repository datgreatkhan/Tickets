package com.khan.tickets.demo.controller;

import com.khan.tickets.demo.data.UserSummary;
import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.model.UserRole;
import com.khan.tickets.demo.repository.UserRepository;
import com.khan.tickets.demo.service.UserSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
/**
 * Controller that deals with anything directly with the user.
 * This contains mainly view based endpoints, such as user summary.
 */
public class UserController {

    private static final List<UserRole> ADMIN_OR_SUPPORT = Arrays.asList(UserRole.ADMIN, UserRole.SUPPORT);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserSummaryService userSummaryService;

    /* This is not an ideal way to deal with permissions/roles in Spring
       I just wrote this to save time. :)
    */
    private boolean isSupportOrAdminRole(OAuth2AuthenticatedPrincipal principal) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            UserRole role = user.getRole();

            return role == UserRole.ADMIN || role == UserRole.SUPPORT;
        }

        return false;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                               @PathVariable Long id) {
        if(!isSupportOrAdminRole(principal)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userQuery = userRepository.findById(id);

        if(userQuery.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(userQuery.get(), HttpStatus.OK);
        }
    }

    @GetMapping("/all/{pageNumber}/{sortBy}/{desc}")
    public ResponseEntity<Page<User>> allPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                               @PathVariable Integer pageNumber,
                                               @PathVariable String sortBy,
                                               @PathVariable boolean desc) {
        if(!isSupportOrAdminRole(principal)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<User> requestedPage = userRepository.findAll(PageRequest.of(pageNumber - 1, 15).withSort(Sort.by(direction, sortBy)));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/all/admin-or-support/{pageNumber}")
    public ResponseEntity<Page<User>> allAdminOrSupportPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                               @PathVariable Integer pageNumber) {
        if(!isSupportOrAdminRole(principal)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Page<User> requestedPage = userRepository.findByRoleIn(ADMIN_OR_SUPPORT, PageRequest.of(pageNumber - 1, 10).withSort(Sort.by(Sort.Direction.DESC, "id")));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<UserSummary> summary(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        User user;

        if(userOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }else{
            user = userOptional.get();
        }

        return new ResponseEntity<>(userSummaryService.getSummaryOf(user), HttpStatus.OK);
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<UserSummary> summaryOf(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                 @PathVariable Long id) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        if(userOptional.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        if(userOptional.get().getRole() != UserRole.ADMIN) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<User> targetUser = userRepository.findById(id);

        User user;

        if(targetUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }else{
            user = targetUser.get();
        }

        return new ResponseEntity<>(userSummaryService.getSummaryOf(user), HttpStatus.OK);
    }
}
