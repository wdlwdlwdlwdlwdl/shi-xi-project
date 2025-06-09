package com.aliyun.gts.gmall.platform.trade.persistence.generate;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 用于自动生成mybatis相关代码  跟tradecenter自身业务无关  即使删除也无影响
 */
public class MySQLCommentGenerator extends EmptyCommentGenerator {

    private final Properties properties;

    public MySQLCommentGenerator() {
        properties = new Properties();
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        // 获取自定义的 properties
        this.properties.putAll(properties);
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // 获取表注释
        String remarks = introspectedTable.getRemarks();
        topLevelClass.addJavaDocLine("");
        topLevelClass.addJavaDocLine("import io.swagger.annotations.ApiModel;");
        topLevelClass.addJavaDocLine("import io.swagger.annotations.ApiModelProperty;");
        topLevelClass.addJavaDocLine("import lombok.Data;");
        topLevelClass.addJavaDocLine("@ApiModel(\"" + remarks + "\")");
        topLevelClass.addJavaDocLine("@Data");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        // 获取列注释
        String remarks = introspectedColumn.getRemarks();
        remarks = StringUtils.replaceAll(remarks, "\n" , "");
        remarks = StringUtils.replaceAll(remarks, "\r" , "");
        System.out.println(remarks);
        field.addJavaDocLine("@ApiModelProperty(\"" + remarks + "\")");

    }
}
