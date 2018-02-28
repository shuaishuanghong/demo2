package com.example.demo2;

import com.example.demo2.model.Permission;
import com.example.demo2.model.Role;
import com.example.demo2.model.User;
import com.example.demo2.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AuthRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user= (User) principalCollection.fromRealm(this.getClass().getName()).iterator().next();
        List<String> permissionList=new ArrayList<>();
        List<String> roleList=new ArrayList<>();
        Set<Role>  roleSet=user.getRoles();
        if(CollectionUtils.isNotEmpty(roleSet)){
            for (Role role:roleSet){
                roleList.add(role.getRname());
                Set<Permission> permissionSet=role.getPermissions();
                if(CollectionUtils.isNotEmpty(permissionSet)){
                    for(Permission permission:permissionSet) {
                        permissionList.add(permission.getName());
                    }
                }
            }
        }
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        info.addStringPermissions(permissionList);
        info.addRoles(roleList);
        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken upToken= (UsernamePasswordToken) authenticationToken;
        String username =upToken.getUsername();
        User user=userService.findByUsername(username);
        SimpleAuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo(user,user.getPassword(),this.getClass().getName());
        return  authenticationInfo;

    }
}
