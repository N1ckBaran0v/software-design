package traintickets.control.configuration;

public final class SecurityConfig {
    private String secret;
    private int expiration;
    private RoleConfig userRole;
    private RoleConfig carrierRole;
    private RoleConfig adminRole;
    private RoleConfig systemRole;

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

    public RoleConfig getUserRole() {
        return userRole;
    }

    public void setUserRole(RoleConfig userRole) {
        this.userRole = userRole;
    }

    public RoleConfig getCarrierRole() {
        return carrierRole;
    }

    public void setCarrierRole(RoleConfig carrierRole) {
        this.carrierRole = carrierRole;
    }

    public RoleConfig getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(RoleConfig adminRole) {
        this.adminRole = adminRole;
    }

    public RoleConfig getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(RoleConfig systemRole) {
        this.systemRole = systemRole;
    }
}
