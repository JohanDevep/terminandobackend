package com.backendfunda.backendfunda.dtos;

import lombok.Data;

import java.sql.Blob;

@Data
public class DtoInstructores {
    private Long id;
    private String titulos;
    private String images;
    private String description;
    private String nombre;
    private String estado;
}