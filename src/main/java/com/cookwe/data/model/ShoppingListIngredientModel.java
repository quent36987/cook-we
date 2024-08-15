package com.cookwe.data.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "shopping_list_ingredient")
public class ShoppingListIngredientModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_recipe_id", nullable = true)
    private ShoppingListRecipeModel shoppingListRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingListModel shoppingList;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "checked", nullable = false)
    private Boolean checked = false;
}
