package com.vypersw.response;

import java.util.Date;

public class Donation {

    private long id;
    private long donationId;
    private double amount;
    private Date date;
    private long charity;
    private String charityName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;fail
    }

    public long getDonationId() {
        return donationId;
    }

    public void setDonationId(long donationId) {
        this.donationId = donationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getCharity() {
        return charity;
    }

    public void setCharity(long charity) {
        this.charity = charity;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }
}
