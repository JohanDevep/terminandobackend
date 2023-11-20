package com.backendfunda.backendfunda.repository;

import com.backendfunda.backendfunda.model.Instructores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface InstructoresRepository extends JpaRepository <Instructores, Long> {
    Optional<Instructores> findById(Long idInstructores);
    boolean existsByNombre(String nombre);
    // Otros métodos de repositorio, si los tienes
    Boolean existsBynombre(String nombre);
}
