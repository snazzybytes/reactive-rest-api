package com.snazzybytes.reactiverestapi.dto.coinbase;

public class PriceData {
    private String base;
    private String currency;
    private String amount;

    public String getBase() {
        return this.base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "{" +
                " base='" + getBase() + "'" +
                ", currency='" + getCurrency() + "'" +
                ", amount='" + getAmount() + "'" +
                "}";
    }

}