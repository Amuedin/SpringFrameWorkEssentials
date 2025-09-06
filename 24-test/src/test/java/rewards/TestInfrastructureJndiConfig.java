package rewards;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Sets up a JNDI service for our test.
 *
 * See SimpleJndiHelper class to see how this works.
 */
@Configuration
@Profile("jndi")
public class TestInfrastructureJndiConfig {

	/**
	 * Static method because we are defining a Bean post-processor.
	 * Al declararlo como static, se registrará al principio del ciclo de vida
	 * El contenedor (Tomcat, Simple-JNDI, etc.)realiza un lookup con la clave de 
	 * busqueda y decide cómo resolver ese nombre, en este caso Simple-JNDI, que
	 * lo mapeará a un fichero de propiedades, en el que normalmente suele definirse un
	 * DataSource
	 */
	@Bean
	public static SimpleJndiHelper jndiHelper() {
		return new SimpleJndiHelper();
	}

	/**
	 * Create the data-source by doing a JNDI lookup.
	 * 
	 * @return The data-source if found
	 * @throws Exception
	 *             Any lookup error.
	 */
	@Bean
	public DataSource dataSource() throws Exception {
		return (DataSource) (new InitialContext()).lookup("java:/comp/env/jdbc/rewards"/*Clave de busqueda, no es una ruta */);
	}
}
