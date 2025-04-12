package traintickets.ui.javalin;

import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public final class GsonMapper implements JsonMapper {
    private final Gson gson = new Gson();

    @Override
    public void writeToOutputStream(@NotNull Stream<?> stream, @NotNull OutputStream outputStream) {
        try (var writer = new OutputStreamWriter(outputStream)) {
            stream.forEach(elem -> gson.toJson(elem, writer));
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    @NotNull
    @Override
    public String toJsonString(@NotNull Object obj, @NotNull Type type) {
        return gson.toJson(obj, type);
    }

    @NotNull
    @Override
    public InputStream toJsonStream(@NotNull Object obj, @NotNull Type type) {
        return new ByteArrayInputStream(gson.toJson(obj, type).getBytes(StandardCharsets.UTF_8));
    }

    @NotNull
    @Override
    public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
        try (var jsonReader = new InputStreamReader(json)) {
            return gson.fromJson(jsonReader, targetType);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @NotNull
    @Override
    public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
        return gson.fromJson(json, targetType);
    }
}
