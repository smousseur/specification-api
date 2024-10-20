package com.smousseur.specification.api.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.util.ReflectionUtils;

/** The type Reflection util. */
public final class Utils {
  private Utils() {
    // nothing to do
  }

  /**
   * Call field getter object.
   *
   * @param <T> the type parameter
   * @param field the field
   * @param object the object
   * @return the object
   */
  public static <T> Object callFieldGetter(Field field, T object) {
    String fieldName = field.getName();
    Class<?> searchRequestClass = object.getClass();
    Method getter = getGetterMethod(searchRequestClass, fieldName);
    return ReflectionUtils.invokeMethod(getter, object);
  }

  private static Method getGetterMethod(Class<?> objectClass, String fieldName) {
    String getterName = fieldName;
    Method getter = org.springframework.util.ReflectionUtils.findMethod(objectClass, getterName);
    if (getter == null) {
      getterName = getGetterName("get", fieldName);
      getter = org.springframework.util.ReflectionUtils.findMethod(objectClass, getterName);
    }
    if (getter == null) {
      getterName = getGetterName("is", fieldName);
      getter = org.springframework.util.ReflectionUtils.findMethod(objectClass, getterName);
    }
    if (getter == null) {
      throw new IllegalArgumentException();
    }
    return getter;
  }

  private static String getGetterName(String get, String fieldName) {
    return get + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
  }
}
