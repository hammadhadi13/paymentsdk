package com.noonpayments.paymentsdk.models;

import java.io.Serializable;

public class NoonPaymentsResponse implements Serializable {
    String status;
    String message;
    String transactionId;
    NoonPaymentsCard tokenizedCard;
    String response;

    public NoonPaymentsResponse() {
    }

    public void setDetails(String status, String message, String transactionId,  String response) {
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
        this.response = response;
        tokenizedCard = null;
    }

    public void setDetails(String status, String message, String transactionId,  String response,
                           String cardNumber, String cardType, String cardToken) {
        this.status = status;
        this.message = message;
        this.transactionId = transactionId;
        this.response = response;
        tokenizedCard = new NoonPaymentsCard(cardType, cardNumber,cardToken);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }


    public NoonPaymentsCard getTokenizedCard() {
        return tokenizedCard;
    }

    public void setTokenizedCard(NoonPaymentsCard tokenizedCard) {
        this.tokenizedCard = tokenizedCard;
    }

}
