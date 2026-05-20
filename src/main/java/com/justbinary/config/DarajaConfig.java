package com.justbinary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "daraja")
public class DarajaConfig {

    private String consumerKey;
    private String consumerSecret;
    private String shortcode;
    private String tillNumber;
    private String transactionType;
    private String passkey;
    private String callbackUrl;
    private String baseUrl;
    private double usdToKes;

    public String getConsumerKey() { return consumerKey; }
    public void setConsumerKey(String consumerKey) { this.consumerKey = consumerKey; }
    public String getConsumerSecret() { return consumerSecret; }
    public void setConsumerSecret(String consumerSecret) { this.consumerSecret = consumerSecret; }
    public String getShortcode() { return shortcode; }
    public void setShortcode(String shortcode) { this.shortcode = shortcode; }
    public String getTillNumber() { return tillNumber; }
    public void setTillNumber(String tillNumber) { this.tillNumber = tillNumber; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getPasskey() { return passkey; }
    public void setPasskey(String passkey) { this.passkey = passkey; }
    public String getCallbackUrl() { return callbackUrl; }
    public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public double getUsdToKes() { return usdToKes; }
    public void setUsdToKes(double usdToKes) { this.usdToKes = usdToKes; }
}