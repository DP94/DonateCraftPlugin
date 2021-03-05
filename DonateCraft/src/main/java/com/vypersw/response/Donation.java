package com.vypersw.response;

import java.util.Date;

public class Donation {

    private Long id;
    private Double amount;
    private Date date;
    private long charity;
    private String charityName;
    private boolean isPrivate;

    public long getId() {
        return id;
    }

    public boolean isPrivate() {
        return amount == null && id != null;
    }

    public void setId(Long id) {
        this.id = id;
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
