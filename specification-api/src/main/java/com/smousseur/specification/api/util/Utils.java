package com.smousseur.specification.api.util;

import com.smousseur.specification.api.exception.SpecificationException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.springframework.util.ReflectionUtils;

/** The type Utils. */
public final class Utils {
  private Utils() {
    // nothing to do
  }

  /**
   * Replace last string.
   *
   * @param str the str
   * @param target the old char
   * @param replacement the new char
   * @return the string
   */
  public static String replaceLast(String str, String target, String replacement) {
    return str.replaceFirst("(?s)(.*)" + target, "$1" + replacement);
  }

  /**
   * Wrap string.
   *
   * @param value the value
   * @param wrapper the wrapper
   * @return the string
   */
  public static String wrap(String value, String wrapper) {
    return addPrefixAndSuffix(value, wrapper, wrapper);
  }

  /**
   * Add prefix and suffix string.
   *
   * @param value the value
   * @param prefix the prefix
   * @param suffix the suffix
   * @return the string
   */
  public static String addPrefixAndSuffix(String value, String prefix, String suffix) {
    return prefix + value + suffix;
  }

  /**
   * Call field getter object.
   *
   * @param <T> the type parameter
   * @param field the field
   * @param object the object
   * @return the object
   */
  public static <T> Object callGetterForField(Field field, T object) {
    String fieldName = field.getName();
    Class<?> searchRequestClass = object.getClass();
    Method getter = getGetterMethod(searchRequestClass, fieldName);
    return ReflectionUtils.invokeMethod(getter, object);
  }

  private static Method getGetterMethod(Class<?> objectClass, String fieldName) {
    String getterName = fieldName;
    Method getter = ReflectionUtils.findMethod(objectClass, getterName);
    if (getter == null) {
      getterName = getGetterName("get", fieldName);
      getter = ReflectionUtils.findMethod(objectClass, getterName);
    }
    if (getter == null) {
      getterName = getGetterName("is", fieldName);
      getter = ReflectionUtils.findMethod(objectClass, getterName);
    }
    if (getter == null) {
      throw new SpecificationException(
          String.format(
              "Cannot get getter method for field %s and class %s",
              fieldName, objectClass.getName()));
    }
    return getter;
  }

  private static String getGetterName(String getterMethodPrefix, String fieldName) {
    return getterMethodPrefix + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
  }
}
