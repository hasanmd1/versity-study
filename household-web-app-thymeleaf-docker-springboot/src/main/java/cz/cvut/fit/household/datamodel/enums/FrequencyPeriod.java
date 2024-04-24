package cz.cvut.fit.household.datamodel.enums;

public enum FrequencyPeriod {
    DAILY("daily"),
    WEEKLY("weekly"),
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String type;

    FrequencyPeriod(String type) {
        this.type = type;
    }

    public String getFrequencyPeriod() {
        return type;
    }

}
