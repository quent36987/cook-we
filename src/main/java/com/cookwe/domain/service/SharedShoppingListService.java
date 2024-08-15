package com.cookwe.domain.service;


import com.cookwe.data.model.ShoppingListModel;
import com.cookwe.data.model.UserModel;
import com.cookwe.data.repository.interfaces.ShoppingListRepository;
import com.cookwe.data.repository.interfaces.UserRepository;
import com.cookwe.utils.errors.RestError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class SharedShoppingListService {


    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;


    public void addUserToShoppingList(Long requesterId, String usernameToAdd, Long shoppingListId) {
        ShoppingListModel shoppingList = getShoppingListIfOwner(requesterId, shoppingListId);
        UserModel userToAdd = getUserIfExists(usernameToAdd);

        if (isUserAlreadyShared(shoppingList, userToAdd.getId())) {
            throw RestError.BAD_REQUEST_MESSAGE.get("User is already in the shopping list");
        }

        shoppingList.getSharedWithUsers().add(userToAdd);
        shoppingListRepository.save(shoppingList);
    }

    public void removeUserFromShoppingList(Long requesterId, String usernameToRemove, Long shoppingListId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> RestError.SHOPPING_LIST_NOT_FOUND.get(shoppingListId));

        if (!shoppingList.getOwner().getId().equals(requesterId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of the shopping list");
        }

        UserModel userToRemove = getUserIfExists(usernameToRemove);

        if (!isUserAlreadyShared(shoppingList, userToRemove.getId())) {
            throw RestError.BAD_REQUEST_MESSAGE.get("User is not in the shopping list");
        }

        shoppingList.getSharedWithUsers().remove(userToRemove);
        shoppingListRepository.save(shoppingList);
    }

    private ShoppingListModel getShoppingListIfOwner(Long requesterId, Long shoppingListId) {
        ShoppingListModel shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> RestError.SHOPPING_LIST_NOT_FOUND.get(shoppingListId));

        if (!shoppingList.getOwner().getId().equals(requesterId)) {
            throw RestError.FORBIDDEN_MESSAGE.get("You are not the owner of the shopping list");
        }

        return shoppingList;
    }

    private UserModel getUserIfExists(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(RestError.USER_NOT_FOUND::get);
    }

    private boolean isUserAlreadyShared(ShoppingListModel shoppingList, Long userId) {
        return shoppingList.getSharedWithUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }
}