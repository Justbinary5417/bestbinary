package com.justbinary.config;

import org.springframework.boot.context.properties
    .ConfigurationProperties;
import org.springframework.context.annotation
    .Configuration;

@Configuration
@ConfigurationProperties(prefix = "mpesa")
public class MpesaConfig {

    private String consumerKey;
    private String consumerSecret;
    private String shortcode;
    private String passkey;
    private String callbackUrl;
    private String baseUrl;
    private String b2cShortcode;
    private String b2cInitiatorName;
    private String b2cSecurityCredential;
    private String b2cResultUrl;
    private String b2cQueueUrl;

    // GETTERS
    public String getConsumerKey() {
        return consumerKey;
    }
    public String getConsumerSecret() {
        return consumerSecret;
    }
    public String getShortcode() {
        return shortcode;
    }
    public String getPasskey() {
        return passkey;
    }
    public String getCallbackUrl() {
        return callbackUrl;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
    public String getB2cShortcode() {
        return b2cShortcode;
    }
    public String getB2cInitiatorName() {
        return b2cInitiatorName;
    }
    public String getB2cSecurityCredential() {
        return b2cSecurityCredential;
    }
    public String getB2cResultUrl() {
        return b2cResultUrl;
    }
    public String getB2cQueueUrl() {
        return b2cQueueUrl;
    }

    // SETTERS
    public void setConsumerKey(String v) {
        this.consumerKey = v;
    }
    public void setConsumerSecret(String v) {
        this.consumerSecret = v;
    }
    public void setShortcode(String v) {
        this.shortcode = v;
    }
    public void setPasskey(String v) {
        this.passkey = v;
    }
    public void setCallbackUrl(String v) {
        this.callbackUrl = v;
    }
    public void setBaseUrl(String v) {
        this.baseUrl = v;
    }
    public void setB2cShortcode(String v) {
        this.b2cShortcode = v;
    }
    public void setB2cInitiatorName(String v) {
        this.b2cInitiatorName = v;
    }
    public void setB2cSecurityCredential(String v) {
        this.b2cSecurityCredential = v;
    }
    public void setB2cResultUrl(String v) {
        this.b2cResultUrl = v;
    }
    public void setB2cQueueUrl(String v) {
        this.b2cQueueUrl = v;
    }
}