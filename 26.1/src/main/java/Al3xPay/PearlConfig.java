package Al3xPay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class PearlConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Path path;
	private boolean enabled = true;

	private PearlConfig(Path path) {
		this.path = path;
	}

	public static PearlConfig load(Path path) {
		PearlConfig config = new PearlConfig(path);

		if (Files.exists(path)) {
			try {
				String content = Files.readString(path, StandardCharsets.UTF_8);
				JsonObject json = JsonParser.parseString(content).getAsJsonObject();

				if (json.has("enabled")) {
					config.enabled = json.get("enabled").getAsBoolean();
				}
			} catch (Exception exception) {
				FastClickPearl.LOGGER.warn("Could not read config {}, using defaults", path, exception);
			}
		}

		return config;
	}

	public void save() {
		JsonObject json = new JsonObject();
		json.addProperty("enabled", enabled);

		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(json), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			FastClickPearl.LOGGER.warn("Could not write config {}", path, exception);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
