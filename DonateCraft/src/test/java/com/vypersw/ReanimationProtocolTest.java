package com.vypersw;

import com.vypersw.network.HttpHelper;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReanimationProtocolTest {

    @Mock
    private Server server;

    @Mock
    private Logger logger;

    @Mock
    private Player player;

    @Mock
    private World world;

    @Mock
    private HttpHelper httpHelper;

    private ReanimationProtocol reanimationProtocol;

    @Before
    public void before() {
        reanimationProtocol = new ReanimationProtocol(server, "", httpHelper);
        when(server.getLogger()).thenReturn(logger);
    }

    @Test
    public void testThatPlayerWhoIsInSurvivalModeIsNotRevived() {
        UUID uuid = UUID.randomUUID();
        when(server.getPlayer(uuid)).thenReturn(player);
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player, never()).setGameMode(GameMode.SURVIVAL);
    }

    @Test
    public void testThatAlivePlayerIsNotRevived() {
        UUID uuid = UUID.randomUUID();
        when(server.getPlayer(uuid)).thenReturn(player);
        when(player.isDead()).thenReturn(false);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player, never()).setGameMode(GameMode.SURVIVAL);
    }

    @Test
    public void testThatOnlineAndDeadPlayerIsRevived() {
        UUID uuid = UUID.randomUUID();
        when(server.getPlayer(uuid)).thenReturn(player);
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player).setGameMode(GameMode.SURVIVAL);
        verify(world).strikeLightningEffect(player.getLocation());
    }

    @Test
    public void testThatRevivedPlayerIsTeleportedToSpawnIfNoBedSet() {
        UUID uuid = UUID.randomUUID();
        when(server.getPlayer(uuid)).thenReturn(player);
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player).teleport(world.getSpawnLocation());
    }

    @Test
    public void testThatRevivedPlayerIsTeleportedToBeIfIsSet() {
        UUID uuid = UUID.randomUUID();
        when(server.getPlayer(uuid)).thenReturn(player);
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        Location bedLocation = new Location(world, 0, 0, 0);
        when(player.getBedSpawnLocation()).thenReturn(bedLocation);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player).teleport(bedLocation);
    }

}
