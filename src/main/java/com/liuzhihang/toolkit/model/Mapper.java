package com.liuzhihang.toolkit.model;

import com.intellij.util.xml.*;

import java.util.List;

/**
 * 只需要跳转功能
 *
 * @link https://jetbrains.org/intellij/sdk/docs/reference_guide/frameworks_and_external_apis/xml_dom_api.html
 *
 * @author liuzhihang
 * @date 2020/7/31 19:20
 */
public interface Mapper extends DomElement {

    /**
     * namespace
     *
     * @return
     */
    @Attribute("namespace")
    GenericAttributeValue<String> getNamespace();

    /**
     *
     * 增删改查对应的节点
     *
     * @return
     */
    @SubTagsList({"select", "insert", "update", "delete"})
    List<Statement> getStatements();

    @SubTagList("select")
    List<Select> getSelects();

    @SubTagList("insert")
    List<Insert> getInserts();

    @SubTagList("update")
    List<Update> getUpdates();

    @SubTagList("delete")
    List<Delete> getDeletes();

}
