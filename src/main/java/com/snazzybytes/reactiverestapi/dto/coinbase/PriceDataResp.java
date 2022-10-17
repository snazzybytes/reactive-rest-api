package com.snazzybytes.reactiverestapi.dto.coinbase;

/**
 * Coinbase's PriceData API model
 */
public class PriceDataResp {

    private PriceData data;

    public PriceData getData() {
        return this.data;
    }

    public void setData(PriceData data) {
        this.data = data;
    }
}
