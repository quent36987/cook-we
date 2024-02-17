package com.cookwe.data.repository;

import com.cookwe.data.model.CommentModel;
import com.cookwe.data.repository.interfaces.CommentRepository;
import com.cookwe.utils.errors.RestError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommentRepositoryCustom {

    @Autowired
    private CommentRepository commentRepository;

    public CommentModel getCommentById(Long commentId) {
        Optional<CommentModel> comment = commentRepository.findById(commentId);

        if (comment.isEmpty()) {
            throw RestError.COMMENT_NOT_FOUND.get(commentId);
        }

        return comment.get();
    }
}
