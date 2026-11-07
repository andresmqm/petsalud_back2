package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.AuthResponse;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.util.JwtUtil;
import com.petsalud.backendpetsalud.security.UsuarioDetailsService;
import com.petsalud.backendpetsalud.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.petsalud.backendpetsalud.dto.RegistroRequest;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioDetailsService usuarioDetailsService;

    // POST /api/usuarios/registro

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody RegistroRequest registroRequest) {
        Usuario usuario = new Usuario();
        usuario.setNombre(registroRequest.getNombre());
        usuario.setApellido(registroRequest.getApellido());
        usuario.setEmail(registroRequest.getCorreo());
        usuario.setPassword(registroRequest.getPassword());
        
        usuarioService.registrarUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
        
    }

    // POST /api/usuarios/login

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) 
    
    {
        // autenticamos credenciales
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // cargamos usuario y generamos el JWT
        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        // respondemos con el DTO AuthResponse
        return ResponseEntity.ok(new AuthResponse(token));
    }

    // Demostrar la autorización por roles

    // GET /api/usuarios/gerencia (Solo GERENTE o ADMIN)
    @GetMapping("/gerencia")
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMIN')")
    public ResponseEntity<String> soloGerencia() {
        return ResponseEntity.ok("Acceso autorizado: Bienvenido al panel de Gerencia.");
    }

    // GET /api/usuarios/empleados (Solo EMPLEADO o GERENTE)
    @GetMapping("/empleados")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    public ResponseEntity<String> soloEmpleados() {
        return ResponseEntity.ok("Acceso autorizado: Bienvenido al panel operativo de Empleados.");
    }

    // GET /api/usuarios/clientes (Disponible para CLIENTE)
    @GetMapping("/clientes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<String> soloClientes() {
        return ResponseEntity.ok("Acceso autorizado: Bienvenido al portal de Clientes Petsalud.");
    }

    @GetMapping("/{id}") 
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')") // Permite ejecutar el método si el usuario tiene cualquiera de estos dos roles
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        return usuarioService.obtenerUsuarioPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    public ResponseEntity<Usuario> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        Usuario usuarioActualizado = usuarioService.actualizarUsuario(id, usuario);
        return ResponseEntity.ok(usuarioActualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLEADO', 'GERENTE')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }
}