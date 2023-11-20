package com.backendfunda.backendfunda.controller;
import com.backendfunda.backendfunda.dtos.DtoAuthRespuesta;
import com.backendfunda.backendfunda.dtos.DtoLogin;
import com.backendfunda.backendfunda.dtos.DtoPerfil;
import com.backendfunda.backendfunda.dtos.DtoRegistro;
import com.backendfunda.backendfunda.model.Roles;
import com.backendfunda.backendfunda.model.Usuarios;
import com.backendfunda.backendfunda.repository.RoleRepository;
import com.backendfunda.backendfunda.repository.UsuariosRepository;
import com.backendfunda.backendfunda.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // Reemplaza con el origen de tu aplicación React
//@CrossOrigin(origins = "*", allowedHeaders = "*")
//@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth/")
public class RestControllerAuth {
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;
    private RoleRepository rolesRepository;
    private UsuariosRepository usuariosRepository;
    private JwtGenerador jwtGenerador;

    @Autowired

    public RestControllerAuth(AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, RoleRepository rolesRepository, UsuariosRepository usuariosRepository, JwtGenerador jwtGenerador) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
        this.usuariosRepository = usuariosRepository;
        this.jwtGenerador = jwtGenerador;
    }
    //Método para poder registrar usuarios con role "user"
    @PostMapping("register")
    public ResponseEntity<String> registrar(@RequestBody DtoRegistro dtoRegistro) {
        // Validar confirmación de contraseña
        if (!dtoRegistro.getPassword().equals(dtoRegistro.getConfirmarPassword())) {
            return new ResponseEntity<>("Las contraseñas no coinciden.", HttpStatus.BAD_REQUEST);
        }

        // Validar si el correo ya existe en la base de datos
        if (usuariosRepository.existsByCorreo(dtoRegistro.getCorreo())) {
            return new ResponseEntity<>("Ya existe un usuario con el correo proporcionado. Intenta con otro correo.", HttpStatus.BAD_REQUEST);
        }

        // Validar si el teléfono tiene 10 dígitos
        if (dtoRegistro.getTelefono().length() != 10) {
            return new ResponseEntity<>("El número de teléfono debe tener 10 dígitos.", HttpStatus.BAD_REQUEST);
        }

        // Validar si el nombre de usuario ya existe en la base de datos
        if (usuariosRepository.existsByNombre(dtoRegistro.getNombre())) {
            return new ResponseEntity<>("Ya existe un usuario con el nombre proporcionado. Intenta con otro nombre de usuario.", HttpStatus.BAD_REQUEST);
        }

        // Validar que la contraseña tenga al menos un número y una mayúscula
        String password = dtoRegistro.getPassword();
        if (!password.matches(".*[0-9].*") || !password.matches(".*[A-Z].*")) {
            return new ResponseEntity<>("La contraseña debe contener al menos un número y una mayúscula.", HttpStatus.BAD_REQUEST);
        }

        // Crear el objeto Usuarios y realizar el registro
        Usuarios usuarios = new Usuarios();
        usuarios.setNombre(dtoRegistro.getNombre());
        usuarios.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));
        usuarios.setApellido(dtoRegistro.getApellido());
        usuarios.setCorreo(dtoRegistro.getCorreo());
        usuarios.setTelefono(dtoRegistro.getTelefono());

        // Asignar roles
        Roles roles = rolesRepository.findByNombre("USUARIO").orElseThrow(() -> new RuntimeException("Rol 'USUARIO' no encontrado"));
        usuarios.setRoles(Collections.singletonList(roles));

        // Guardar en la base de datos
        usuariosRepository.save(usuarios);
        return new ResponseEntity<>("Registro de usuario exitoso", HttpStatus.OK);
    }


    //Método para poder guardar usuarios de tipo ADMIN
    @PostMapping("registerAdm")
    public ResponseEntity<String> registrarAdmin(@RequestBody DtoRegistro dtoRegistro) {
        if (usuariosRepository.existsByNombre(dtoRegistro.getNombre())) {
            return new ResponseEntity<>("el usuario ya existe, intenta con otro", HttpStatus.BAD_REQUEST);
        }
        Usuarios usuarios = new Usuarios();
        usuarios.setNombre(dtoRegistro.getNombre());
        usuarios.setPassword(passwordEncoder.encode(dtoRegistro.getPassword()));
        usuarios.setApellido(dtoRegistro.getApellido());
        usuarios.setCorreo(dtoRegistro.getCorreo());
        usuarios.setTelefono(dtoRegistro.getTelefono());
        Roles roles = rolesRepository.findByNombre("ADMIN").get();
        usuarios.setRoles(Collections.singletonList(roles));
        usuariosRepository.save(usuarios);
        return new ResponseEntity<>("Registro de admin exitoso", HttpStatus.OK);
    }

    //Método para poder logear un usuario y obtener un token
    @PostMapping("ingresar")
    public ResponseEntity<?> login(@RequestBody DtoLogin dtoLogin) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dtoLogin.getNombre(), dtoLogin.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generar el token JWT
        String token = jwtGenerador.generarToken(authentication);
        // Obtener la información del perfil del usuario
        Optional<Usuarios> optionalUserInfo = usuariosRepository.findByNombre(dtoLogin.getNombre());
        if (optionalUserInfo.isPresent()) {
            Usuarios userInfo = optionalUserInfo.get();
            // Crear un DTO que incluya tanto el token como la información del perfil
            DtoPerfil response = new DtoPerfil(token, userInfo.getNombre(), userInfo.getApellido(), userInfo.getTelefono(), userInfo.getCorreo(), userInfo.getImagen());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            // En caso de que no se pueda obtener la información del perfil, solo retornar el token
            return new ResponseEntity<>(new DtoAuthRespuesta(token), HttpStatus.OK);
        }
    }
    @GetMapping("/usuariosver")
    List<Usuarios> getAllUsers(){
        return usuariosRepository.findAll();
    }

    @GetMapping("/usuariosver/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable Long id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(usuariosRepository.findById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Server error.\"}");
        }
    }
    @GetMapping("/perfil")
    public ResponseEntity<DtoPerfil> getPerfil(Principal principal) {
        // Obtiene el nombre de usuario del usuario autenticado
        String nombreDeUsuario = principal.getName();

        // Busca el perfil del usuario por su nombre de usuario
        Optional<Usuarios> optionalUserProfile = usuariosRepository.findByNombre(nombreDeUsuario);

        if (optionalUserProfile.isPresent()) {
            Usuarios userProfile = optionalUserProfile.get();
            // Mapea la entidad Usuarios a un DTO de perfil
            DtoPerfil perfilDto = new DtoPerfil(
                    userProfile.getNombre(),
                    userProfile.getNombre(),
                    userProfile.getApellido(),
                    userProfile.getTelefono(),
                    userProfile.getCorreo(),
                    userProfile.getImagen()
            );
            return ResponseEntity.ok(perfilDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/perfil/editar")
    public ResponseEntity<Object> editarPerfil(@RequestBody DtoPerfil perfilDto, Principal principal) {
        try {
            // Obtén el nombre de usuario del usuario autenticado
            String nombreDeUsuario = principal.getName();

            // Busca el perfil del usuario por su nombre de usuario
            Optional<Usuarios> optionalUserProfile = usuariosRepository.findByNombre(nombreDeUsuario);

            if (optionalUserProfile.isPresent()) {
                Usuarios userProfile = optionalUserProfile.get();

                // Validación de longitud del número de teléfono
                String telefono = perfilDto.getTelefono();
                if (telefono != null && telefono.length() == 10) {
                    userProfile.setTelefono(telefono);
                } else {
                    return ResponseEntity.badRequest().body("Verifica El Numero Telefonico."); // Devuelve un código de error 400 Bad Request si la longitud del teléfono no es válida
                }

                // Validación de existencia del correo en la base de datos
                String nuevoCorreo = perfilDto.getCorreo();
                if (!nuevoCorreo.equals(userProfile.getCorreo()) && usuariosRepository.existsByCorreo(nuevoCorreo)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo ya existe en la base de datos."); // Mensaje personalizado
                }

                // Actualiza los demás campos del perfil con los valores del DTO
                userProfile.setNombre(perfilDto.getNombre());
                userProfile.setApellido(perfilDto.getApellido());
                userProfile.setCorreo(nuevoCorreo); // Se utiliza el nuevo correo validado
                userProfile.setImagen(perfilDto.getImagen());

                // Guarda los cambios en el perfil
                Usuarios perfilActualizado = usuariosRepository.save(userProfile);

                // Mapea la entidad Usuarios actualizada a un DTO de perfil
                DtoPerfil perfilActualizadoDto = new DtoPerfil(
                        perfilActualizado.getNombre(),
                        perfilActualizado.getNombre(),
                        perfilActualizado.getApellido(),
                        perfilActualizado.getTelefono(),
                        perfilActualizado.getCorreo(),
                        perfilActualizado.getImagen()
                );

                return ResponseEntity.ok(perfilActualizadoDto);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Manejo de la excepción, puedes imprimir el mensaje de error o realizar alguna acción específica
            e.printStackTrace(); // Aquí simplemente imprimimos el rastreo de la excepción, puedes personalizar esto según tus necesidades.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar el perfil."); // Mensaje personalizado
        }
    }
}

