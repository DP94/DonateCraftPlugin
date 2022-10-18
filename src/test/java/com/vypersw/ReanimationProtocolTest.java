package com.vypersw;

import com.vypersw.network.HttpHelper;
import com.vypersw.response.Revival;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

    @Mock
    private MessageHelper messageHelper;

    private ReanimationProtocol reanimationProtocol;
    private UUID uuid;

    private PotionEffect glowing;
    private PotionEffect regen;
    private PotionEffect heal;
    private PotionEffect damageResistance;

    @Before
    public void before() {
        reanimationProtocol = new ReanimationProtocol(server, messageHelper, httpHelper);
        uuid = UUID.randomUUID();
        when(server.getLogger()).thenReturn(logger);
        when(server.getPlayer(uuid)).thenReturn(player);
        glowing = new PotionEffect(PotionEffectType.GLOWING, 200, 100, true, true);
        regen = new PotionEffect(PotionEffectType.REGENERATION, 200, 10, true, true);
        heal = new PotionEffect(PotionEffectType.HEAL, 200, 10, true, true);
        damageResistance = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 10, true, true);
    }

    @Test
    public void testThatPlayerWhoIsInSurvivalModeIsNotRevived() {
        when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player, never()).setGameMode(GameMode.SURVIVAL);
    }

    @Test
    public void testThatAlivePlayerIsNotRevived() {
        when(player.isDead()).thenReturn(false);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player, never()).setGameMode(GameMode.SURVIVAL);
    }

    @Test
    public void testThatOnlineAndDeadPlayerIsRevived() {
        Revival revival = new Revival();
        revival.setKey(uuid.toString());
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player).setGameMode(GameMode.SURVIVAL);
        verify(world).strikeLightningEffect(player.getLocation());
        verify(httpHelper).fireAsyncPostRequestToServer("/revived", revival);
    }

    @Test
    public void testThatRevivedPlayerIsTeleportedToSpawnIfNoBedSet() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player).teleport(world.getSpawnLocation());
    }

    @Test
    public void testThatRevivedPlayerIsTeleportedToBeIfIsSet() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        Location bedLocation = new Location(world, 0, 0, 0);
        when(player.getBedSpawnLocation()).thenReturn(bedLocation);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(player).teleport(bedLocation);
    }

    @Test
    public void testThatPlayerWhoIsntDeadButIsEligibleForRevivalSendsRequestToServerToDeleteLock() {
        Revival revival = new Revival();
        revival.setKey(uuid.toString());
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(false);
        reanimationProtocol.reanimatePlayer(uuid);
        verify(httpHelper).fireAsyncPostRequestToServer("/revived", revival);
    }

    @Test
    public void testThatPlayerWhoIsRevivedHasPotionsApplied() {
        when(player.isOnline()).thenReturn(true);
        when(player.isDead()).thenReturn(true);
        when(player.getWorld()).thenReturn(world);
        reanimationProtocol.reanimatePlayer(uuid);
        runPotionVerification();
    }

    @Test
    public void testThatAddRespawnEffectsAppliesCorrectPotions() {
        reanimationProtocol.addRespawnPotionEffects(player);
        runPotionVerification();
    }

    private void runPotionVerification() {
        verify(player).addPotionEffect(glowing);
        verify(player).addPotionEffect(regen);
        verify(player).addPotionEffect(heal);
        verify(player).addPotionEffect(damageResistance);
    }
}
