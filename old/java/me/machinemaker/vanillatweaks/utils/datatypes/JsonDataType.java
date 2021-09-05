package me.machinemaker.vanillatweaks.utils.datatypes;

import com.google.gson.Gson;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Lets you store arbitrary data in PDC.
 *
 * @param <T>
 */
public class JsonDataType<T> implements PersistentDataType<String, T> {

    private static final Gson gson = new Gson();
    private final Class<T> typeClass;

    public JsonDataType(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<T> getComplexType() {
        return typeClass;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull T complex, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(complex);
    }

    @NotNull
    @Override
    public T fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return gson.fromJson(primitive, getComplexType());
    }
}
