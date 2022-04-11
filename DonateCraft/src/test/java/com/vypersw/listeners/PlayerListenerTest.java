package com.vypersw.listeners;

import com.vypersw.MessageHelper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.Death;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlayerListenerTest {

  private static final String DEATH_MESSAGE = "pricked to death";
  private static final String PLAYER_NAME = "Thornyfy";
  private static final UUID PLAYER_UUID = UUID.randomUUID();
  private static final Location PLAYER_LOCATION = new Location(null, 0.1, 0.2, 0.3);

  @Mock
  private Player player;

  @Mock
  private HttpHelper httpHelper;

  @Mock
  private MessageHelper messageHelper;

  @Mock
  private Server server;

  @Mock
  private Logger serverLogger;

  @Mock
  private ItemFactory serverItemFactory;

  @Mock
  private SkullMeta skullMeta;

  @Mock
  private World world;

  @Captor
  ArgumentCaptor<Death> deathArgumentCaptor;

  @Captor
  ArgumentCaptor<Runnable> runnableArgumentCaptor;

  @Captor
  ArgumentCaptor<ItemStack> itemStackArgumentCaptor;

  private PlayerListener playerListener;

  @Before
  public void before() {
    lenient().when(player.getUniqueId()).thenReturn(PLAYER_UUID);
    lenient().when(player.getName()).thenReturn(PLAYER_NAME);
    lenient().when(player.getWorld()).thenReturn(world);
    lenient().when(player.getLocation()).thenReturn(PLAYER_LOCATION);
    playerListener = new PlayerListener(messageHelper, httpHelper);

    lenient().when(server.getLogger()).thenReturn(serverLogger);
    lenient().when(server.getItemFactory()).thenReturn(serverItemFactory);
    lenient().when(serverItemFactory.getItemMeta(Material.PLAYER_HEAD)).thenReturn(skullMeta);

    // Right now this works as it is the only test that requires Bukkit internals.
    // If future tests require this we will need a more robust solution.
    if (Bukkit.getServer() == null) {
      Bukkit.setServer(server);
    }
  }


  @Test
  public void testOnPlayerDeath() {
    playerListener.onPlayerDeath(new PlayerDeathEvent(player, Collections.emptyList(), 0, DEATH_MESSAGE));

    verify(httpHelper).fireAsyncPostRequestToServer(eq("/lock"), deathArgumentCaptor.capture(), runnableArgumentCaptor.capture());
    verifyNoMoreInteractions(httpHelper);
    Runnable runnable = runnableArgumentCaptor.getValue();
    runnable.run();

    Death death = deathArgumentCaptor.getValue();
    assertThat(death.getReason(), equalTo(DEATH_MESSAGE));
    assertThat(death.getName(), equalTo(PLAYER_NAME));
    assertThat(death.getUuid(), equalTo(PLAYER_UUID));

    verify(messageHelper).sendDeathURL(player);
    verifyNoMoreInteractions(messageHelper);

    verify(player).getUniqueId();
    verify(player).getName();
    verify(player).getWorld();
    verify(player).getLocation();
    verifyNoMoreInteractions(player);

    verify(world).dropItem(eq(PLAYER_LOCATION), itemStackArgumentCaptor.capture());
    verifyNoMoreInteractions(world);

    ItemStack skull = itemStackArgumentCaptor.getValue();
    assertThat(skull.getAmount(), equalTo(1));
    assertThat(skull.getType(), equalTo(Material.PLAYER_HEAD));
    assertThat(skull.getItemMeta(), equalTo(skullMeta));
    verify(skullMeta).setLore(Collections.singletonList(DEATH_MESSAGE));
    verify(skullMeta).setOwningPlayer(player);
  }

  @Test
  public void testOnLoginAlivePlayer() {
    when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
    playerListener.onLogin(new PlayerJoinEvent(player, null));

    verifyNoInteractions(messageHelper);
    verify(player).getGameMode();
    verifyNoMoreInteractions(player);
  }

  @Test
  public void testOnLoginDeadPlayer() {
    when(player.getGameMode()).thenReturn(GameMode.SPECTATOR);
    playerListener.onLogin(new PlayerJoinEvent(player, null));

    verify(messageHelper).sendDeathURL(player);
    verifyNoMoreInteractions(messageHelper);
    verify(player).getGameMode();
    verifyNoMoreInteractions(player);
  }

}
