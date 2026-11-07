package com.petsalud.backendpetsalud.controller;

import com.petsalud.backendpetsalud.dto.AuthResponse;
import com.petsalud.backendpetsalud.dto.LoginRequest;
import com.petsalud.backendpetsalud.entity.Usuario;
import com.petsalud.backendpetsalud.util.JwtUtil;
import com.petsalud.backendpetsalud.service.UsuarioService; // Permite utilizar el servicio de registro.
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import com.petsalud.backendpetsalud.dto.RegistroRequest;

import com.petsalud.backendpetsalud.security.SecurityMonitorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService; // Centraliza las reglas del registro.

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SecurityMonitorService securityMonitorService;

    @PostMapping("/registro") // Atiende solicitudes POST a /api/auth/register.
    public ResponseEntity<String> register(@RequestBody RegistroRequest registroRequest) { // Recibe los datos enviados en JSON.
        Usuario usuario = new Usuario();
        usuario.setNombre(registroRequest.getNombre());
        usuario.setApellido(registroRequest.getApellido());
        usuario.setEmail(registroRequest.getCorreo());
        usuario.setPassword(registroRequest.getPassword());
        usuarioService.registrarUsuario(usuario);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest authRequest, HttpServletRequest request) {
    
        String ip = request.getRemoteAddr(); // Obtiene la dirección IP del cliente.

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            // Registra el inicio de sesión exitoso.
            securityMonitorService.registrarLoginExitoso(ip);

            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (AuthenticationException e) {

            // Registra el intento de inicio de sesión fallido.
            securityMonitorService.registrarLoginFallido(authRequest.getUsername(), ip);
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
