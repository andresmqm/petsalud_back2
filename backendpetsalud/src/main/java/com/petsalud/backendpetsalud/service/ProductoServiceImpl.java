package com.petsalud.backendpetsalud.service;

import com.petsalud.backendpetsalud.entity.Producto;
import com.petsalud.backendpetsalud.repository.ProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto agregarProducto(Producto producto) {
        // Elimina las etiquetas HTML de los campos de texto.
        producto.setNombre(limpiarTexto(producto.getNombre()));
        producto.setDescripcion(limpiarTexto(producto.getDescripcion()));
        producto.setCategoria(limpiarTexto(producto.getCategoria()));

        // Rechaza un stock negativo.
        if (producto.getStock() != null && producto.getStock() < 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El stock no puede ser negativo"
            );
        }

        // El nombre del producto no puede estar vacío.
        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre del producto no puede estar vacío"
            );
        }

        // Verificar si el producto ya existe
        Optional<Producto> productoExistente = productoRepository.findByNombre(producto.getNombre());
        if (productoExistente.isPresent()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El producto ya existe"
            );
        }
        return productoRepository.save(producto);
    }

    @Override
    public Optional<Producto> obtenerProductoPorId(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto actualizarProducto(Long id, Producto productoActualizado) {
        // Elimina las etiquetas HTML de los campos de texto.
        productoActualizado.setNombre(limpiarTexto(productoActualizado.getNombre()));
        productoActualizado.setDescripcion(limpiarTexto(productoActualizado.getDescripcion()));
        productoActualizado.setCategoria(limpiarTexto(productoActualizado.getCategoria()));

        // Rechaza un stock negativo al editar.
        if (productoActualizado.getStock() != null && productoActualizado.getStock() < 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El stock no puede ser negativo"
            );
        }

        // El nombre del producto no puede estar vacío al editar.
        if (productoActualizado.getNombre() == null || productoActualizado.getNombre().trim().isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre del producto no puede estar vacío"
            );
        }

        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(productoActualizado.getNombre());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setPrecio(productoActualizado.getPrecio());
            producto.setStock(productoActualizado.getStock());
            producto.setCategoria(productoActualizado.getCategoria());
            return productoRepository.save(producto);
        }).orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Producto no encontrado"
        ));
    }

    @Override
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    @Override
    public List<Producto> listarProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Producto> listarProductosConStockBajo(Integer cantidad) {
        return productoRepository.findByStockLessThan(cantidad);
    }

    // Limpia el HTML y conserva null cuando no se recibe un valor.
    private String limpiarTexto(String texto) {
        return texto == null ? null : Jsoup.clean(texto, Safelist.none());
    }
}
