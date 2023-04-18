package com.noonpayments.paymentsdk.models;

import java.io.Serializable;

public class NoonPaymentsCard implements Serializable {
    private String cardType = "";
    private String cardNumber = "";
    private String cardToken = "";

    public NoonPaymentsCard(String cardType, String cardNumber, String cardToken) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.cardToken = cardToken;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }
}
