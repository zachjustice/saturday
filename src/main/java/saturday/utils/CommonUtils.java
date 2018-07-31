package saturday.utils;

import saturday.exceptions.ResourceNotFoundException;

import java.util.function.Supplier;

public class CommonUtils {

    public static <T> T getOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (ResourceNotFoundException e) {
            return defaultValue;
        }
    }
}
