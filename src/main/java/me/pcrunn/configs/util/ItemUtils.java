package me.pcrunn.configs.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    public static String serialize(ItemStack source) {
        StringBuilder builder = new StringBuilder();

        if (source == null) {
            return builder.toString();
        }

        builder.append("@t")
                .append(source.getType())
                .append(":@a")
                .append(source.getAmount())
                .append(":@d")
                .append(source.getDurability())
                .append(":@s")
                .append(source.getData().getData());

        if (source.hasItemMeta()) {
            if (source.hasItemMeta()) {
                if (source.getItemMeta().hasDisplayName()) {
                    builder.append(":@n");
                    builder.append(source.getItemMeta().getDisplayName());
                }

                if (source.getItemMeta().hasLore()) {
                    builder.append(":@l");
                    for (String string : source.getItemMeta().getLore()) {
                        builder.append(string.replace(ChatColor.COLOR_CHAR, '&'))
                                .append(",");
                    }
                }
            }
        }

        if (!source.getEnchantments().isEmpty()) {
            builder.append(":@e");
            for (Enchantment ench : source.getEnchantments().keySet()) {
                builder.append(ench.getName())
                        .append("-")
                        .append(",");
            }
        }

        return builder.toString();
    }

    public static ItemStack deserialize(String string) {

        if (string.isEmpty()) {
            return null;
        }

        final ItemStack toReturn = new ItemStack(Material.AIR);
        final ItemMeta meta = toReturn.getItemMeta();

        final String[] split = string.split(":");

        for (String str : split) {
            char c = str.charAt(1);

            final String data = str.substring(1);

            switch (c) {
                case 't':
                    toReturn.setType(Material.getMaterial(data));
                    break;
                case 'a':
                    toReturn.setAmount(Integer.valueOf(data));
                    break;
                case 'd':
                    toReturn.setDurability(Short.valueOf(data));
                    break;
                case 's':
                    toReturn.getData().setData(Byte.valueOf(data));
                    break;
                case 'n':
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', data));
                    break;
                case 'l':
                    final String[] loreSplit = data.split(",");
                    List<String> lore = new ArrayList<>();
                    for (String l : loreSplit) {
                        lore.add(l.replace('&', ChatColor.COLOR_CHAR));
                    }
                    meta.setLore(lore);
                    break;
                case 'e':
                    final String[] enchSplit = data.split(",");
                    for (String e : enchSplit) {
                        final String[] enchLocalSplit = e.split("-");
                        Enchantment ench = Enchantment.getByName(enchLocalSplit[0]);
                        int level = Integer.valueOf(enchLocalSplit[1]);
                        toReturn.addEnchantment(ench, level);
                    }
                    break;
            }
        }
        return toReturn;
    }

}
