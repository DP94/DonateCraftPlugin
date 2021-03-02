package com.vypersw.response;

import java.util.Date;

public class Donation {

    private long id;
    private Long donationId;
    private Double amount;
    private Date date;
    private long charity;
    private String charityName;
    private boolean isPrivate;

    public long getId() {
        return id;
    }

    public boolean isPrivate() {
        return amount == null && donationId != null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(long donationId) {
        this.donationId = donationId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
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
