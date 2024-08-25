package com.cookwe.domain.service;

import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.interfaces.ShoppingListRepository;
import com.cookwe.domain.entity.ShoppingListDTO;
import com.cookwe.domain.entity.ShoppingListDetailDTO;
import com.cookwe.domain.mapper.ShoppingListDetailMapper;
import com.cookwe.domain.mapper.ShoppingListMapper;
import com.cookwe.utils.errors.RestError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ShoppingListService {


    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListMapper shoppingListMapper;
    private final ShoppingListDetailMapper shoppingListDetailMapper;

    @Transactional(readOnly = true)
    public List<ShoppingListDTO> getUserShoppingList(Long userId) {
        List<ShoppingListModel> lists = shoppingListRepository.findByUserId(userId);
        return lists.stream()
                .map(shoppingListMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShoppingListDetailDTO getShoppingListWithId(Long userId, Long shoppingListId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> RestError.SHOPPING_LIST_NOT_FOUND.get(shoppingListId));

        if (!isUserAllowed(userId, shoppingList)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous n'êtes pas autorisé à voir cette liste de courses");
        }

        return shoppingListDetailMapper.toDTO(shoppingList);
    }

    @Transactional
    public ShoppingListDTO createEmptyShoppingList(String name, Long ownerId) {
        ShoppingListModel shoppingList = new ShoppingListModel();
        shoppingList.setName(name);
        shoppingList.setCreatedAt(LocalDateTime.now());

        UserModel owner = new UserModel();
        owner.setId(ownerId);
        shoppingList.setOwner(owner);

        ShoppingListModel savedList = shoppingListRepository.save(shoppingList);
        return shoppingListMapper.toDTO(savedList);
    }

    @Transactional
    public void deleteList(Long userId,Long listId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> RestError.SHOPPING_LIST_NOT_FOUND.get(listId));

        if (!isUserAllowed(userId, shoppingList)) {
            throw RestError.FORBIDDEN_MESSAGE.get("Vous n'êtes pas autorisé à supprimer cette liste de courses");
        }

        shoppingListRepository.delete(shoppingList);
    }

    private boolean isUserAllowed(Long userId, ShoppingListModel shoppingList) {
        return shoppingList.getOwner().getId().equals(userId) ||
                shoppingList.getSharedWithUsers().stream().anyMatch(user -> user.getId().equals(userId));
    }
}
