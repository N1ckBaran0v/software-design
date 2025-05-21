package traintickets.control.configuration;

import java.util.Map;

public final class DatabaseConfig {
    private String type;
    private Map<String, String> params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
