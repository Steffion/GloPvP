package nl.Steffion.GloPvP;

import java.util.logging.Level;

import nl.Steffion.GloPvP.util.Config;
import nl.Steffion.GloPvP.util.Locale;
import nl.Steffion.GloPvP.util.Messager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class GloPvP extends JavaPlugin {
	public static Config config;
	public static Config kits;
	public static Config locale;
	public static Config messages;
	public static PluginDescriptionFile pdf;
	public static GloPvP plugin;
	public static Config users;

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {
		Player player = null;

		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (cmd.getName().equalsIgnoreCase("glopvp")) {
			if (args.length == 0) {
				Messager.sendMessage(player, Level.SEVERE, GloPvP.messages,
						"usage", false, "command",
						"glopvp <list|save|delete|sign|select> [kitname]");
				return true;
			} else if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					String list = "";
					Boolean first = true;

					if (GloPvP.kits.config.getKeys(false).isEmpty()) {
						list = "&onone&r";
					}

					for (final String key : GloPvP.kits.config.getKeys(false)) {
						if (first) {
							first = false;
							list = key;
						} else {
							list = list + ", " + key;
						}
					}

					Messager.sendMessage(player, Level.INFO, GloPvP.messages,
							"commands.list", false, "list", list);
					return true;
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("save")) {
					if (player == null) {
						Messager.sendConsoleMessage(Level.SEVERE,
								GloPvP.messages, "commands.ingameOnly", false);
						return true;
					}

					if (!player.hasPermission("glopvp.save")) {
						Messager.sendMessage(player, Level.SEVERE,
								GloPvP.messages, "noPermission", false);
						return true;
					}

					final String kitname = args[1];

					GloPvP.kits.set(kitname, null);
					GloPvP.kits.set(kitname + ".name", kitname);

					if (player.getInventory().getArmorContents()[0].getType() != Material.AIR) {
						GloPvP.kits.set(kitname + ".boots", player
								.getInventory().getArmorContents()[0]);
					}

					if (player.getInventory().getArmorContents()[1].getType() != Material.AIR) {
						GloPvP.kits.set(kitname + ".leggings", player
								.getInventory().getArmorContents()[1]);
					}

					if (player.getInventory().getArmorContents()[2].getType() != Material.AIR) {
						GloPvP.kits.set(kitname + ".chestplate", player
								.getInventory().getArmorContents()[2]);
					}

					if (player.getInventory().getArmorContents()[3].getType() != Material.AIR) {
						GloPvP.kits.set(kitname + ".helmet", player
								.getInventory().getArmorContents()[3]);
					}

					for (int i = player.getInventory().getSize(); i >= 0; i--) {
						final ItemStack item = player.getInventory().getItem(i);
						if (item != null) {
							GloPvP.kits.set(kitname + "." + i, item);
						}
					}

					GloPvP.kits.saveConfig();

					Messager.sendMessage(player, Level.INFO, GloPvP.messages,
							"commands.save", true, "name", kitname);
					return true;
				} else if (args[0].equalsIgnoreCase("delete")) {
					final String kitname = args[1];

					if (GloPvP.kits.getString(kitname + ".name") == null) {
						Messager.sendMessage(player, Level.SEVERE,
								GloPvP.messages, "commands.select.invalid",
								false);
						return true;
					}

					GloPvP.kits.set(kitname, null);
					GloPvP.kits.saveConfig();

					Messager.sendMessage(player, Level.INFO, GloPvP.messages,
							"commands.delete", true, "name", kitname);
					return true;
				} else if (args[0].equalsIgnoreCase("sign")) {
					if (player == null) {
						Messager.sendConsoleMessage(Level.SEVERE,
								GloPvP.messages, "commands.ingameOnly", false);
						return true;
					}

					final String kitname = args[1];

					if (GloPvP.kits.getString(kitname + ".name") == null) {
						Messager.sendMessage(player, Level.SEVERE,
								GloPvP.messages, "commands.select.invalid",
								false);
						return true;
					}

					Bukkit.dispatchCommand(
							Bukkit.getConsoleSender(),
							Messager.replaceColours(GloPvP.messages
									.getLocaleString(player, "selectSign")
									.replaceAll("%player%", player.getName())
									.replaceAll("%kit%", kitname)));

					Messager.sendMessage(player, Level.INFO, GloPvP.messages,
							"commands.sign", false);
					return true;
				} else if (args[0].equalsIgnoreCase("select")) {
					if (player == null) {
						Messager.sendConsoleMessage(Level.SEVERE,
								GloPvP.messages, "commands.ingameOnly", false);
						return true;
					}

					final String kitname = args[1];

					if (GloPvP.kits.getString(kitname + ".name") == null) {
						Messager.sendMessage(player, Level.SEVERE,
								GloPvP.messages, "commands.select.invalid",
								false);
						return true;
					}

					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);

					if (GloPvP.kits.config.get(kitname + ".boots") != null) {
						player.getInventory().setBoots(
								(ItemStack) GloPvP.kits.config.get(kitname
										+ ".boots"));
					}

					if (GloPvP.kits.config.get(kitname + ".leggings") != null) {
						player.getInventory().setLeggings(
								(ItemStack) GloPvP.kits.config.get(kitname
										+ ".leggings"));
					}

					if (GloPvP.kits.config.get(kitname + ".chestplate") != null) {
						player.getInventory().setChestplate(
								(ItemStack) GloPvP.kits.config.get(kitname
										+ ".chestplate"));
					}

					if (GloPvP.kits.config.get(kitname + ".helmet") != null) {
						player.getInventory().setHelmet(
								(ItemStack) GloPvP.kits.config.get(kitname
										+ ".helmet"));
					}

					for (int i = player.getInventory().getSize(); i >= 0; i--) {
						if (GloPvP.kits.config.get(kitname + "." + i) == null) {
							player.getInventory().setItem(i,
									new ItemStack(Material.AIR));
						} else {
							player.getInventory().setItem(
									i,
									(ItemStack) GloPvP.kits.config.get(kitname
											+ "." + i));
						}
					}

					player.updateInventory();

					Messager.sendMessage(player, Level.INFO, GloPvP.messages,
							"commands.select.selected", false, "name", kitname);
					return true;
				}

				Messager.sendMessage(player, Level.SEVERE, GloPvP.messages,
						"usage", false, "command",
						"glopvp <list|save|delete|sign|select> [kitname]");
				return true;
			}
		}

		return true;
	}

	@Override
	public void onDisable() {
		Messager.sendConsoleMessage(Level.INFO, GloPvP.messages,
				"onDisable.author", true, "author", GloPvP.pdf.getAuthors()
						.get(0));
	}

	@Override
	public void onEnable() {
		GloPvP.plugin = this;
		GloPvP.pdf = getDescription();

		GloPvP.config = new Config("config.yml");
		GloPvP.messages = new Config("messages.yml");
		GloPvP.kits = new Config("kits.yml");
		GloPvP.users = new Config("users.yml");
		Locale.initiateLocaleSystem();

		Bukkit.getPluginManager().registerEvents(new Messager(), this);

		Messager.sendConsoleMessage(Level.INFO, GloPvP.messages,
				"onEnable.author", false, "author", GloPvP.pdf.getAuthors()
						.get(0));
		Messager.sendConsoleMessage(Level.INFO, GloPvP.messages,
				"onEnable.finished", true, "name", GloPvP.pdf.getName(),
				"version", GloPvP.pdf.getVersion());
	}
}