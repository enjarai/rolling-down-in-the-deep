package dev.enjarai.rollingdowninthedeep.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.enjarai.rollingdowninthedeep.RollingDownInTheDeep;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// copied from https://github.com/enjarai/cicada-lib/blob/37c55143cf53c35a198ec9fdff79550a52511afa/src/main/java/nl/enjarai/cicada/api/util/AbstractModConfig.java
// licensed under the MIT license
public abstract class AbstractModConfig {
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting() // Makes the json use new lines instead of being a "one-liner"
            .serializeNulls() // Makes fields with `null` value to be written as well.
            .disableHtmlEscaping() // We'll be able to use custom chars without them being saved differently
            .create();

    transient Path file;

    public void save() {
        if (file == null) {
            throw new IllegalStateException("This config object has not been given a path, use AbstractModConfig.loadConfigFile() to give it one.");
        }

        try (var writer = Files.newBufferedWriter(file)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Problem occurred when trying to save config: " + file, e);
        }
    }

    /**
     * Loads a config file based on a subclass of {@link AbstractModConfig}.
     *
     * @param file The file to load the config from.
     * @param defaultInstance An unedited default instance of the config, usually initialized using the empty constructor.
     * @return T The loaded config or the initialized default instance.
     */
    public static <T extends AbstractModConfig> T loadConfigFile(Path file, T defaultInstance, Type serializedType) {
        T config = null;

        if (Files.exists(file)) {
            // An existing config is present, we should use its values
            try (var fileReader = Files.newBufferedReader(file)) {
                // Parses the config file and puts the values into config object
                config = GSON.fromJson(fileReader, serializedType);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred when trying to load config: " + file, e);
            } catch (JsonParseException e) {
                RollingDownInTheDeep.LOGGER.error("Failed to parse config file, backing up and overwriting with default config: {}", file, e);
                try {
                    Files.copy(file, file.resolveSibling(file.getFileName() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e1) {
                    RollingDownInTheDeep.LOGGER.error("Failed to back up faulty config file: ", e1);
                }
            }
        }
        // gson.fromJson() can return null if file is empty
        if (config == null) {
            config = defaultInstance;
        }

        config.file = file;

        // Saves the file in order to write new fields if they were added
        config.save();
        return config;
    }
}
