package com.liuzhihang.toolkit.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * Created with IntelliJ IDEA.
 * User: mishchenko
 * Date: 26.05.12
 * Time: 16:20
 */
public interface WithResultMap extends DomElement {

    @Attribute("resultMap")
    GenericAttributeValue<ResultMap> getResultMap();

}
