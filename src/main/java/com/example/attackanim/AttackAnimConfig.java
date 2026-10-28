package com.example.attackanim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Cấu hình lưu tại config/attackanim.json (tự tạo lần chạy đầu tiên). */
public final class AttackAnimConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static AttackAnimConfig instance = new AttackAnimConfig();

	/** Bật/tắt toàn bộ mod. */
	public boolean enabled = true;
	/** Độ mạnh của chuyển động đòn đánh (0.0 - 2.0). */
	public float intensity = 1.0F;
	/** Áp dụng dáng đứng khi cầm kiếm (không đánh). */
	public boolean stance = true;
	/** Bước chân/lao người theo đòn đánh và dáng đứng (khi đứng yên). */
	public boolean legStep = true;
	/** Xoay thân khi đánh. */
	public boolean bodyTwist = true;
	/** Sau bao lâu (mili giây) không đánh thì combo quay về đòn đầu tiên. */
	public int comboResetMs = 1200;
	/** Item nào được coi là vũ khí: tên trong registry chứa một trong các từ này (vd "sword", "axe", "mace"). */
	public List<String> weaponKeywords = new ArrayList<>(List.of("sword"));

	public static AttackAnimConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve("attackanim.json");
		if (Files.exists(path)) {
			try (Reader reader = Files.newBufferedReader(path)) {
				AttackAnimConfig loaded = GSON.fromJson(reader, AttackAnimConfig.class);
				if (loaded != null) {
					if (loaded.weaponKeywords == null || loaded.weaponKeywords.isEmpty()) {
						loaded.weaponKeywords = new ArrayList<>(List.of("sword"));
					}
					instance = loaded;
				}
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
