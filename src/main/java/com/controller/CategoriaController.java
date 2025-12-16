package com.controller;

import org.springframework.web.bind.annotation.*;
import com.model.CategoriaProducto;
import com.service.CategoriaService;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public List<CategoriaProducto> getAll() {
        return categoriaService.findAll();
    }

    @GetMapping("/{id}")
    public CategoriaProducto getById(@PathVariable Long id) {
        return categoriaService.findById(id);
    }

    @PostMapping
    public CategoriaProducto create(@RequestBody CategoriaProducto categoria) {
        return categoriaService.save(categoria);
    }

    @PutMapping("/{id}")
    public CategoriaProducto update(@PathVariable Long id, @RequestBody CategoriaProducto categoria) {
        categoria.setId(id);
        return categoriaService.save(categoria);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoriaService.delete(id);
    }
}