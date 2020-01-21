package com.liuzhihang.toolkit.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeParameterListOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomService;
import com.liuzhihang.toolkit.model.Mapper;
import com.liuzhihang.toolkit.model.MapperIdentifiableStatement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * @author liuzhihang
 * @date 2019/4/30 16:55
 */
public class MapperDefinitionSearch extends QueryExecutorBase<XmlElement, PsiElement> {

    public MapperDefinitionSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull PsiElement element, @NotNull Processor<? super XmlElement> consumer) {

        if (element instanceof PsiTypeParameterListOwner) {
            Processor<DomElement> processor = domElement -> consumer.process(domElement.getXmlElement());
            if (element instanceof PsiMethod) {
                PsiMethod psiMethod = (PsiMethod) element;
                PsiClass psiClass = psiMethod.getContainingClass();
                if (null != psiClass) {
                    // 当前项目
                    Project project = psiMethod.getProject();
                    // 当前项目的所有元素 mapper
                    List<DomFileElement<Mapper>> fileElements = DomService.getInstance().getFileElements(Mapper.class, project, GlobalSearchScope.allScope(project));
                    // 判断全类名和Xml标签中的id是否相等, 相等则添加到processor
                    String qualifiedName = psiClass.getQualifiedName();
                    String methodName = psiMethod.getName();

                    for (DomFileElement<Mapper> mapperDomFileElement : fileElements) {
                        Mapper mapper = mapperDomFileElement.getRootElement();
                        for (MapperIdentifiableStatement statement : mapper.getIdentifiableStatements()) {
                            String namespace = mapper.getNamespace().getStringValue();
                            String xmlDomElementId = statement.getId().getRawText();
                            if (Objects.equals(qualifiedName, namespace) && methodName.equals(xmlDomElementId)) {
                                processor.process(statement);
                            }
                        }
                    }
                }
            } else if (element instanceof PsiClass) {
                PsiClass psiClass = (PsiClass) element;
                // 当前项目
                Project project = psiClass.getProject();
                // 当前项目的所有元素 mapper
                List<DomFileElement<Mapper>> fileElements = DomService.getInstance().getFileElements(Mapper.class, project, GlobalSearchScope.allScope(project));
                // 只需要判断namespace
                String qualifiedName = psiClass.getQualifiedName();

                for (DomFileElement<Mapper> mapperDomFileElement : fileElements) {
                    Mapper mapper = mapperDomFileElement.getRootElement();
                    for (MapperIdentifiableStatement statement : mapper.getIdentifiableStatements()) {
                        String namespace = mapper.getNamespace().getStringValue();
                        if (Objects.equals(qualifiedName, namespace)) {
                            processor.process(statement);
                        }
                    }
                }
            }
        }


    }
}
