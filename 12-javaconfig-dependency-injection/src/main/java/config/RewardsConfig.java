package config;

import javax.sql.DataSource;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import rewards.RewardNetwork;
import rewards.internal.RewardNetworkImpl;
import rewards.internal.account.AccountRepository;
import rewards.internal.account.JdbcAccountRepository;
import rewards.internal.restaurant.JdbcRestaurantRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.JdbcRewardRepository;
import rewards.internal.reward.RewardRepository;

/**
 * TODO-00: In this lab, you are going to exercise the following:
 * - Creating Spring configuration class
 * - Defining bean definitions within the configuration class
 * - Specifying the dependency relationships among beans
 * - Injecting dependencies through constructor injection
 * - Creating Spring application context in the test code
 *   (WITHOUT using Spring testContext framework)
 *
 * TODO-01: Make this class a Spring configuration class
 * - Use an appropriate annotation.
 *
 * TODO-02: Define four empty @Bean methods, one for the
 *          reward-network and three for the repositories.
 * - The names of the beans should be:
 *   - rewardNetwork
 *   - accountRepository
 *   - restaurantRepository
 *   - rewardRepository
 *
 * TODO-03: Inject DataSource through constructor injection
 * - Each repository implementation has a DataSource
 *   property to be set, but the DataSource is defined
 *   elsewhere (TestInfrastructureConfig.java), so you
 *   will need to define a constructor for this class
 *   that accepts a DataSource parameter.
 * - As it is the only constructor, @Autowired is optional.
 *
 * TODO-04: Implement each @Bean method to contain the code
 *          needed to instantiate its object and set its
 *          dependencies
 * - You can create beans from the following implementation classes
 *   - rewardNetwork bean from RewardNetworkImpl class
 *   - accountRepository bean from JdbcAccountRepository class
 *   - restaurantRepository bean from JdbcRestaurantRepository class
 *   - rewardRepository bean from JdbcRewardRepository class
 * - Note that return type of each bean method should be an interface
 *   not an implementation.
 */

 /**
  * This annotation tells Spring that treat this class as a set of configuration
  instructions to be used when the application is starting up
  >>>>>>>Registro de los beans de manera manual
  */
@Configuration
public class RewardsConfig {

	// Set this by adding a constructor to inject the dependency
	//@Qualifier("hsqlDataSource") no tiene efecto porque no está siendo inyectado automaticamente con Autowired.
	//Al usar inyección por constructor, @Qualifier va en el parametro del constructor
	private DataSource dataSource;

	/**
	 * 
	 * @param dataSource
	 */
	//@Autowired: totalmente inutil aqui, TestInfraestructureConfig tiene
	//registrado el bean DataSource y al estar marcada como @Configuration,
	//al crear manualmente el contexto:
	//SpringApplication.run(TestInfrastructureConfig.class)
	//Spring ya tiene acceso a los beans y resuelve cualquier dependencia entre las clases,
	//en este caso le pedimos a Spring un bean de tipo DataSource
	//AQUI SELECCIONAMOS LA BASE DE DATOS: oracleDataSource // hsqlDataSource
	public RewardsConfig(@Qualifier("hsqlDataSource") DataSource dataSource){
		this.dataSource = dataSource;
	}
	

	//Registro de RewardNetwork como Bean
	@Bean
	public RewardNetwork rewardNetwork(){
		return new RewardNetworkImpl(accountRepository(), restaurantRepository(), rewardRepository());
	}


	/**
	 * Within each repository method we have to instantiate the right implementation
	 * of the AccountRepository Interface and return it. Since the repositories have a dependency on 
	 * a DataSource, its needed to call the method setDataSource passing in a reference to the dataSource
	 * injected in this class.
	 * Estamos instanciando los repositorios de manera mixta,
	 * Spring crea los Beans, dentro de estos usamos new para instanciar manualmente,
	 * y setDataSource para inyectar manualmente también
	 * @return
	 */
	@Bean
	public AccountRepository accountRepository(){
		JdbcAccountRepository repository = new JdbcAccountRepository();
		repository.setDataSource(dataSource);
		return repository;
	}

	@Bean
	public RestaurantRepository restaurantRepository(){
		JdbcRestaurantRepository repository = new JdbcRestaurantRepository();
		repository.setDataSource(dataSource);

		return repository;
	}

	@Bean
	public RewardRepository rewardRepository(){
		JdbcRewardRepository repository = new JdbcRewardRepository();
		repository.setDataSource(dataSource);

		return repository;
	}

}
