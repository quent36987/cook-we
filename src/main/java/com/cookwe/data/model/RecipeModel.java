package com.cookwe.data.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;

@Table(name = "recipes")
@Entity
@Getter
@Data
@Setter
@AllArgsConstructor
@With
@NoArgsConstructor
public class RecipeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    public String name;

    public Long time;

    public String user_id;

    public LocalDateTime createdAt;
}
