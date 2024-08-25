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
import lombok.Builder;
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

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByRecipeId(Long recipeId) {
        List<CommentModel> comments = commentRepository.findByRecipeId(recipeId);

        return commentMapper.toDTOList(comments);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByUsername(String username) {
        List<CommentModel> comments = commentRepository.findByUsername(username);

        return commentMapper.toDTOList(comments);
    }

    public CommentDTO createComment(Long userId, Long recipeId, String text) {
        RecipeModel recipe = recipeRepositoryCustom.getRecipeModelById(recipeId);

        CommentModel commentModel = CommentModel.builder().recipe(recipe).text(text).user(new UserModel(userId)).build();

        CommentModel comment = commentRepository.save(commentModel);

        return commentMapper.toDTO(comment);
    }

    public void deleteComment(Long userId, Long commentId) {
        CommentModel comment = commentRepositoryCustom.getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous ne pouvez pas supprimer ce commentaire (pas le propriétaire)");
        }

        commentRepository.deleteById(commentId);
    }

    public CommentDTO updateComment(Long userId, Long commentId, String text) {
        CommentModel comment = commentRepositoryCustom.getCommentById(commentId);

        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous ne pouvez pas modifier ce commentaire (pas le propriétaire)");
        }

        comment.setText(text);

        return commentMapper.toDTO(commentRepository.save(comment));
    }
}
