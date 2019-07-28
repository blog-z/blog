package com.user.vo;

import java.util.List;

public class UserSecurityVo {
    private Integer id;
    private String name;
    private String password;
    private String role;

    public UserSecurityVo() {
    }

    public UserSecurityVo(Integer id, String name, String password,String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role=role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
