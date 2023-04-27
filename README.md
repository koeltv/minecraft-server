# Minecraft Server Web Interface

This web application is meant to be a secure way
to interact with and run a minecraft server through a web interface.

## How to use

You first need to create your minecraft server folder.
To do that, go to the [Forge Website](https://files.minecraftforge.net/net/minecraftforge/forge/)
and download the `Installer` of the version you want

Launch the `Installer`, select `Install server` and direct it to `<THIS_PROJECT_PATH>/server`

Then you can simply launch the application with gradle
using `USER=<your_user> ROOT_PASSWORD=<your_password> ./gradlew run` (or you can set the environment variables
beforehand).  
You can also run it using either with java using `java -jar minecraft-server-all.jar` or with docker
using `docker-compose up`

You can then access the interface via your navigator (port 80 by default)

From here, start your minecraft server and pass it commands if you want

## Currently implemented

- Login page
- Start & Stop server with minimalist feedback
- Show console output

## To-do
- Handle commands
- Allow server configuration (server.properties)
- Pass mods & config via local files
- Pass mods via online repository
- Import forge file (and cache ?)

## Resource

- [Minecraft Banner by iwen56](https://www.deviantart.com/iwen56/art/Banniere-minecraft-368139531)
- [Minecraft favicon created by Aldo Cervantes - Flaticon](https://www.flaticon.com/free-icons/minecraft)
- [Login Title font by Allison James](https://www.fontspace.com/minecraft-evenings-font-f17735)