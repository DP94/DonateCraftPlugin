package com.vypersw.command;

import com.vypersw.MessageHelper;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DonateCraftCommandsTest {

  @Mock
  private CommandSender consoleCommandSender;

  @Mock
  private Player playerCommandSender;

  @Mock
  private MessageHelper messageHelper;

  private DonateCraftCommands donateCraftCommands;

  @Before
  public void before() {
    donateCraftCommands = new DonateCraftCommands(messageHelper);
  }


  @Test
  public void testOnCommandNotAPlayer() {
    donateCraftCommands.onCommand(consoleCommandSender, null, null, null);

    verifyNoInteractions(messageHelper);
    verify(consoleCommandSender).sendMessage("Command must be used in game");
    verifyNoMoreInteractions(consoleCommandSender);
  }

  @Test
  public void testOnCommandOnAlivePlayer() {
    when(playerCommandSender.getGameMode()).thenReturn(GameMode.SURVIVAL);
    donateCraftCommands.onCommand(playerCommandSender, null, null, null);

    verifyNoInteractions(messageHelper);
    verify(playerCommandSender).sendMessage("You seem to be alive to me? You must be dead to get your revival URL.");
    verify(playerCommandSender).getGameMode();
    verifyNoMoreInteractions(playerCommandSender);
  }

  @Test
  public void testOnCommandOnDeadPlayer() {
    when(playerCommandSender.getGameMode()).thenReturn(GameMode.SPECTATOR);
    donateCraftCommands.onCommand(playerCommandSender, null, null, null);

    verify(messageHelper).sendDeathURL(playerCommandSender);
    verifyNoMoreInteractions(messageHelper);
    verify(playerCommandSender).getGameMode();
    verifyNoMoreInteractions(playerCommandSender);
  }
}
