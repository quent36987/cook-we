package com.cookwe.domain.service;

import com.cookwe.data.model.CommentModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.CommentRepository;
import com.cookwe.data.repository.RecipeRepository;
import com.cookwe.domain.entity.CommentEntity;
import com.cookwe.utils.converters.CommentModelToCommentEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Data
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Transactional
    public List<CommentEntity> getCommentsByRecipeId(Long recipeId) {
        Iterable<CommentModel> comments = commentRepository.findByRecipeId(recipeId);

        return CommentModelToCommentEntity.convertList(comments);
    }

    @Transactional
    public CommentModel getCommentById(Long commentId) {
        Optional<CommentModel> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            throw RestError.COMMENT_NOT_FOUND.get(commentId);
        }

        return comment.get();
    }

    @Transactional
    public CommentEntity createComment(Long userId, Long recipeId, String text) {
        Optional<RecipeModel> recipe = recipeRepository.findById(recipeId);

        if (recipe.isEmpty()) {
            throw RestError.RECIPE_NOT_FOUND.get(recipeId);
        }

        CommentModel commentModel = new CommentModel().withUser(new UserModel(userId))
                .withRecipe(recipe.get())
                .withText(text)
                .withCreatedAt(LocalDateTime.now());

        return CommentModelToCommentEntity.convert(commentRepository.save(commentModel));
    }

    @Transactional
    public void deleteComment(Long UserId, Long commentId) {
        CommentModel comment = getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), UserId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You can't delete this comment(not the owner)");
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentEntity updateComment(Long userId, Long commentId, String text) {
        CommentModel comment = getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You can't update this comment(not the owner)");
        }

        comment.setText(text);

        return CommentModelToCommentEntity.convert(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentEntity> getCommentsByUsername(String username) {
        Iterable<CommentModel> comments = commentRepository.findByUsername(username);

        return CommentModelToCommentEntity.convertList(comments);
    }
}
