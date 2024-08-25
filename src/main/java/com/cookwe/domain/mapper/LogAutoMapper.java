package com.cookwe.domain.mapper;

import com.cookwe.data.model.CommentModel;
import com.cookwe.data.model.LogAutoServiceModel;
import com.cookwe.domain.entity.CommentDTO;
import com.cookwe.domain.entity.LogAutoServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LogAutoMapper {


    LogAutoServiceDTO toDTO(LogAutoServiceModel log);

    List<LogAutoServiceDTO> toDTOList(List<LogAutoServiceModel> logs);

}
