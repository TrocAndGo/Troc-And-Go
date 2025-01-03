package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.model.Roles;
import com.trocandgo.trocandgo.model.enums.RoleName;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Roles getByName(RoleName name);
}
