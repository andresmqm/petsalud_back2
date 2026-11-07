package com.petsalud.backendpetsalud.security;
import org.springframework.stereotype.Service;
import org.slf4j.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SecurityMonitorService {
    private static final Logger logger = LoggerFactory.getLogger(SecurityMonitorService.class);

    private final ConcurrentHashMap<String, AtomicInteger> intentosFallidos = new ConcurrentHashMap<>();

    public void registrarLoginFallido(String email, String ip) {
        int intentos = intentosFallidos.computeIfAbsent(ip, clave -> new AtomicInteger(0)).incrementAndGet();
        logger.warn("Intento de login fallido para el email: {} desde la IP: {}. Intentos fallidos: {}", email, ip, intentos);

        if (intentos >= 5) {
            logger.warn("ALERTA DE SEGURIDAD: Se han detectado {} intentos fallidos de login desde la IP: {}.", intentos, ip);
        }
    }

    public void registrarLoginExitoso(String ip) {
        intentosFallidos.remove(ip);
    }
    
}
