package com.backendfunda.backendfunda.controller;

import com.backendfunda.backendfunda.model.ContactForm;
import com.backendfunda.backendfunda.repository.ContactFormRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth/contacto/")
public class ContactFormController {

    @Autowired
    private ContactFormRepository contactFormRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    // Método para enviar una respuesta predeterminada con diseño personalizado
    private void sendDefaultResponse(String recipientEmail) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setFrom(emailFrom);
            helper.setTo(recipientEmail);
            helper.setSubject("Pronto estaremos en contacto contigo");

            // HTML con diseño personalizado
            String htmlContent = "<html>"
                    + "<head>"
                    + "<style>"
                    + "  body { font-family: Arial, sans-serif; }"
                    + "  .container { background-color: #f2e7ff; padding: 20px; }"
                    + "  h1 { color: #6a3ab2; }"
                    + "  p { color: #333; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<h1>Gracias por ponerte en contacto con nosotros</h1>"
                    + "<p>Pronto estaremos en contacto contigo.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Manejar la excepción en caso de un error en el envío del correo
        }
    }

    @PostMapping("/enviarMensaje")
    public ResponseEntity<String> enviarMensaje(@RequestBody ContactForm contactForm) {
        // Valida los datos del formulario
        if (contactForm.getEmail() == null || contactForm.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo de correo electrónico es obligatorio");
        }
        if (contactForm.getSubject() == null || contactForm.getSubject().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo de asunto es obligatorio");
        }
        if (contactForm.getMessage() == null || contactForm.getMessage().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo de mensaje es obligatorio");
        }

        // Envía un correo electrónico con los datos del formulario
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);  // Usar la dirección configurada
        message.setTo(contactForm.getEmail());  // Usar la dirección proporcionada en el formulario
        message.setSubject(contactForm.getSubject());
        message.setText(contactForm.getMessage());

        javaMailSender.send(message);

        //una respuesta al correo
        sendDefaultResponse(contactForm.getEmail());

        // Guarda el formulario en la base de datos
        contactFormRepository.save(contactForm);

        return ResponseEntity.ok("Mensaje enviado correctamente");
    }
}