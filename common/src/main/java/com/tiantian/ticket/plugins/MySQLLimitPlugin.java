package com.tiantian.ticket.plugins;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/***
* @Description:
* @Author: maobing
* @Date: 2018/4/11
*/
public class MySQLLimitPlugin extends PluginAdapter {

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        // addfield, getter, setter for limit clause
        addLimit(topLevelClass, introspectedTable, "offset");
        addLimit(topLevelClass, introspectedTable, "size");
        return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
                                                                     IntrospectedTable introspectedTable) {
        //LIMIT 5,10; // 检索记录行 6-15
        XmlElement isNotNullElement = new XmlElement("if");//$NON-NLS-1$
        isNotNullElement.addAttribute(new Attribute("test", "offset != null and offset >=0"));//$NON-NLS-1$ //$NON-NLS-2$
        isNotNullElement.addElement(new TextElement("limit ${offset} , ${size}"));
        element.addElement(isNotNullElement);
        // LIMIT 5;//检索前 5个记录行
        return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
    }

    private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(FullyQualifiedJavaType.getIntInstance());
        field.setName(name);
        field.setInitializationString("-1");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);

        char c = name.charAt(0);
        String camel = Character.toUpperCase(c) + name.substring(1);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("set" + camel);
        method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
        method.addBodyLine("this." + name + "=" + name + ";");

        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.setName("get" + camel);
        method.addBodyLine("return " + name + ";");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    /**
     * This plugin is always valid -no properties are required
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
