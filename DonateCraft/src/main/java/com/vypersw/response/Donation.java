package com.vypersw.response;

import java.util.Date;

public class Donation {

    private Long id;
    private Double amount;
    private Date createdDate;
    private long charityId;
    private String charityName;
    private DCPlayer player;
    private DCPlayer paidForBy;

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public long getCharityId() {
        return charityId;
    }

    public void setCharityId(long charityId) {
        this.charityId = charityId;
    }

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public DCPlayer getPlayer() {
        return player;
    }

    public void setPlayer(DCPlayer player) {
        this.player = player;
    }

    public DCPlayer getPaidForBy() {
        return paidForBy;
    }

    public void setPaidForBy(DCPlayer paidForBy) {
        this.paidForBy = paidForBy;
    }
}
