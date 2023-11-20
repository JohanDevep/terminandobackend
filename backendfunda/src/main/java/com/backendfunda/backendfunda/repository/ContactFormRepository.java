package com.backendfunda.backendfunda.repository;

import com.backendfunda.backendfunda.model.ContactForm;
import org.springframework.data.repository.CrudRepository;

public interface ContactFormRepository extends CrudRepository<ContactForm, Long> {
}
