package com.cookwe.data.repository;


import com.cookwe.data.model.RecipeModel;
import com.cookwe.domain.entity.RecipeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RecipeRepository extends CrudRepository<RecipeModel, Long> {
//    Optional<RecipeModel> findByName(String name);
    @Query("SELECT r FROM RecipeModel r WHERE r.name = :name")
    Optional<RecipeModel> findByName(@Param("name") String name);




}
