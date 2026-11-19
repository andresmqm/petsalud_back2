package com.petsalud.backendpetsalud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=mi_clave_secreta_de_pruebas_petsalud_2026")
class BackendpetsaludApplicationTests {

    @Test
    void contextLoads() {
    }

}



