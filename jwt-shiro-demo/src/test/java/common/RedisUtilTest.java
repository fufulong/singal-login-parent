package common;

import com.SingalLoginApplication;
import com.common.RedisUtil;
import com.sys.domain.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * redisUtil工具类的测试类
 */
@SpringBootTest(classes = SingalLoginApplication.class)
@RunWith(SpringRunner.class)
public class RedisUtilTest {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ValueOperations<String,Object> valueOperations;
    @Autowired
    private HashOperations<String,String, Object> hashOperations;

    /**
     * valueOpereation 添加键值对
     */
    @Test
    public void test1() {
        SysUser u = new SysUser();
        u.setId(1);
        u.setName("付福龙");
        u.setAge(26);
        u.setSex(1);
        redisUtil.addStringValue("test1:p5", u,10L, TimeUnit.SECONDS);

        System.out.println("--------------------");
    }

    @Test
    public void getStringvalue(){
        Object o = valueOperations.get("test1:p5");
        System.out.println(o);

    }

    @Test
    public void setHashmap(){
        HashMap<String, Object> map = new HashMap<>();
        SysUser u = new SysUser();
        u.setId(1);
        u.setName("付福龙");
        u.setAge(26);
        u.setSex(1);

        map.put("hash1",u);
        map.put("hash2",u);
        redisUtil.addHashvalue("hashtest",map);
    }

    @Test
    public void getHashvalue(){

        Object o = hashOperations.get("hashtest", "hash1");
        System.out.println(((SysUser)o).getName());
    }


}
