package com.cookwe.data.repository.interfaces;

import com.cookwe.data.model.ShoppingListModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingListRepository extends CrudRepository<ShoppingListModel, Long> {

    Optional<ShoppingListModel> findById(Long id);

    List<ShoppingListModel> findByOwnerId(Long ownerId);

    @Query("SELECT s FROM ShoppingListModel s WHERE s.owner.id = :userId OR s.id IN " +
            "(SELECT sl.shoppingList.id FROM ShoppingListSharedModel sl WHERE sl.user.id = :userId)")
    List<ShoppingListModel> findByUserId(@Param("userId") Long userId);
}
