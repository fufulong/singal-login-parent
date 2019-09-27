package com.sys.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.sys.domain.SysUser;
import com.sys.dao.SysUserMapper;
import com.sys.service.SysUserService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ffl
 * @since 2019-09-19
 */
@Service
@Transactional
public class SysUserImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public Set<String> selectPermisssions(Integer userid) {
        List<String> permissions = sysUserMapper.selectPerissions(userid);

        if (permissions != null && ! permissions.isEmpty()){
            Set<String> result = permissions.stream().distinct().collect(Collectors.toSet());
            return result;
        }

        return null;
    }

    /**
     * 根据用户的名称查询用户
     * @param userName
     * @return
     */
    @Override
    public SysUser selectByUserName(String userName) {
        if (StringUtils.isEmpty(userName)){
            System.out.println("用户名不能是空");
            return null;
        }
        EntityWrapper<SysUser> wrapper = new EntityWrapper<>();
        wrapper.eq("name",userName);
        SysUser user = this.selectOne(wrapper);
        if (user == null){
            System.out.println("userName = " + userName + "的用户不存在");
        }
        return user;
    }

    /**
     *根据 用户名 和算法最后的密码值,查询用户
     * @param name: 用户名
     * @param passwod: 已经把原始输入的密码经过前端加密,再经过后端 HAS-256 算法循环加密 10次后的密码字符串
     * @return 查找到的用户
     */
    @Override
    public SysUser selectByNameAndPassword(String name, String passwod) {

        EntityWrapper<SysUser> wrapper = new EntityWrapper<SysUser>();
        wrapper.eq("name",name).eq("passWord",passwod);
        SysUser user = this.selectOne(wrapper);
        if (user == null){
            System.out.println("不存在 name = " + name + ",passWord = " + passwod + "的用户");
        }
        return user;
    }

    @Override
    public List<SysUser> selectAll() {
        this.selectList(null);

        return this.selectList(null);
    }

}
