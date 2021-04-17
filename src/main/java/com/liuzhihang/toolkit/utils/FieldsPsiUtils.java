package com.liuzhihang.toolkit.utils;

import com.intellij.psi.*;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;
import com.liuzhihang.toolkit.constants.FieldTypeConstant;
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

        Map<String, Object> fieldMap = new LinkedHashMap<>();
        // Map<String, Object> commentFieldMap = new LinkedHashMap<>();

        if (psiClass != null && !psiClass.isEnum() && !psiClass.isInterface() && !psiClass.isAnnotationType()) {
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
                    //reference Type
                    String fieldTypeName = type.getPresentableText();
                    // 指定的类型
                    if (FieldTypeConstant.FIELD_TYPE.containsKey(fieldTypeName)) {
                        fieldMap.put(name, FieldTypeConstant.FIELD_TYPE.get(fieldTypeName));
                    } else if (type instanceof PsiArrayType) {
                        //array type
                        List<Object> list = new ArrayList<>();
                        PsiType deepType = type.getDeepComponentType();
                        String deepTypeName = deepType.getPresentableText();
                        if (deepType instanceof PsiPrimitiveType) {
                            list.add(PsiTypesUtil.getDefaultValue(deepType));
                        } else if (FieldTypeConstant.FIELD_TYPE.containsKey(deepTypeName)) {
                            list.add(FieldTypeConstant.FIELD_TYPE.get(deepTypeName));
                        } else {
                            list.add(getFieldsAndDefaultValue(PsiUtil.resolveClassInType(deepType), null));
                        }
                        fieldMap.put(name, list);
                    } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_COLLECTION)) {
                        // List Set or HashSet
                        List<Object> list = new ArrayList<>();
                        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
                        PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);
                        if (iterableClass != null) {
                            String classTypeName = iterableClass.getName();
                            if (FieldTypeConstant.FIELD_TYPE.containsKey(classTypeName)) {
                                list.add(FieldTypeConstant.FIELD_TYPE.get(classTypeName));
                            } else {
                                list.add(getFieldsAndDefaultValue(iterableClass, null));
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

                        fieldMap.put(name, getFieldsAndDefaultValue(PsiUtil.resolveClassInType(type), null));
                    }
                }
            }
        }
        return fieldMap;
    }

    /**
     * 是否包含指定的注解
     *
     * @param annotations
     * @return
     */
    private static boolean containsAnnotation(@NotNull PsiAnnotation[] annotations) {
        for (PsiAnnotation annotation : annotations) {
            if (FieldTypeConstant.ANNOTATION_TYPES.contains(annotation.getQualifiedName())) {
                return true;
            }
        }
        return false;
    }

}
