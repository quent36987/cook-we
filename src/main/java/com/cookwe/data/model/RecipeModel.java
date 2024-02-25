package com.cookwe.data.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@With
@Table(name = "recipes")
public class RecipeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private Long time;

    private Long portions;

    @Enumerated(EnumType.STRING)
    private ESeason season;

    @Enumerated(EnumType.STRING)
    private EType type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserModel user;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecipeStepModel> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecipePictureModel> pictures = new ArrayList<>();

    @ManyToMany(mappedBy = "favoriteRecipes", fetch = FetchType.LAZY)
    @JsonIgnore
    public List<UserModel> favoritedBy = new ArrayList<>();
}
