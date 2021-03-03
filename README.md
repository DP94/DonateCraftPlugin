# DonateCraft

DonateCraft is a Minecraft plugin aimed at hardcore servers to allow players to donate in order to be reborn on death. 

This repository stores 3 projects.
DonateCraft - Java 11 Minecraft 1.16.5 Spigot Plugin
DonateCraftNG - Frontend Angular
DonateCraftNode - Express Node JS Backend

# Developer Setup

1. Clone the git repository
2. Sign up to JustGiving API - https://developer.justgiving.com
3. Ensure you have MySQL server installed (version 5+).
4. Ensure you have a Minecraft Spigot 1.16.5 server. https://www.spigotmc.org/
5. Ensure you have JDK/JRE 11
6. Ensure the server uses Java 11

## Donate Craft (Minecraft Spigot Plugin)

1. Import the plugin into IntelliJ
2. Setup the project to use Java 11
3. Go to File > Project Structure > Artifacts
4. Create a new Jar from modules with dependencies.
5. Use the default settings
6. Ensure you have Maven: org.json.json in the output layout.
7. Change the output directory to your /plugins/ directory of the server.
8. Apply changes.
9. Click Build > Build Artifacts > Build
10. Verify plugin was enabled on the server successfully via /reload or start.

ALT: Build Minecraft plugin (look out for the path in pom.xml) and drag into plugins folder

## DonateCraftNG (Frontend)

1. Run `npm install` in the DonateCraftNG directory.
2. Modify src/environments/environment.ts replacing "redacted" with your JustGiving API key.
3. Run `npm start`
4. Go to localhost:4200 to assert it is live.
5. Additional steps: Modify envrionment.prod.ts to contain required values
6. Run ng build --prod to generate all minified HTML & Javascript files; drag these into the "build/public" folder in the DonateCraftNode project to allow the node server to serve the Angular application

## DonateCraftNode (Backend API)

1. Create a user and password in MySQL for the backend.
2. Ensure .env is updated with the username and password of the MySQL database.
3. Insert the JustGiving API key into the .env file
4. Run `npm install` in the DonateCraft directory.
5. Run `npm start`
6. The backend is now started and can be called from the frontend/plugin.
