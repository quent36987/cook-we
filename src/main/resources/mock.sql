INSERT INTO recipes (name,)


INSERT INTO ingredients (name, quantity, unit, recipe_id)
values ('brocoli', 2, 'PIECE', 1), ('lait', 500, 'MILLILITER', 1);

INSERT INTO step (name, text, recipe_id, stepNumber)
values ('step1', 'couper le brocoli', 1, 1),
       ('step2', 'faire bouillir le lait', 1, 2);