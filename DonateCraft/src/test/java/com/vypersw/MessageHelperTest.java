package com.vypersw;

import com.vypersw.response.Donation;
import com.vypersw.response.Revival;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageHelperTest {

    @Mock
    private Player player;

    private Revival revival;
    private Donation donation;
    private MessageHelper messageHelper;

    @Before
    public void before() {
        when(player.getName()).thenReturn("Test");
        messageHelper = new MessageHelper();
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
        String expected = "§6Test§f just donated§a £10.5§f to §6Test charity§f! They will be revived shortly (if they are online)";
        assertEquals(expected, result);
    }
}
