package com.worksplit.auth.server.userconfig;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "role")
public class Role extends  BaseId{

    String name ;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "permission_role" , joinColumns = {
            @JoinColumn(name = "role_id" , referencedColumnName = "id")} , inverseJoinColumns = {
            @JoinColumn(name = "permission_id" , referencedColumnName = "id")}
    )
    List<Permission> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
