package com.example.attackanim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Cấu hình lưu tại config/attackanim.json (tự tạo lần chạy đầu tiên). */
public final class AttackAnimConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static AttackAnimConfig instance = new AttackAnimConfig();

	/** Bật/tắt animation chém. */
	public boolean enabled = true;
	/** Độ mạnh của chuyển động (0.0 - 2.0). */
	public float intensity = 1.0F;
	/** Xoay thân khi chém. */
	public boolean bodyTwist = true;
	/** Bước chân khi chém. */
	public boolean legStep = true;

	public static AttackAnimConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("attackanim.json");
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				AttackAnimConfig loaded = GSON.fromJson(reader, AttackAnimConfig.class);
				if (loaded != null) instance = loaded;
			} catch (Exception e) {
				AttackAnimClient.LOGGER.warn("Không đọc được attackanim.json, dùng mặc định", e);
			}
		}
		save(path);
	}

	private static void save(Path path) {
		try (Writer writer = Files.newBufferedWriter(path)) {
			GSON.toJson(instance, writer);
		} catch (IOException e) {
			AttackAnimClient.LOGGER.warn("Không ghi được attackanim.json", e);
		}
	}
}
