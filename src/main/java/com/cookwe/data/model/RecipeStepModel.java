package com.cookwe.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Table(name = "recipe_steps")
public class RecipeStepModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @JsonIgnore
    private RecipeModel recipe;

    private String text;

    @Column(name = "step_number")
    private Long stepNumber;

    public RecipeStepModel(RecipeModel recipe, String text, Long stepNumber) {
        this.recipe = recipe;
        this.text = text;
        this.stepNumber = stepNumber;
    }
}
