package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.CommentEntity;
import com.cookwe.domain.service.CommentService;
import com.cookwe.presentation.request.CreateCommentRequest;
import com.cookwe.presentation.response.CommentResponse;
import com.cookwe.utils.converters.CommentEntityToCommentResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    /**
     * Get all comments
     *
     * @param recipeId - The id of the recipe
     * @return - An List object of Comment full filled
     */
    @GetMapping("/recipes/{recipeId}")
    public List<CommentResponse> getCommentsByRecipeId(@PathVariable Long recipeId) {
        List<CommentEntity> comments = commentService.getCommentsByRecipeId(recipeId);

        return CommentEntityToCommentResponse.convertList(comments);
    }

    /**
     * Create a new comment
     *
     * @param recipeId - The id of the recipe
     * @param request  - A CreateCommentRequest object
     * @return - A Comment object
     */
    @PostMapping("/recipes/{recipeId}")
    public CommentResponse createComment(
            @PathVariable Long recipeId,
            @RequestBody CreateCommentRequest request) {
        if (request.text == null || request.text.isEmpty()) {
            throw RestError.MISSING_FIELD.get("text");
        }

        CommentEntity createdComment = commentService.createComment(getUserId(), recipeId, request.text);

        return CommentEntityToCommentResponse.convert(createdComment);
    }

    /**
     * Delete a comment
     *
     * @param commentId - The id of the comment
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(getUserId(), commentId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Update a comment
     *
     * @param commentId - The id of the comment
     * @param request   - A UpdateCommentRequest object
     * @return - A Comment object
     */
    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentRequest request) {
        CommentEntity updatedComment = commentService.updateComment(getUserId(), commentId, request.text);

        return CommentEntityToCommentResponse.convert(updatedComment);
    }

    /**
     * Get all comments by user id
     *
     * @param username - The username of the user
     * @return - An List object of Comment full filled
     */
    @GetMapping("/users/{username}")
    public List<CommentResponse> getCommentsByUserId(@PathVariable String username) {
        List<CommentEntity> comments = commentService.getCommentsByUsername(username);

        return CommentEntityToCommentResponse.convertList(comments);
    }

//  TODO (injection sql ;(  // Recherche de mot dans les commentaires
//    @GetMapping("/search")
//    public ResponseEntity<List<CommentResponse>> searchComments(@RequestParam String keyword) {
//        List<CommentResponse> comments = commentService.searchComments(keyword);
//        return ResponseEntity.ok(comments);
//    }
}
