package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.CommentEntity;
import com.cookwe.domain.service.CommentService;
import com.cookwe.presentation.request.CreateCommentRequest;
import com.cookwe.presentation.response.CommentResponse;
import com.cookwe.utils.converters.CommentEntityToCommentResponse;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@Tag(name = "Comment", description = "Comment operations")
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

    @Operation(summary = "Get all comments of a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @GetMapping("/recipes/{recipeId}")
    public List<CommentResponse> getCommentsByRecipeId(@PathVariable Long recipeId) {
        List<CommentEntity> comments = commentService.getCommentsByRecipeId(recipeId);

        return CommentEntityToCommentResponse.convertList(comments);
    }

    @Operation(summary = "Create a comment for a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
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

    @Operation(summary = "Delete a comment")
    @Parameter(name = "commentId", description = "The id of the comment")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(getUserId(), commentId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a comment")
    @Parameter(name = "commentId", description = "The id of the comment")
    @PutMapping("/{commentId}")
    public CommentResponse updateComment(
            @PathVariable Long commentId,
            @RequestBody CreateCommentRequest request) {
        CommentEntity updatedComment = commentService.updateComment(getUserId(), commentId, request.text);

        return CommentEntityToCommentResponse.convert(updatedComment);
    }

    @Operation(summary = "Get all comments by user")
    @Parameter(name = "username", description = "The username of the user")
    @GetMapping("/users/{username}")
    public List<CommentResponse> getCommentsByUserId(@PathVariable String username) {
        List<CommentEntity> comments = commentService.getCommentsByUsername(username);

        return CommentEntityToCommentResponse.convertList(comments);
    }
}
