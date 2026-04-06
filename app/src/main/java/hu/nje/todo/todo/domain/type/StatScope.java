package hu.nje.todo.todo.domain.type;

public enum StatScope {
    OWN("OWN"),
    SHARED("SHARED"),
    ALL("ALL");

    private final String apiValue;

    StatScope(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }
}


