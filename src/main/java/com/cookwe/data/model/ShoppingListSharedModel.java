package com.cookwe.data.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "shopping_list_shared", indexes = {
        @Index(name = "idx_shopping_list_id", columnList = "shopping_list_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
public class ShoppingListSharedModel {

    @EmbeddedId
    private ShoppingListSharedId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("shoppingListId")
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingListModel shoppingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Data
    @Embeddable
    public static class ShoppingListSharedId implements java.io.Serializable {
        private Long shoppingListId;
        private Long userId;
    }
}
