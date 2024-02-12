package com.cookwe.data.repository;

import com.cookwe.data.model.RecipeStepModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeStepRepository extends CrudRepository<RecipeStepModel, Long> {
}
