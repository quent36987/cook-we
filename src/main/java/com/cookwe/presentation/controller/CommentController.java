package com.cookwe.presentation.controller;

import com.cookwe.domain.entity.CommentDTO;
import com.cookwe.domain.service.CommentService;
import com.cookwe.presentation.request.CommentRequest;
import com.cookwe.utils.errors.RestError;
import com.cookwe.utils.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comment", description = "Comment operations")
public class CommentController {


    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }

        throw RestError.USER_NOT_FOUND.get();
    }

    @Operation(summary = "Get all comments of a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @GetMapping("/recipes/{recipeId}")
    public List<CommentDTO> getCommentsByRecipeId(@PathVariable Long recipeId) {
        return commentService.getCommentsByRecipeId(recipeId);
    }

    @Operation(summary = "Create a comment for a recipe")
    @Parameter(name = "recipeId", description = "The id of the recipe")
    @PostMapping("/recipes/{recipeId}")
    public CommentDTO createComment(
            @PathVariable Long recipeId,
            @RequestBody CommentRequest request) {
        if (request.text == null || request.text.isEmpty()) {
            throw RestError.MISSING_FIELD.get("text");
        }

        return commentService.createComment(getUserId(), recipeId, request.text);
    }

    @Operation(summary = "Delete a comment")
    @Parameter(name = "commentId", description = "The id of the comment")
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(getUserId(), commentId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a comment")
    @Parameter(name = "commentId", description = "The id of the comment")
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER')")
    public CommentDTO updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request) {
        return commentService.updateComment(getUserId(), commentId, request.text);
    }

    @Operation(summary = "Get all comments by user")
    @Parameter(name = "username", description = "The username of the user")
    @GetMapping("/users/{username}")
    public List<CommentDTO> getCommentsByUserId(@PathVariable String username) {
        return commentService.getCommentsByUsername(username);
    }
}
