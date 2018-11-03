package com.worksplit.auth.server.userconfig;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "permission")
public class Permission extends  BaseId{

    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
