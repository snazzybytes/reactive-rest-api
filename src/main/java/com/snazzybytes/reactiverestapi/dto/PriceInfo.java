package com.snazzybytes.reactiverestapi.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public class PriceInfo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    private String currency;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal satsPerFiat;

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getSatsPerFiat() {
        return this.satsPerFiat;
    }

    public void setSatsPerFiat(BigDecimal satsPerFiat) {
        this.satsPerFiat = satsPerFiat;
    }

    @Override
    public String toString() {
        return "{" +
                " price='" + getPrice() + "'" +
                ", satsPerFiat='" + getSatsPerFiat() + "'" +
                "}";
    }

}
