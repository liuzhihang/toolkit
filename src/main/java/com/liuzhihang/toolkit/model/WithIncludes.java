package com.liuzhihang.toolkit.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mishchenko
 * Date: 26.05.12
 * Time: 15:47
 */
public interface WithIncludes extends DomElement {

    @SubTagList("include")
    List<Include> getIncludes();

}
