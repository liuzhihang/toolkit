package com.liuzhihang.toolkit.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

/**
 * 只需要跳转功能
 *
 * @author liuzhihang
 * @date 2020/7/31 19:25
 */
public interface Statement extends DomElement {


    /**
     * id 属性
     *
     * @return
     */
    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();

}
