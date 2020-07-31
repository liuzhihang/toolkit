package com.liuzhihang.toolkit.provider;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import com.intellij.util.xml.GenericAttributeValue;
import com.liuzhihang.toolkit.model.Mapper;
import com.liuzhihang.toolkit.model.Statement;
import com.liuzhihang.toolkit.utils.IconUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 行标记和跳转符号
 *
 * @author liuzhihang
 * @date 2019/4/30 15:33
 */
public class Java2MapperLineMarkerProvider extends RelatedItemLineMarkerProvider {


    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {

        //element 需要是 PsiNameIdentifierOwner 且为接口
        if (element instanceof PsiNameIdentifierOwner && isElementWithinInterface(element)) {

            List<PsiElement> resultList = process(element);
            if (!resultList.isEmpty()) {
                //构建导航图标的builder
                NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
                        .create(IconUtils.NAVIGATE_TO_XML)
                        .setAlignment(GutterIconRenderer.Alignment.CENTER)
                        .setTargets(resultList)
                        .setTooltipTitle("Navigation to target in mapper xml");
                result.add(builder.createLineMarkerInfo(Objects.requireNonNull(((PsiNameIdentifierOwner) element).getNameIdentifier())));
            }
        }

    }

    /**
     * 处理元素
     *
     * @return
     */
    private List<PsiElement> process(PsiElement element) {

        List<PsiElement> elementList = new ArrayList<>();

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

                    for (Statement statement : mapper.getStatements()) {
                        String namespace = mapper.getNamespace().getStringValue();
                        String xmlDomElementId = statement.getId().getRawText();
                        if (Objects.equals(qualifiedName, namespace) && methodName.equals(xmlDomElementId)) {
                            elementList.add(statement.getXmlElement());
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

                String namespace = mapper.getNamespace().getStringValue();
                if (Objects.equals(qualifiedName, namespace)) {
                    elementList.add(mapper.getXmlElement());
                }
            }
        }
        return elementList;
    }

    /**
     * 是否为接口
     */
    private boolean isElementWithinInterface(PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return Optional.ofNullable(type).isPresent() && type.isInterface();
    }
}
