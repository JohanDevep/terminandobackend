package com.backendfunda.backendfunda.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ContactForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String subject;
    private String name;
    private String message;

    // Agrega getters y setters

    public ContactForm() {
    }

    public ContactForm(String email, String subject, String name, String message) {
        this.email = email;
        this.subject = subject;
        this.name = name;
        this.message = message;
    }

}
