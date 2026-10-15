package com.eletrorecicla.eletrorecicla;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de integração: sobe o contexto completo da aplicação, o que exige
 * um SQL Server acessível e as variáveis de ambiente DB_PASSWORD e JWT_SECRET
 * (veja o README). Só é executado quando DB_PASSWORD estiver definida;
 * caso contrário é ignorado, para que o build não dependa de infraestrutura externa.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "DB_PASSWORD", matches = ".+")
class EletroreciclaApplicationTests {

	@Test
	void contextLoads() {
	}

}