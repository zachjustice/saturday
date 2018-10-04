package saturday.utils;

import saturday.exceptions.ResourceNotFoundException;

import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public class CommonUtils {

    public static <T> T coalesce(T a) {
        return a;
    }

    public static <T> T coalesce(T a, T b) {
        return a == null ? b : a;
    }

    public static <T> T coalesce(T a, T b, T c) {
        return a != null ? a : (b != null ? b : c);
    }

    public static <T> T coalesce(T ...items) {
        for(T i : items) if(i != null) return i;
        return null;
    }

    public static <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (ResourceNotFoundException e) {
            return defaultValue;
        }
    }
}
