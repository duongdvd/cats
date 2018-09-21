package jp.co.willwave.aca.web.form;

import java.util.Objects;

public class RoleForm {
    private Integer id;
    private String name;

    public RoleForm(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof RoleForm) {
            return Objects.equals(((RoleForm) obj).getId(), this.getId());
        }
        return false;
    }
}
