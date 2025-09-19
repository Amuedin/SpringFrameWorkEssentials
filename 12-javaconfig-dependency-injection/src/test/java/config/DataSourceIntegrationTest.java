package config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;
import java.sql.Connection;
import rewards.TestInfrastructureConfig;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Esta anotación levanta el contexto de Spring completo
 */
@SpringBootTest(classes = TestInfrastructureConfig.class)//usar la configuración que hemos creado
public class DataSourceIntegrationTest {

    
    /**
     * Podemos cambiar el Qualifier entre hsqlDataSOurce y oracleDataSource para comprobar que conecta
     * a la base de datos deseada. Al usar autowired y no inyección por constructor, colocamos
     * la anotación @Qualifier sobre el mismo campo
     */
    @Autowired
    @Qualifier("oracleDataSource")
    private DataSource dataSource;

    

   
    @Test
    void testConnection() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn);
            System.out.println("✅ ¡Conectado a "+ conn.getMetaData().getDatabaseProductName() + " como " + conn.getMetaData().getUserName() + "!");
        }
    }
}
