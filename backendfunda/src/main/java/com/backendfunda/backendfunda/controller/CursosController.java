package com.backendfunda.backendfunda.controller;

import com.backendfunda.backendfunda.dtos.DtoCursos;
import com.backendfunda.backendfunda.model.Cursos;
import com.backendfunda.backendfunda.repository.CursosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/auth/")
public class CursosController {

    @Autowired
    private CursosRepository cursosRepository;

    @GetMapping("/cursos")
    public List<Cursos> getCursos() {
        return cursosRepository.findAll();
    }

    @GetMapping("/cursos/{id}")
    public ResponseEntity<?> getCursos(@PathVariable Long id) {
        Optional<Cursos> cursoOptional = cursosRepository.findById(id);

        if (cursoOptional.isPresent()) {
            Cursos curso = cursoOptional.get();
            return ResponseEntity.ok(curso);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado por ese ID");
        }
    }

    @DeleteMapping("/cursos/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        if (cursosRepository.existsById(id)) {
            cursosRepository.deleteById(id);
            return ResponseEntity.ok("Curso eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ID no encontrado");
        }
    }

    @PutMapping("cursoEdit/{id}")
    public ResponseEntity<String> editarCurso(@PathVariable Long id, @RequestBody DtoCursos dtoCursos) {
        Optional<Cursos> optionalCursos = cursosRepository.findById(id);
        if (optionalCursos.isPresent()) {
            Cursos cursos = optionalCursos.get();
            cursos.setVideo(dtoCursos.getVideo());
            cursos.setVideo1(dtoCursos.getVideo1());
            cursos.setVideo2(dtoCursos.getVideo2());
            cursos.setVideo3(dtoCursos.getVideo3());
            cursos.setVideo4(dtoCursos.getVideo4());
            cursos.setVideo5(dtoCursos.getVideo5());
            cursos.setVideo6(dtoCursos.getVideo6());
            cursos.setVideo7(dtoCursos.getVideo7());
            cursos.setVideo8(dtoCursos.getVideo8());
            cursos.setVideo9(dtoCursos.getVideo9());
            cursosRepository.save(cursos);
            return ResponseEntity.ok("Curso Modificado Exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado, verifica el ID: " + id);
        }
    }

    @PostMapping("/CrearCurso")
    public ResponseEntity<String> crearCurso(@RequestBody DtoCursos dtoCursos) {
        if (cursosRepository.existsByTitulo(dtoCursos.getTitulo())) {
            return new ResponseEntity<>("El curso con ese titulo ya existe, intenta con otro titulo", HttpStatus.BAD_REQUEST);
        }
        Cursos cursos = new Cursos();
        cursos.setTitulo(dtoCursos.getTitulo());
        cursos.setDescription(dtoCursos.getDescription());
        cursos.setImages(dtoCursos.getImages());
        cursos.setInstructor(dtoCursos.getInstructor());
        cursos.setCategoria(dtoCursos.getCategoria());
        cursos.setVideo(dtoCursos.getVideo());
        cursos.setVideo1(dtoCursos.getVideo1());
        cursos.setVideo2(dtoCursos.getVideo2());
        cursos.setVideo3(dtoCursos.getVideo3());
        cursos.setVideo4(dtoCursos.getVideo4());
        cursos.setVideo5(dtoCursos.getVideo5());
        cursos.setVideo6(dtoCursos.getVideo6());
        cursos.setVideo7(dtoCursos.getVideo7());
        cursos.setVideo8(dtoCursos.getVideo8());
        cursos.setVideo9(dtoCursos.getVideo9());
        cursosRepository.save(cursos);
        return new ResponseEntity<>("Registro de cursos exitoso", HttpStatus.OK);
    }
}