package jp.co.willwave.aca.model.enums;

public enum UserRole {
    NONE(0), SYSTEM_ADMIN(1), COMPANY_DIRECTOR(2), DIVISION_DIRECTOR(3), OPERATOR(4), VIEWER(5), DEVICES(6);
    private Integer role;

    UserRole(Integer role) {
        this.role = role;
    }

    public static UserRole getUserRole(Integer role) {
        if (SYSTEM_ADMIN.getRole().equals(role)) {
            return SYSTEM_ADMIN;
        }

        if (COMPANY_DIRECTOR.getRole().equals(role)) {
            return COMPANY_DIRECTOR;
        }

        if (DIVISION_DIRECTOR.getRole().equals(role)) {
            return DIVISION_DIRECTOR;
        }

        if (OPERATOR.getRole().equals(role)) {
            return OPERATOR;
        }

        if (VIEWER.getRole().equals(role)) {
            return VIEWER;
        }

        return NONE;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public boolean isSystemAdmin() {
        return UserRole.SYSTEM_ADMIN.role.equals(this.role);
    }

    public boolean isCompanyDirector() {
        return UserRole.COMPANY_DIRECTOR.role.equals(this.role);
    }

    public boolean isDivisionDirector() {
        return UserRole.DIVISION_DIRECTOR.role.equals(this.role);
    }

    public boolean isOperator() {
        return UserRole.OPERATOR.role.equals(this.role);
    }

    public boolean isViewer() {
        return UserRole.VIEWER.role.equals(this.role);
    }

    public boolean isDevices() {
        return UserRole.DEVICES.role.equals(this.role);
    }

    public boolean eq(UserRole userRole) {
        return (this.role != null && this.role.equals(userRole.getRole()));
    }
}
