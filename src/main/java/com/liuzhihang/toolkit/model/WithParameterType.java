package com.liuzhihang.toolkit.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

public interface WithParameterType extends DomElement {

    @NameValue
    @Attribute("parameterType")
    GenericAttributeValue<String> getParameterType();
}
