package traintickets.ui.javalin;

import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.util.stream.Stream;

public final class GsonMapper implements JsonMapper {
    private final Gson gson = new Gson();

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T fromJsonString(@NotNull String json, @NotNull Type targetType) {
        var result = (T) gson.fromJson(json, targetType);
        if (result == null) {
            throw new JsonMapperException("Cannot parse empty body");
        }
        return result;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type targetType) {
        try (var reader = new BufferedReader(new InputStreamReader(json))) {
            var result = (T) gson.fromJson(reader, targetType);
            if (result == null) {
                throw new JsonMapperException("Cannot parse empty body");
            }
            return result;
        } catch (IOException e) {
            throw new JsonMapperException(e);
        }
    }

    @NotNull
    @Override
    public InputStream toJsonStream(@NotNull Object obj, @NotNull Type type) {
        var json = gson.toJson(obj);
        return new ByteArrayInputStream(json.getBytes());
    }

    @NotNull
    @Override
    public String toJsonString(@NotNull Object obj, @NotNull Type type) {
        return gson.toJson(obj);
    }

    @Override
    public void writeToOutputStream(@NotNull Stream<?> stream, @NotNull OutputStream outputStream) {
        try (var writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            stream.forEach(obj -> gson.toJson(obj, writer));
        } catch (IOException e) {
            throw new JsonMapperException(e);
        }
    }
}
