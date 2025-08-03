FALTA DESCRIPCION DEL PRIMER MODULO


EN EL SEGUNDO MODULO DEL PROYECTO SE HA CREADO LA RAMA MODULO2 QUE CONTIENE LOS CAMBIOS QUE SE HAN TRABAJADO EN EL DIRECTORIO \lab\12-javaconfig-dependency-injection. 
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
