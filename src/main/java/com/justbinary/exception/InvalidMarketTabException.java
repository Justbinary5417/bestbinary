package com.justbinary.exception;

public class InvalidMarketTabException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String attemptedTab;
    private final String[] allowedTabs = {"eo", "ou", "md"};

    public InvalidMarketTabException(String attemptedTab) {
        super(String.format(
            "Invalid market tab: [%s]. " +
            "Allowed tabs are: [eo, ou, md].",
            attemptedTab
        ));
        this.attemptedTab = attemptedTab;
    }

    public String getAttemptedTab() {
        return attemptedTab;
    }

    public String[] getAllowedTabs() {
        return allowedTabs;
    }

    public boolean isNullOrEmpty() {
        return attemptedTab == null || attemptedTab.trim().isEmpty();
    }

    public boolean isSimilarToAllowed() {
        for (String tab : allowedTabs) {
            if (tab.equalsIgnoreCase(attemptedTab)) {
                return true;
            }
        }
        return false;
    }

    public String getToastMessage() {
        return String.format(
            "⚠️ Market tab [%s] is not recognised. " +
            "Please select Even/Odd, Over/Under or Matches/Differs.",
            attemptedTab
        );
    }
}