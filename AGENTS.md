# AGENTS.md

Guidance for AI coding agents working in this repository.

## Repository overview

DonateCraft is a Minecraft plugin for hardcore servers: players donate (via
JustGiving) to revive dead players. The README describes three sub-projects
(a Spigot plugin, an Angular frontend, and a Node/Express backend), but
**only the Spigot plugin currently lives in this repository**, under
[`DonateCraft/`](DonateCraft). The frontend (`DonateCraftNG`) and backend
(`DonateCraftNode`) described in the README do not exist here — do not
assume their code is present or attempt to edit files that aren't there.

All work in this repo happens inside `DonateCraft/`, a single-module Maven
project.

## Build, test, and run

Run all commands from `DonateCraft/` (or pass `--file DonateCraft/pom.xml`
from the repo root, as CI does).

```bash
cd DonateCraft
mvn package        # compiles, runs tests, and shades the plugin jar
mvn test           # runs the JUnit test suite only
```

- Requires **JDK 17** (`maven-compiler-plugin` targets 17; CI in
  [`.github/workflows/maven.yml`](.github/workflows/maven.yml) uses JDK 17).
  Note the plugin's own `plugin.yml` still declares `api-version: 1.16` and
  the top-level README references Java 11 — these are stale; trust the
  `pom.xml`/CI config over the README for toolchain requirements.
- The `maven-jar-plugin` is configured with a hardcoded local
  `outputDirectory` (`D:\Java\Spigot server\plugins`) — this is a developer
  machine path, not something to "fix" generically; leave it unless asked.
- The shaded jar (with `org.json` and Jackson bundled) is produced at
  `DonateCraft/target/DonateCraft-<version>-shaded.jar` and is what gets
  dropped into a Spigot server's `plugins/` directory.
- There is no linter/formatter configured — match the existing code style
  (see below) rather than introducing a new one.

## Runtime dependencies

The plugin talks to an external backend and frontend over HTTP, configured
in [`src/main/resources/config.yml`](DonateCraft/src/main/resources/config.yml)
(`backendUrl`, `frontendUrl`). It does not embed those services — they must
be running separately for end-to-end behavior, but are irrelevant to
compiling/testing the plugin itself.

## Code structure

```
DonateCraft/src/main/java/com/vypersw/
  DonateCraft.java              # JavaPlugin entry point (onEnable wiring)
  MessageHelper.java            # player-facing messaging
  ReanimationProtocol.java      # scheduled task driving the revival flow
  RevivalResponse.java
  command/DonateCraftCommands.java
  listeners/PlayerListener.java # Bukkit event listeners (e.g. death)
  network/HttpHelper.java       # async HTTP client wrapping java.net.http
  response/                     # POJOs mapped from backend JSON (Jackson)
```

Tests mirror this package layout under `src/test/java/com/vypersw/`.

## Conventions

- Standard Java packages under `com.vypersw`; new classes should follow the
  existing sub-package split (`command`, `listeners`, `network`, `response`).
- JSON (de)serialization uses Jackson `ObjectMapper` with
  `PropertyNamingStrategy.UPPER_CAMEL_CASE` to match the backend's JSON
  casing — reuse this when adding new request/response types rather than
  introducing a different mapper configuration.
- Networking is async via `java.net.http.HttpClient` / `sendAsync`
  (`HttpHelper`), not synchronous calls — follow this pattern for any new
  outbound requests so plugin ticks aren't blocked.
- Tests use JUnit 4 (`org.junit`) with Mockito (`@RunWith(MockitoJUnitRunner.class)`)
  and Hamcrest matchers — keep new tests consistent with this stack (not
  JUnit 5).
- `target/` is a build output directory and should never be edited by hand
  or committed to.

## Verifying changes

Since this is a Bukkit/Spigot plugin, most logic can and should be covered
by unit tests (`mvn test`) rather than requiring a live Minecraft server.
When a change is testable in isolation (helpers, protocol logic, response
parsing), add/extend JUnit tests alongside the existing ones rather than
requiring manual in-game verification.
