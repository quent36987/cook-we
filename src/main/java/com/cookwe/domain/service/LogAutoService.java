package com.cookwe.domain.service;

import com.cookwe.data.model.CommentModel;
import com.cookwe.data.model.LogAutoServiceModel;
import com.cookwe.data.model.RecipeModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.CommentRepositoryCustom;
import com.cookwe.data.repository.RecipeRepositoryCustom;
import com.cookwe.data.repository.interfaces.CommentRepository;
import com.cookwe.data.repository.interfaces.LogAutoServiceRepository;
import com.cookwe.domain.entity.CommentDTO;
import com.cookwe.domain.entity.LogAutoServiceDTO;
import com.cookwe.domain.mapper.CommentMapper;
import com.cookwe.domain.mapper.LogAutoMapper;
import com.cookwe.utils.errors.RestError;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LogAutoService {

    private final LogAutoServiceRepository logAutoServiceRepository;
    private final LogAutoMapper logAutoMapper;

    public LogAutoService(LogAutoServiceRepository logAutoServiceRepository, LogAutoMapper logAutoMapper) {
        this.logAutoServiceRepository = logAutoServiceRepository;
        this.logAutoMapper = logAutoMapper;
    }


    @Transactional(readOnly = true)
    public List<LogAutoServiceDTO> getLogs(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<LogAutoServiceModel> logs = logAutoServiceRepository.findAll(pageable);

        return logAutoMapper.toDTOList(logs.getContent());
    }

}
