package com.liuzhihang.toolkit.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 23.12.11
 * Time: 23:43
 */
public interface MapperIdentifiableStatement extends DomElement, WithIncludes {

    @NameValue
    @Attribute("id")
    GenericAttributeValue<String> getId();

}
