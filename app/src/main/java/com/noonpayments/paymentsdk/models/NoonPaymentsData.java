package com.noonpayments.paymentsdk.models;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class NoonPaymentsData {

    private String applicationId = "";
    private String applicationKey = "";
    private String businessId = "";
    private String merchantId = "";
    private String paymentCategory = "";
    private String paymentChannel = "";
    private PaymentMode paymentMode = PaymentMode.LIVE;
    private String paymentType = "Authorize";
    private String styleProfile = "";
    private boolean allowCardTokenization = false;
    private String authorizationHeader = "";

    private float amount;
    private String currency;
    private String publicKey;
    private String orderId;

    public String getDisplayAmount(Language language)
    {
        NumberFormat nf= NumberFormat.getInstance(new Locale("en","UK"));
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#,##0.00");
        if (language == Language.ARABIC)
            return currency + " " + df.format(amount);
        else
            return currency + " " + df.format(amount);
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(String paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public void setPaymentChannel(String paymentChannel) {
        this.paymentChannel = paymentChannel;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isAllowCardTokenization() {
        return allowCardTokenization;
    }

    public void setAllowCardTokenization(boolean allowCardTokenization) {
        this.allowCardTokenization = allowCardTokenization;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

}
