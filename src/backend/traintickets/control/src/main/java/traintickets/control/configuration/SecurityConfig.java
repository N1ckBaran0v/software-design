package traintickets.control.configuration;

public final class SecurityConfig {
    private String secret;
    private int expiration;
    private String userRole;
    private String carrierRole;
    private String adminRole;

    public int getExpiration() {
        return expiration;
    }

    public void setExpiration(int expiration) {
        this.expiration = expiration;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getCarrierRole() {
        return carrierRole;
    }

    public void setCarrierRole(String carrierRole) {
        this.carrierRole = carrierRole;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }
}
