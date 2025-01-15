package com.trocandgo.trocandgo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trocandgo.trocandgo.entity.Roles;
import com.trocandgo.trocandgo.entity.enums.RoleName;

public interface RoleRepository extends JpaRepository<Roles, Long> {
    Roles getByName(RoleName name);
}
