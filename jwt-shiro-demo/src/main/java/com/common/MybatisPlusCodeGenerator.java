package com.common;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Mybatis-plus的代码生成器
 * @author ffl
 * @since 2019-9-19
 */
public class MybatisPlusCodeGenerator {

    private static final String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    private static final String DATASOURCE_URL = "jdbc:mysql://localhost:3306/demo?allowMultiQueries=true" +
            "&useUnicode=true&characterEncoding=UTF-8&useSSL=true&autoReconnect=true" +
            "&pinGlobalTxToPhysicalConnection=true&serverTimezone=Asia/Shanghai";

    private static final String USER_NAME = "root";

    private static final String PASSWORD = "root";

    private static final String PROJECT_PATH = System.getProperty("user.dir") + "/jwt-shiro-demo";

    private static String scanner(String tip){
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入 " + tip +" :");
        while(scanner.hasNext()){
            String result = scanner.next();
            if (!StringUtils.isEmpty(result)){
                return result;
            }
        }

        throw new RuntimeException("请输入正确的" + tip +" !");
    }

    /**
     * 代码生成器主函数
     * @param args 参数
     */
    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator();
        //1.数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriverName(DRIVER_CLASS_NAME);
        dataSourceConfig.setUrl(DATASOURCE_URL);
        dataSourceConfig.setUsername(USER_NAME);
        dataSourceConfig.setPassword(PASSWORD);
        dataSourceConfig.setDbType(DbType.MYSQL);
        generator.setDataSource(dataSourceConfig);

        //2.设置全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(PROJECT_PATH + "/src/main/java");
        globalConfig.setOpen(false);
        globalConfig.setIdType(IdType.AUTO);
        globalConfig.setAuthor("ffl");
        globalConfig.setBaseResultMap(true);
        globalConfig.setEnableCache(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setServiceName("%sService");
        globalConfig.setServiceImplName("%sImpl");
        globalConfig.setControllerName("%sController");
        generator.setGlobalConfig(globalConfig);

        //3.设置包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com");
        packageConfig.setModuleName(scanner("模块名"));
        packageConfig.setEntity("domain");
        packageConfig.setMapper("dao");
        packageConfig.setService("service");
        packageConfig.setServiceImpl("service.impl");
        packageConfig.setController("controller");
        generator.setPackageInfo(packageConfig);

        //4.设置策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setControllerMappingHyphenStyle(true);
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setRestControllerStyle(true);
        strategyConfig.setInclude(scanner("表名,多个表名中间用,隔开").split(","));
        generator.setStrategy(strategyConfig);

        //5.设置自定义设置
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                System.out.println("这里可以做点什么");
            }
        };
        ArrayList<FileOutConfig> fileOutConfigs = new ArrayList<>();
        //自定义 mapper.xml文件的输出位置,自定义xxxMapper.xml文件的自定义输出配置会优先于全局配置里面的xml文件的输出配置
        FileOutConfig mapperConfig = new FileOutConfig("/templates/mapper.xml.ftl") {
            //entity的名称如果有前后缀,mapper文件会跟着变化,返回值是 xml文件的输出路径
            @Override
            public String outputFile(TableInfo tableInfo) {
                String path = PROJECT_PATH + "/src/main/resources/mapper/" + packageConfig.getModuleName()
                        + "/" + tableInfo.getEntityName().replace("Entity","")
                        + "Mapper.xml" ;
                return path;
            }
        };

        fileOutConfigs.add(mapperConfig);
        injectionConfig.setFileOutConfigList(fileOutConfigs);
        generator.setCfg(injectionConfig);

        //6. 设置模板引擎配置
        generator.setTemplate(new TemplateConfig().setXml("/templates/mapper.xml"));
        generator.setTemplateEngine(new FreemarkerTemplateEngine());
        generator.execute();

    }
}
