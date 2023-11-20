package com.backendfunda.backendfunda.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.sql.Blob;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "instructores")
public class Instructores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instructores")
    private Long idInstructores;
    private String titulos;
    private String images;
    private String nombre;
    private String description;
    private String estado;


}

