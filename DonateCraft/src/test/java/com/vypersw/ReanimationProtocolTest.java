package com.vypersw;

import org.junit.jupiter.api.Disabled;

// Disabled: modern Spigot's PotionEffectType requires a running Bukkit registry to load,
// and MockBukkit does not yet publish a build compatible with spigot-api 26.2.
// Re-enable and rewrite against MockBukkit (or an equivalent test harness) once one is
// released for this API version.
@Disabled("Awaiting MockBukkit release for Spigot 26.2")
public class ReanimationProtocolTest {
}
