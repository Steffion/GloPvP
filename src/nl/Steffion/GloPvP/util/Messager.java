package nl.Steffion.GloPvP.util;

import java.util.logging.Level;

import nl.Steffion.GloPvP.GloPvP;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Messager implements Listener {
	/**
	 * Replace default Minecraft colour codes.
	 *
	 * @param message
	 *            - Message which needs to be replaced.
	 * @return Colour replaced message.
	 */
	public static String replaceColours(final String message) {
		return message.replaceAll("&tag", GloPvP.config.getString("tag"))
				.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");
	}

	/**
	 * Send a locale message to the console.
	 *
	 * @param level
	 *            - One of the message level identifiers, e.g., SEVERE.
	 * @param config
	 *            - The config file to get the message from.
	 * @param path
	 *            - The path in the messages file.
	 * @param feedback
	 *            - Send message to other admins.
	 * @param replaceVars
	 *            - OPTIONAL Replace one or more variables in the message. Use
	 *            '%name%' in the config files.<br>
	 *            It replaces the first value with the second value, e.g.:
	 *            "name", test.getName()
	 */
	public static void sendConsoleMessage(final Level level,
			final Config config, final String path, final Boolean feedback,
			final String... replaceVars) {
		String locale = GloPvP.locale.getString("users.CONSOLE.language");

		if (config.getString(locale + "." + path) == null) {
			locale = GloPvP.locale.getString("general.defaultLanguage");
			if (config.getString(locale + "." + path) == null) {
				Messager.sendConsoleMessage(Level.SEVERE, GloPvP.messages,
						"messager.missingValue", true, "setting", path,
						"config", config.configName);
				return;
			}
		}

		if (GloPvP.locale.getBoolean("general.forceLanguage")) {
			if (config.getString(GloPvP.locale
					.getString("general.defaultLanguage") + "." + path) == null) {
				locale = "GB";
			} else {
				locale = GloPvP.locale.getString("general.defaultLanguage");
			}
		}

		String message = config.getString(locale + "." + path)
				;

		if (replaceVars != null) {
			Integer counter = 0;

			for (int i = 0; i < (replaceVars.length / 2); i++) {
				message = message.replaceAll("%" + replaceVars[counter] + "%",
						replaceVars[counter + 1].replaceAll("&tag", "")
						.replaceAll("(&([a-fk-or0-9]))", ""));
				counter = counter + 2;
			}
		}

		GloPvP.plugin.getLogger().log(level, message.replaceAll("&tag", "").replaceAll("(&([a-fk-or0-9]))", ""));
		if (feedback) {
			for (final Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("bukkit.broadcast.admin")) {
					player.sendMessage(Messager.replaceColours("&7&o[Server: "
							+ message + "&7&o]"));
				}
			}
		}
	}

	/**
	 * Send a locale message to a player.<br>
	 *
	 * @param player
	 *            - The player to send the locale message to.<br>
	 *            Entering null will send it to console. Use
	 *            {@link #sendConsoleMessage(Level, Config, String, String...)}
	 *            to send directly to console.
	 * @param level
	 *            - Level of message the console will receive
	 * @param config
	 *            - The config file to get the message from.
	 * @param path
	 *            - The path in the messages file.
	 * @param replaceVars
	 *            - OPTIONAL Replace one or more variables in the message. Use
	 *            '%name%' in the config files.<br>
	 *            It replaces the first value with the second value, e.g.:
	 *            "name", test.getName()
	 */
	public static void sendMessage(final Player player, final Level level,
			final Config config, final String path, final Boolean feedback,
			final String... replaceVars) {
		if (player == null) {
			Messager.sendConsoleMessage(level, config, path, feedback,
					replaceVars);
			return;
		}

		String locale = GloPvP.locale.getString("users."
				+ player.getUniqueId().toString() + ".language");

		if (config.getString(locale + "." + path) == null) {
			locale = GloPvP.locale.getString("general.defaultLanguage");
			if (config.getString(locale + "." + path) == null) {
				Messager.sendMessage(player, Level.SEVERE, GloPvP.messages,
						"messager.missingValue", true, "setting", path,
						"config", config.configName);
				return;
			}
		}

		if (GloPvP.locale.getBoolean("general.forceLanguage")) {
			if (config.getString(GloPvP.locale
					.getString("general.defaultLanguage") + "." + path) == null) {
				locale = "GB";
			} else {
				GloPvP.locale.getString("general.defaultLanguage");
			}
		}

		String message = config.getString(locale + "." + path);

		if (replaceVars != null) {
			Integer counter = 0;

			for (int i = 0; i < (replaceVars.length / 2); i++) {
				message = message.replaceAll("%" + replaceVars[counter] + "%",
						replaceVars[counter + 1]);
				counter = counter + 2;
			}
		}

		player.sendMessage(Messager.replaceColours(message));
		if (feedback) {
			GloPvP.plugin.getLogger().log(Level.INFO,
					"[" + player.getName() + ": " + message.replaceAll("&tag", "").replaceAll("(&([a-fk-or0-9]))", "") + "]");
			for (final Player otherPlayer : Bukkit.getOnlinePlayers()) {
				if (otherPlayer != player && otherPlayer.hasPermission("bukkit.broadcast.admin")) {
					otherPlayer.sendMessage(Messager.replaceColours("&7&o["
							+ player.getName() + ": " + message + "&7&o]"));
				}
			}
		}
	}

	/*
	 * The join event to check and save the player's language.
	 */
	@EventHandler
	public void onPlayerJoinEvent(final PlayerJoinEvent event) {
		if (!Locale.initiated) {
			Locale.initiateLocaleSystem();
		}

		final Player player = event.getPlayer();
		GloPvP.locale.set("users." + player.getUniqueId().toString() + ".name",
				player.getName());
		GloPvP.locale.set("users." + player.getUniqueId().toString()
				+ ".language", Locale.getCountryCode(player));
		GloPvP.locale.saveConfig();

		// TODO GloPvP - add a language chooser, using JSON messages/buttons
	}
}
