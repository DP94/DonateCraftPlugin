package com.vypersw.listeners;

import com.vypersw.MessageHelper;
import com.vypersw.network.HttpHelper;
import com.vypersw.response.Death;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlayerListenerTest {

  private static final String DEATH_MESSAGE = "pricked to death";
  private static final String PLAYER_NAME = "Thornyfy";
  private static final UUID PLAYER_UUID = UUID.randomUUID();

  @Mock
  private Player player;

  @Mock
  private HttpHelper httpHelper;

  @Mock
  private MessageHelper messageHelper;

  @Captor
  ArgumentCaptor<Death> deathArgumentCaptor;

  @Captor
  ArgumentCaptor<Runnable> runnableArgumentCaptor;

  private PlayerListener playerListener;

  @Before
  public void before() {
    when(player.getUniqueId()).thenReturn(PLAYER_UUID);
    when(player.getName()).thenReturn(PLAYER_NAME);
    playerListener = new PlayerListener(messageHelper, httpHelper);
  }


  @Test
  public void testOnPlayerDeath() {
    playerListener.onPlayerDeath(new PlayerDeathEvent(player, Collections.emptyList(), 0, DEATH_MESSAGE));

    verify(httpHelper).fireAsyncPostRequestToServer(eq("/lock"), deathArgumentCaptor.capture(), runnableArgumentCaptor.capture());
    verifyNoMoreInteractions(httpHelper);
    Runnable runnable = runnableArgumentCaptor.getValue();
    runnable.run();

    Death death = deathArgumentCaptor.getValue();
    assertThat(death.getLastDeathReason(), equalTo(DEATH_MESSAGE));
    assertThat(death.getName(), equalTo(PLAYER_NAME));
    assertThat(death.getUuid(), equalTo(PLAYER_UUID));

    verify(messageHelper).sendDeathURL(player);
    verifyNoMoreInteractions(messageHelper);
  }

  @Test
  public void testOnLoginAlivePlayer() {
    when(player.getGameMode()).thenReturn(GameMode.SURVIVAL);
    playerListener.onLogin(new PlayerJoinEvent(player, null));

    verifyZeroInteractions(messageHelper);
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
