package com.cookwe.domain.service;

import com.cookwe.data.model.CommentModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.CommentRepositoryCustom;
import com.cookwe.data.repository.interfaces.CommentRepository;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.domain.entity.CommentDTO;
import com.cookwe.domain.mapper.CommentMapper;
import com.cookwe.utils.errors.RestError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRepositoryCustom commentRepositoryCustom;
    private final RecipeRepositoryCustom recipeRepositoryCustom;
    private final CommentMapper commentMapper;

    public CommentService(CommentRepository commentRepository, CommentRepositoryCustom commentRepositoryCustom, RecipeRepositoryCustom recipeRepositoryCustom, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.commentRepositoryCustom = commentRepositoryCustom;
        this.recipeRepositoryCustom = recipeRepositoryCustom;
        this.commentMapper = commentMapper;
    }


    public List<CommentDTO> getCommentsByRecipeId(Long recipeId) {
        List<CommentModel> comments = commentRepository.findByRecipeId(recipeId);

        return commentMapper.toDTOList(comments);
    }

    public List<CommentDTO> getCommentsByUsername(String username) {
        List<CommentModel> comments = commentRepository.findByUsername(username);

        return commentMapper.toDTOList(comments);
    }

    public CommentDTO createComment(Long userId, Long recipeId, String text) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        CommentModel commentModel = new CommentModel().withUser(new UserModel(userId))
                .withRecipe(recipe)
                .withText(text);

        CommentModel comment = commentRepository.save(commentModel);

        return commentMapper.toDTO(comment);
    }

    public void deleteComment(Long userId, Long commentId) {
        CommentModel comment = commentRepositoryCustom.getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You can't delete this comment(not the owner)");
        }

        commentRepository.deleteById(commentId);
    }

    public CommentDTO updateComment(Long userId, Long commentId, String text) {
        CommentModel comment = commentRepositoryCustom.getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You can't update this comment(not the owner)");
        }

        comment.setText(text);

        return commentMapper.toDTO(commentRepository.save(comment));
    }
}
