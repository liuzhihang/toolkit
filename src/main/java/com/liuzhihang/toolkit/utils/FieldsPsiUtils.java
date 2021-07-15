package com.liuzhihang.toolkit.utils;

import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.liuzhihang.toolkit.constants.Constants;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 参数处理工具
 *
 * @author liuzhihang
 * @date 2020/11/17 15:15
 */
public class FieldsPsiUtils {

    @NotNull
    public static Map<String, Object> getFieldsAndDefaultValue(PsiClass psiClass, PsiType[] genericArr) {

        return getFieldsAndDefaultValue(psiClass, genericArr, new LinkedList<>());
    }

    /**
     * @param psiClass
     * @param genericArr
     * @param qualifiedNameList 根节点到当前节点的链表
     * @return
     */
    public static Map<String, Object> getFieldsAndDefaultValue(PsiClass psiClass, PsiType[] genericArr, LinkedList<String> qualifiedNameList) {

        Map<String, Object> fieldMap = new LinkedHashMap<>();

        if (psiClass == null || psiClass.isEnum() || psiClass.isInterface() || psiClass.isAnnotationType()) {
            return fieldMap;
        }

        // 设置当前类的类型
        qualifiedNameList.add(psiClass.getQualifiedName());


        for (PsiField field : psiClass.getAllFields()) {

            if (field.getModifierList() != null && field.getModifierList().hasModifierProperty(PsiModifier.STATIC)) {
                continue;
            }

            PsiType type = field.getType();
            String name = field.getName();
            // 判断注解 javax.annotation.Resource   org.springframework.beans.factory.annotation.Autowired
            PsiAnnotation[] annotations = field.getAnnotations();
            if (annotations.length > 0 && containsAnnotation(annotations)) {
                fieldMap.put(name, "");
            } else if (type instanceof PsiPrimitiveType) {
                // 基本类型
                fieldMap.put(name, PsiTypesUtil.getDefaultValue(type));
            } else {
                // 引用类型
                String fieldTypeName = type.getPresentableText();
                // 指定的类型
                if (Constants.FIELD_TYPE.containsKey(fieldTypeName)) {
                    fieldMap.put(name, Constants.FIELD_TYPE.get(fieldTypeName));
                } else if (type instanceof PsiArrayType) {
                    // 数组类型
                    List<Object> list = new ArrayList<>();
                    PsiType deepType = type.getDeepComponentType();
                    if (deepType instanceof PsiPrimitiveType) {
                        list.add(PsiTypesUtil.getDefaultValue(deepType));
                    } else if (Constants.FIELD_TYPE.containsKey(deepType.getPresentableText())) {
                        list.add(Constants.FIELD_TYPE.get(deepType.getPresentableText()));
                    } else {
                        // 参数类型为对象 校验是否递归
                        PsiClass classInType = PsiUtil.resolveClassInType(deepType);

                        LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                        if (classInType != null && hasContainQualifiedName(temp, classInType.getQualifiedName())) {
                            list.add("Object for " + classInType.getName());
                        } else {
                            list.add(getFieldsAndDefaultValue(classInType, null, temp));
                        }
                    }
                    fieldMap.put(name, list);
                } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_COLLECTION)) {
                    // List Set or HashSet
                    List<Object> list = new ArrayList<>();
                    PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                    PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                    if (iterableClass != null) {
                        String classTypeName = iterableClass.getName();
                        if (Constants.FIELD_TYPE.containsKey(classTypeName)) {
                            list.add(Constants.FIELD_TYPE.get(classTypeName));
                        } else {

                            // 参数类型为对象 校验是否递归
                            LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                            if (hasContainQualifiedName(temp, iterableClass.getQualifiedName())) {
                                list.add("Object for " + iterableClass.getName());
                            } else {
                                list.add(getFieldsAndDefaultValue(iterableClass, null, temp));
                            }

                        }
                    }
                    fieldMap.put(name, list);
                } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_MAP)) {
                    // HashMap or Map
                    fieldMap.put(name, new HashMap<>(4));
                } else if (psiClass.isEnum() || psiClass.isInterface() || psiClass.isAnnotationType()) {
                    // enum or interface
                    fieldMap.put(name, "");
                } else {

                    if (type.getPresentableText().equals("T") && genericArr != null && genericArr.length >= 1) {
                        // T 泛型
                        type = genericArr[0];
                    } else if (type.getPresentableText().equals("K") && genericArr != null && genericArr.length >= 2) {
                        // K 泛型
                        type = genericArr[1];
                    }

                    if (Constants.FIELD_TYPE.containsKey(type.getPresentableText())) {
                        fieldMap.put(name, Constants.FIELD_TYPE.get(type.getPresentableText()));
                    } else {

                        // 参数类型为对象 校验是否递归
                        PsiClass classInType = PsiUtil.resolveClassInType(type);

                        LinkedList<String> temp = new LinkedList<>(qualifiedNameList);
                        if (classInType != null && hasContainQualifiedName(temp, classInType.getQualifiedName())) {
                            fieldMap.put(name, "Object for " + classInType.getName());
                        } else {
                            fieldMap.put(name, getFieldsAndDefaultValue(PsiUtil.resolveClassInType(type), null, temp));
                        }


                    }


                }
            }

        }
        return fieldMap;
    }

    private static boolean hasContainQualifiedName(LinkedList<String> qualifiedNameList, String qualifiedName) {

        if (qualifiedNameList.isEmpty()) {
            return false;
        }

        for (String s : qualifiedNameList) {
            if (s.equals(qualifiedName)) {
                return true;
            }
        }

        return false;

    }


    /**
     * 是否包含指定的注解
     *
     * @param annotations
     * @return
     */
    private static boolean containsAnnotation(@NotNull PsiAnnotation[] annotations) {
        for (PsiAnnotation annotation : annotations) {
            if (Constants.ANNOTATION_TYPES.contains(annotation.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }

}
