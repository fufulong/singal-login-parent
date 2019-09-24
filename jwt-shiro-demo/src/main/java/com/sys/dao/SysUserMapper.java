package com.sys.dao;

import com.sys.domain.SysUser;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ffl
 * @since 2019-09-19
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户的id 查找这个用户的全部的权限信息
     * @param userId: 要查找的用户的id
     * @return 这个用户的权限信息
     */
    public List<String> selectPerissions(@Param(value = "userId") Integer userId);

}
