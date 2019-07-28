package com.user.config;

import com.user.entity.User;
import com.user.vo.UserSecurityVo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SecurityUserDetails extends User implements UserDetails {

    private String myUsername;

    private Collection<? extends GrantedAuthority> authorities;

    private static final long serialVersionUID = 1L;

    public SecurityUserDetails(User user) {
        if(user!=null) {
            this.setUserId(user.getUserId());
            this.setUserName(user.getUserName());
            this.setUserPassword(user.getUserPassword());
            this.setUserRole(user.getUserRole());
        }
    }

    public SecurityUserDetails(String username,Collection<? extends GrantedAuthority> authorities){
        this.myUsername=username;
        this.authorities=authorities;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getMyUsername() {
        return myUsername;
    }



    @Override
    public String getUsername() {
        return this.getUsername();
    }

    @Override
    public String getPassword() {
        return this.getPassword();
    }

    /**
     * 帐号是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /**
     * 是否锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    /**
     * 密码是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /**
     * 是否禁用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return false;
    }
}
