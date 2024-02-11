package com.cookwe.data.repository;


import com.cookwe.data.model.RecipeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends CrudRepository<RecipeModel, Long> {
//    Optional<RecipeModel> findByName(String name);

}
