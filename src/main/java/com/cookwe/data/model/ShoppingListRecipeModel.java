package com.cookwe.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "shopping_list_recipe")
public class ShoppingListRecipeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeModel recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_list_id", nullable = false)
    private ShoppingListModel shoppingList;

    @Column(name = "portion", nullable = false)
    private int portion;

    @OneToMany(mappedBy = "shoppingListRecipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ShoppingListIngredientModel> ingredients = new ArrayList<>();
}
