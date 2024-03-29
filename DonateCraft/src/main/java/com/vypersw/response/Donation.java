package com.vypersw.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties({"paidForId", "playerId"})
public class Donation {

    private Long id;
    private Double amount;
    private Date createdDate;
    private long charityId;
    private String charityName;
    private DCPlayer paidForBy;
    private boolean isPrivate;

    public long getId() {
        return id;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
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

    public DCPlayer getPaidForBy() {
        return paidForBy;
    }

    public void setPaidForBy(DCPlayer paidForBy) {
        this.paidForBy = paidForBy;
    }
}
