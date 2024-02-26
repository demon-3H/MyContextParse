package org.example.config;

import lombok.Data;
import org.example.entity.Role;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 角色配置类
 *
 * @author guoshuai
 */
@Configuration
@Data
public class RoleConfig {
    //筛选业务角色
    private Role role1;

    //解析业务的角色
    private Role role2;
    private Role role3;
    private Role role4;
    private Role role5;

    /**
     * 初始化角色
     *
     * @throws IOException
     */
    public void init() throws IOException {
        role1=new Role("role1.txt");
        role2=new Role("role2.txt");
        role3=new Role("role3.txt");
        role4=new Role("role4.txt");
        role5=new Role("role5.txt");
    }
}
