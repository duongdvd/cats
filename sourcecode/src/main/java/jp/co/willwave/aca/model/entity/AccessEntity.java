package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.model.enums.PathType;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "access")
public class AccessEntity extends BaseEntity {
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "method", nullable = false)
    private Integer method;

    @Column(name = "path_type", nullable = false)
    private Integer pathType;

    public Integer getRoleId() { return roleId; }

    public void setRoleId(Integer roleId) { this.roleId = roleId; }

    public String getPath() { return path; }

    public void setPath(String path) { this.path = path; }

    public Integer getMethod() { return method; }

    public void setMethod(Integer method) { this.method = method; }

    public Integer getPathType() { return pathType; }

    public void setPathType(Integer pathType) { this.pathType = pathType; }

    public boolean checkAccess(String pathCheck, Integer methodCheck) {
        if (pathCheck == null || methodCheck == null) return false;

        if (PathType.MATCH.getPathType() == pathType) {
            return method == methodCheck && path.equalsIgnoreCase(pathCheck);
        } else {
            return method == methodCheck && pathCheck.matches(path);
        }
    }
}
