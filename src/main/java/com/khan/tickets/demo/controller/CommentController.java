package com.khan.tickets.demo.controller;

import com.khan.tickets.demo.model.TicketComment;
import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.repository.TicketCommentRepository;
import com.khan.tickets.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    private record CommentEdit(Long id, String changeTo) {}

    @PostMapping("/edit")
    public ResponseEntity<HttpStatus> edit(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                @RequestBody CommentEdit commentEdit) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        if(userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userOptional.get();

        Optional<TicketComment> commentOptional = commentRepository.findById(commentEdit.id());

        if(commentOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        TicketComment comment = commentOptional.get();

        if(comment.getOwner().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String changedTo = commentEdit.changeTo();

        if(changedTo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            comment.setContent(commentEdit.changeTo());

            commentRepository.save(comment);

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
