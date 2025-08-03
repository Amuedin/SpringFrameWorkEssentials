package rewards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;


import common.money.MonetaryAmount;

/**
 * Con @SpringBootTest cargamos el contexto de forma completa, y automática.
 * Recomendable para pruebas de integración. No es tan automático si lo cargamos manualmente
 * con SpringApplication.run() como en el otro test para hsql
 * Clase para testear el servicio en base de datos de Oracle
 */
@SpringBootTest(classes = TestInfrastructureConfig.class) 
public class RewardNetworkOracleTests {

    @Autowired
    private RewardNetwork rewardNetwork;

    @Autowired
    @Qualifier("oracleDataSource")
    private DataSource dataSource;

    

    @Test
    void testTarjetaExiste() throws Exception {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn
                        .prepareStatement("SELECT COUNT(*) FROM T_ACCOUNT_CREDIT_CARD WHERE CARD_NUMBER = ?")) {
            ps.setString(1, "1234123412340001");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("🔍 Registros encontrados: " + count);
                }
            }
        }
    }

    @Test
    public void testRewardForDining() {
        //Recuerda que Oracle no usa indice 0 como ID, y al cargar los datos con los insert hubo problemas
        Dining dining = Dining.createDining("100.00", "1234123412340001", "1234567890");
        
        // call the 'rewardNetwork' to test its rewardAccountFor(Dining) method
        RewardConfirmation confirmation = rewardNetwork.rewardAccountFor(dining);

        // assert the expected reward confirmation results
        assertNotNull(confirmation);
        assertNotNull(confirmation.getConfirmationNumber());

        // assert an account contribution was made
        AccountContribution contribution = confirmation.getAccountContribution();
        assertNotNull(contribution);

        // the account number should be '123456789'
        assertEquals("123456789", contribution.getAccountNumber());

        // the total contribution amount should be 8.00 (8% of 100.00)
        assertEquals(MonetaryAmount.valueOf("8.00"), contribution.getAmount());

        // the total contribution amount should have been split into 2 distributions
        assertEquals(2, contribution.getDistributions().size());

        // each distribution should be 4.00 (as both have a 50% allocation)
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Annabelle").getAmount());
        assertEquals(MonetaryAmount.valueOf("4.00"), contribution.getDistribution("Corgan").getAmount());
    }
}
