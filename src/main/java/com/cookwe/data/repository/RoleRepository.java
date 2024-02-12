package com.cookwe.data.repository;


import com.cookwe.data.model.ERole;
import com.cookwe.data.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
  Optional<RoleModel> findByName(ERole name);
}
