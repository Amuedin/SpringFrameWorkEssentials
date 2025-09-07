DESCRIPCION MODULO 1: \lab\10-spring-intro
  Diseño de lógica de negocio desacoplada de las API de infraestructura.

  Pruebas unitarias con JUnit 5 utilizando repositorios stub para aislar la lógica de negocio.

  Preparación del código para integrarlo posteriormente en un contexto Spring con repositorios reales y acceso a base de datos.


DESCRIPCION MODULO 2: \lab\12-javaconfig-dependency-injection. 
ADEMAS DEL CONTENIDO PROPORCIONADO POR LA ACADEMIA PARA EL APRENDIZAJE, HE IMPLEMENTADO POR MI CUENTA UNA CONEXION A UNA BASE DE DATOS DE ORACLE. RESUMEN SEGUNDO MODULO:

  >>>>Módulo de Configuración de DataSources y Pruebas de Integración
Este módulo implementa una arquitectura de pruebas que permite conectar la aplicación a múltiples orígenes de datos utilizando Spring Boot. Se han desarrollado pruebas de integración sobre bases de datos embebidas (HSQL) y bases de datos externas (Oracle), demostrando la flexibilidad del sistema de configuración.

  >>>>Características implementadas
Configuración de múltiples DataSource con Spring y uso de @Qualifier para seleccionar entre ellos.

Pruebas de integración que conectan con:

  HSQLDB embebida, para tests rápidos y aislados.

  Oracle, para validaciones con el SGBD de destino real.

  Para cambiar de una base de datos a otra modificar: \lab\12-javaconfig-dependency-injection\src\main\java\config\RewardsConfig.java

Diseño de una clase de infraestructura común para abstraer el acceso a los DataSource.

Uso de dos enfoques para pruebas:

  Mediante @SpringBootTest, con carga automática del contexto y uso directo de @Autowired.

  Mediante SpringApplication.run(...), para pruebas más controladas con carga manual del contexto.

Separación clara de configuración para cada entorno de test (mediante application-test.yml, perfiles, etc.).

  >>>>Objetivo de las pruebas
Validar la correcta conexión y configuración de cada base de datos.

Comprobar el comportamiento funcional de las capas de acceso a datos (repositories, mappers, etc.) sobre distintos motores.

Facilitar el desarrollo guiado por pruebas (TDD) con infraestructura realista.

DESCRIPCION MODULO 4: \lab\16-annotations
  Migración de configuración manual a configuración basada en anotaciones:

    Uso de estereotipos de Spring: @Service para lógica de negocio y @Repository para clases de acceso a datos.

    Inyección de dependencias con @Autowired mediante inyección por constructor (preferida) y por setter.

Configuración de escaneo de componentes con @ComponentScan para detectar clases anotadas y registrar beans automáticamente.

Ejecución y verificación de pruebas de integración para comprobar la correcta conexión de los componentes.

Ciclo de vida de beans y callbacks

    Implementación de inicialización con @PostConstruct en JdbcRestaurantRepository para poblar la caché de restaurantes una vez inyectadas las dependencias.

    Implementación de destrucción con @PreDestroy para limpiar la caché al cerrar el contexto de la aplicación.

    Comprobación del orden correcto de ejecución y validación con mensajes en consola.

DESCRIPCION MODULO 6: \lab\22-aop
  Creación de un aspecto de logging (LoggingAspect) para registrar la ejecución de métodos find* en las clases de repositorio:

    Definición de pointcuts con expresiones execution(...) para filtrar métodos específicos.

    Uso de advice @Before para registrar información antes de la ejecución de los métodos interceptados.

    Configuración con @Aspect, @Component y escaneo de componentes limitado al paquete rewards.internal.aspects.

Implementación de un aspecto de monitorización de rendimiento con @Around para medir el tiempo de ejecución de métodos update* en los repositorios:

    Inicio y parada de mediciones con Monitor y MonitorFactory.

    Uso de ProceedingJoinPoint.proceed() para garantizar la ejecución del método real y devolver su valor.

Ajuste de pruebas (RewardNetworkTests) para validar la presencia de las trazas esperadas en consola y comprobar que los aspectos se aplican correctamente.
Creación de un aspecto para manejo centralizado de excepciones (DBExceptionHandlingAspect) con @AfterThrowing, interceptando cualquier excepción lanzada por los repositorios y registrando un mensaje de advertencia.

DESCRIPCION MODULO 7: \lab\24-test
  Mejorar las pruebas de integración utilizando el Spring TestContext framework y los perfiles de Spring.
    Usar Spring TestContext como forma recomendada de realizar system tests en lugar de crear el Test Context manualmente
    Escribir múltiples escenarios de prueba.
    Configurar repositorios alternativos (Stubs o JDBC) mediante perfiles.
    Cambiar fácilmente entre configuraciones de entorno (local vs JNDI).

DESCRIPCION MODULO 8: \lab\26-jdbc
  Simplificar el acceso a datos JDBC usando Spring y su clase JdbcTemplate.
    Reducción de código repetitivo al trabajar con JDBC gracias a JdbcTemplate.
    Diferencias entre RowMapper (fila a objeto) y ResultSetExtractor (result set completo a objeto complejo)
    Varias formas de implementar mapeos: lambdas, method reference, clases anónimas o privadas.
    Beneficios de inyectar JdbcTemplate directamente como dependencia.
    Importancia de las pruebas de integración para validar los cambios en repositorios.
