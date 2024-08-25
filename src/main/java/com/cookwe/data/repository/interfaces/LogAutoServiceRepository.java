package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.LogAutoServiceModel;
import com.cookwe.data.model.RecipeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LogAutoServiceRepository extends CrudRepository<LogAutoServiceModel, Long> {

    Page<LogAutoServiceModel> findAll(Pageable pageable);

}
