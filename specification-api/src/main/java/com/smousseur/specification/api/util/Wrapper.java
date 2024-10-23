package com.smousseur.specification.api.util;

/**
 * The type Wrapper.
 *
 * @param <T> the type parameter
 */
public class Wrapper<T> {
  /** The Value. */
  private T value;

  public Wrapper() {}

  public Wrapper(T value) {
    this.value = value;
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public boolean isEmpty() {
    return this.value == null;
  }
}
