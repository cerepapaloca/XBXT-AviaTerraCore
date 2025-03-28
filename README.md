# AviaTerraCore
<p>Plugin echó para XBXT</p>
<small>El motivo de su nombre se debe a que esté plugin era para una network que lo cancelaron que
se llamaba AviaTerra luego se quedó el nombre asi</small>

## Características (Resumido)
- **Login:** Tiene un sistema de inicio de sesión y de registros
- **AntiExploit:** Tiene un sistema de eliminación objetos ilegales y anti OP
- **Traducción Dinámica:** Dependiendo del idioma del cliente cambia los mensajes del plugin
- **Api:** Tiene una pequeña api para la página web en la dirección: `https://xbxt.xyz:8443/`
- **Gestor de comandos:** Gestiona los comandos que los usuarios pueden ejecutar y los filtra en el tab

## Como se instala
1. Abre una terminal en el la carpeta `Avia Terra Core`
2. ejecuta esté comando de maven
    ```
    mvn paper-nms:init -f pom.xml 
    ```
    y luego en la carpeta root del proyecto abre un terminal y ejecuta este comando
    ```
    mvn clean package
    ```
    En caso de que no lo tengas instalado. [Aquí](https://maven.apache.org/download.cgi) puedes descargarlo
4. luego se van a crear varios .jar el importante es que se llama `avia-terra-core-0.0-SNAPSHOT.jar`
5. Ese jar lo pasa al a tu carpeta de plugins
6. Luego tiene que poner otros plugin que son dependéncia, Los cuales son
[ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/),
[LuckPerms](https://luckperms.net/) y [Votifier](https://www.spigotmc.org/resources/nuvotifier.13449/).
7. El servidor debe estar en modo offline
8. En server.properties tienen que dejar `server-name=` vacio

A partir de qui no es obligatorio, peró si quieres tener todas funciones tienes que hacer esto
- Crear un bot de discord y ir en la sección de Bot, luego le das click a un botón que se llama `regenerate a new token` o `Reset Token`
copias el Token y lo pegas en `token-bot:` en la config del plugin
  - Luego tienes que añadir las ids de los canales de discord en la config del plugin
- Crear un archivo `keystore.p12`. Este archivo lo tienes que poner en la carpeta del plugin. Al crearlo le tuviste que
poner una contraseña, esa contraseña lo tiene que poner en `password-ssl:` en la config del plugin para que funcione la API web
- Incluir a [placeHolder](https://www.spigotmc.org/resources/placeholderapi.6245/) al servidor
- Crear una base de datos mysql donde tenga los permisos de escritura y de lectura
