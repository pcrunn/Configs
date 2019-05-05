package me.pcrunn.configs;

import me.pcrunn.configs.util.ItemUtils;
import me.pcrunn.configs.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class ConfigService {

    final FileConfiguration fileConfiguration;
    private final String path;
    private final JavaPlugin plugin;
    private final File file;

    public ConfigService(JavaPlugin plugin, String config) {
        this.plugin = plugin;
        path = config;
        file = new File(plugin.getDataFolder().getAbsolutePath(), File.separator + config + ".yml");
        if (!file.exists()) {
            plugin.saveResource(config + ".yml", false);
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            try {
                update();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 20L, 20L);
    }

    private void update() throws Exception {
        fileConfiguration.load(file);
        for (Field field : getClass().getFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                ConfigValue configValue = field.getAnnotation(ConfigValue.class);
                String path = configValue.value();
                if (String.class.equals(field.getType())) {
                    field.set(this, fileConfiguration.getString(path));
                } else if (List.class.equals(field.getType())) {
                    field.set(this, fileConfiguration.getList(path));
                } else if (Integer.class.equals(field.getType())) {
                    field.set(this, fileConfiguration.getInt(path));
                } else if (Double.class.equals(field.getType())) {
                    field.set(this, fileConfiguration.getDouble(path));
                } else if (ItemStack.class.equals(field.getType())) {
                    field.set(this, ItemUtils.deserialize(fileConfiguration.getString(path)));
                } else if (Location.class.equals(field.getType())) {
                    field.set(this, LocationUtil.deserialize(fileConfiguration.getString(path)));
                } else {
                    field.set(this, fileConfiguration.get(path));
                }
            }
        }
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfiguration() {
        return this.fileConfiguration;
    }

    public String getPath() {
        return this.path;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public File getFile() {
        return this.file;
    }
}
