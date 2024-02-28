package com.cookwe.domain.service;

import com.cookwe.data.model.CommentModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.CommentRepositoryCustom;
import com.cookwe.data.repository.interfaces.CommentRepository;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.domain.entity.CommentEntity;
import com.cookwe.utils.converters.CommentModelToCommentEntity;
import com.cookwe.utils.errors.RestError;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Data
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentRepositoryCustom commentRepositoryCustom;

    @Autowired
    private RecipeRepositoryCustom recipeRepositoryCustom;

    @Transactional
    public List<CommentEntity> getCommentsByRecipeId(Long recipeId) {
        Iterable<CommentModel> comments = commentRepository.findByRecipeId(recipeId);

        return CommentModelToCommentEntity.convertList(comments);
    }

    @Transactional
    public List<CommentEntity> getCommentsByUsername(String username) {
        Iterable<CommentModel> comments = commentRepository.findByUsername(username);

        return CommentModelToCommentEntity.convertList(comments);
    }

    @Transactional
    public CommentEntity createComment(Long userId, Long recipeId, String text) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        CommentModel commentModel = new CommentModel().withUser(new UserModel(userId))
                .withRecipe(recipe)
                .withText(text);

        return CommentModelToCommentEntity.convert(commentRepository.save(commentModel));
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        CommentModel comment = commentRepositoryCustom.getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You can't delete this comment(not the owner)");
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentEntity updateComment(Long userId, Long commentId, String text) {
        CommentModel comment = commentRepositoryCustom.getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You can't update this comment(not the owner)");
        }

        comment.setText(text);

        return CommentModelToCommentEntity.convert(commentRepository.save(comment));
    }
}
