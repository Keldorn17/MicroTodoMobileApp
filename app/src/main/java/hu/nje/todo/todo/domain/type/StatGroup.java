package hu.nje.todo.todo.domain.type;

public enum StatGroup {
    NONE(""),
    PRIORITY("priority");

    private final String apiValue;

    StatGroup(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }
}
