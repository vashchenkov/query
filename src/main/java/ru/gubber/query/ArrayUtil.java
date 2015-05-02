package ru.gubber.query;

import org.hibernate.type.Type;

/**
 *
 */
public class ArrayUtil {
    public static Object[] merge(Object[] arr1, Object[] arr2) {
        Object[] result = new Object[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i];
        }
        for (int i = 0; i < arr2.length; i++) {
            result[arr1.length + i] = arr2[i];
        }

        return result;
    }

    public static Type[] merge(Type[] arr1, Type[] arr2) {
        Type[] result = new Type[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i++) {
            result[i] = arr1[i];
        }
        for (int i = arr1.length; i < arr1.length + arr2.length; i++) {
            result[i] = arr2[i];
        }

        return result;
    }

    /**
     * Создаёт массив из n копий объекта Type.
     * @param count число копий
     * @param type объект Type
     * @return Type[n]
     */
    public static Type[] nCopies(int count, Type type) {
        Type[] types = new Type[count];
        for (int i = 0; i < count; ++i) {
            types[i] = type;
        }

        return types;
    }
}
