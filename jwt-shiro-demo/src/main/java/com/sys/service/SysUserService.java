package com.sys.service;

import com.sys.domain.SysUser;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ffl
 * @since 2019-09-19
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 根据用户id查找用户的权限集
     * @param userid
     * @return
     */
    public Set<String> selectPermisssions(Integer userid);

    public SysUser selectByUserName(String userName);

    public SysUser selectByNameAndPassword(String name,String passwod);

}
