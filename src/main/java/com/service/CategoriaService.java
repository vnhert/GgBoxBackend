package com.service;

import org.springframework.stereotype.Service;
import com.model.CategoriaProducto;
import com.repository.CategoriaProductoRepository;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaProductoRepository categoriaProductoRepository;

    public CategoriaService(CategoriaProductoRepository categoriaProductoRepository) {
        this.categoriaProductoRepository = categoriaProductoRepository;
    }

    public List<CategoriaProducto> findAll() {
        return categoriaProductoRepository.findAll();
    }

    public CategoriaProducto findById(Long id) {
        return categoriaProductoRepository.findById(id).orElse(null);
    }

    public CategoriaProducto save(CategoriaProducto categoriaProducto) {
        return categoriaProductoRepository.save(categoriaProducto);
    }

    public void delete(Long id) {
        categoriaProductoRepository.deleteById(id);
    }
}
