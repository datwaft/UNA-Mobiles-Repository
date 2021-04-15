# Guía de utilización

Buenas, este es mi proyecto de la materia de diseño y programación de dispositivos móviles. Este proyecto está formado por dos partes, una parte web y una parte de servidor; la idea es en un futuro añadir una parte móvil.

## Servidor

El servidor fue programado en Java 15, la última versión de Java hasta el momento, por lo que el servidor que se va a utilizar debe poder soportar esa versión de Java.

### Requerimientos

- El JDK de Java 15 instalado
- Un servidor web ([Tomcat](http://tomcat.apache.org/), por ejemplo) con soporte para Java 15.
- [Gradle 7.0](https://gradle.org/) o superior.
- Postgresql

### Guía de despliegue

Instrucciones para el despliegue del servidor:

1. Descomprimir el repositorio el alguna carpeta.
2. Entrar a la carpeta `/database/` y ejecutar el script `setup.bat`, se recomienda ejecutarlo desde una terminal usando powershell.
  ![image](https://user-images.githubusercontent.com/37723586/114942498-dc1d5d80-9e01-11eb-83a8-8c90ddd56337.png)
  2.1. Al ejecutar el script se va a generar una base de datos llamada `airline` cuyo usuario dueño es `postgres`.  
  **Importante:** El script asume que la base de datos se va a montar con el usuario llamado `postgres` y la contraseña diciendo `losiram01`, esto se puede cambiar dentro del script.
3. Entrar a la carpeta `/server/` y abrir una terminal (se recomienda usar powershell), luego ejecutar el comando `gradle war`, el cual va a generar un archivo war dentro de `/server/build/libs/` llamado `server-0.1.0.war`.
  ![image](https://user-images.githubusercontent.com/37723586/114943134-c0668700-9e02-11eb-9a4c-43b6da1dc482.png)
4. Cambiar de nombre al archivo war y nombrarlo `server.war`.
5. Iniciar el servicio del servidor web (Tomcat en este caso)
  ![image](https://user-images.githubusercontent.com/37723586/114943282-fefc4180-9e02-11eb-8bbd-c93bd1c79afd.png)
6. Abrir la herramienta de despliegue del servidor web, en mi caso es la página web en la url http://localhost:8099/manager/.
  ![image](https://user-images.githubusercontent.com/37723586/114943536-5bf7f780-9e03-11eb-80ca-9cde9ccd0e03.png)
7. Desplegar el war del paso 3 y 4.
  ![image](https://user-images.githubusercontent.com/37723586/114943572-6d410400-9e03-11eb-82e5-6c92c553ecea.png)

Luego de esto el servidor va a estar hosteado en el puerto del servidor en la url `http://localhost:<puerto>/server`.

## Web

La interfaz web está programada utilizando VueJS en NodeJS, más especificamente utilizando VueCLI, VueRouter y Vuex junto con PrimeVue.

### Requerimientos

- NodeJS versión 15.11.0 o superior

### Guía de despliegue

1. Descomprimir el repositorio en alguna carpeta, esto solamente es necesario la primera vez.
2. Entrar a la carpeta `/web/` y ejecutar el comando `npm install` desde la terminal (se recomienda usar powershell), esto solamente es necesario la primera vez.
  ![image](https://user-images.githubusercontent.com/37723586/114945149-0ec95500-9e06-11eb-8b2c-70276f5005ca.png)
3. Ejecutar el comando `npm run serve` desde la terminal (se recomienda usar powershell).
  ![image](https://user-images.githubusercontent.com/37723586/114945446-91eaab00-9e06-11eb-8b37-29e6bba5384f.png)

Luego de esto el servidor web va a estar hosteado en la url que indica la terminal, en mi caso http://localhost:8080/.

![image](https://user-images.githubusercontent.com/37723586/114945489-ab8bf280-9e06-11eb-9d61-4c15711d4820.png)

