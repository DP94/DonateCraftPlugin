package com.vypersw;

import com.vypersw.response.Donation;
import com.vypersw.response.Revival;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageHelperTest {

    private static final String SERVER_URL = "server.url";
    private static final UUID PLAYER_UUID = UUID.randomUUID();

    @Mock
    private Player player;

    @Mock
    private Player.Spigot spigotPlayer;

    @Captor
    ArgumentCaptor<TextComponent> textComponentArgumentCaptor;

    private Revival revival;
    private Donation donation;
    private MessageHelper messageHelper;

    @Before
    public void before() {
        when(player.getName()).thenReturn("Test");
        when(player.spigot()).thenReturn(spigotPlayer);
        when(player.getUniqueId()).thenReturn(PLAYER_UUID);
        messageHelper = new MessageHelper(SERVER_URL);
        revival = new Revival();
        revival.setId(1L);
        donation = new Donation();
        donation.setDonationId(1L);
        donation.setCharityName("Test charity");
        revival.setDonation(donation);
    }

    @Test
    public void testThatDonationBroadcastDoesNotIncludeDonationAmountIfIsPrivate() {
        donation.setAmount(null);
        String result = messageHelper.getDonationMessageFromRevival(player, revival);
        //§ is inserted by ChatColor.COLOR, i.e. ChatColor.WHITE is §f
        String expected = "§6Test§f just donated§f to §6Test charity§f! They will be revived shortly (if they are online)";
        assertEquals(expected, result);
    }

    @Test
    public void testThatDonationBroadcastDoesIncludeDonationAmountIfIsNotPrivate() {
        donation.setAmount(10.50);
        String result = messageHelper.getDonationMessageFromRevival(player, revival);
        //§ is inserted by ChatColor.COLOR, i.e. ChatColor.WHITE is §f
        String expected = "§6Test§f just donated§a £10.50§f to §6Test charity§f! They will be revived shortly (if they are online)";
        assertEquals(expected, result);
    }

    @Test
    public void testThatDonationFormatsCorrectlyIfWholeNumber(){
        donation.setAmount(10.00);
        String result = messageHelper.getDonationMessageFromRevival(player, revival);
        //§ is inserted by ChatColor.COLOR, i.e. ChatColor.WHITE is §f
        String expected = "§6Test§f just donated§a £10.00§f to §6Test charity§f! They will be revived shortly (if they are online)";
        assertEquals(expected, result);
    }

    @Test
    public void testSendDeathURL() {
      messageHelper.sendDeathURL(player);

      verify(player).spigot();
      verify(player).getUniqueId();
      verifyNoMoreInteractions(player);
      verify(spigotPlayer).sendMessage(textComponentArgumentCaptor.capture(), textComponentArgumentCaptor.capture(), textComponentArgumentCaptor.capture());
      verifyNoMoreInteractions(spigotPlayer);

      List<TextComponent> componentList = textComponentArgumentCaptor.getAllValues();
      assertThat(componentList, hasSize(3));

      assertThat(componentList.get(0).getText(), equalTo("You died! Please click "));
      assertThat(componentList.get(1).getText(), equalTo("this link"));
      assertThat(componentList.get(1).isBold(), equalTo(true));
      assertThat(componentList.get(1).isUnderlined(), equalTo(true));
      assertThat(componentList.get(1).getClickEvent().getAction(), equalTo(ClickEvent.Action.OPEN_URL));
      assertThat(componentList.get(1).getClickEvent().getValue(), equalTo(SERVER_URL + "#donate;key=" + PLAYER_UUID));
      assertThat(componentList.get(2).getText(), equalTo(" to donate to a charity to buy back in!"));
    }
}
