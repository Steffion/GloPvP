package nl.Steffion.GloPvP.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;

import nl.Steffion.GloPvP.GloPvP;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Create and manage config files.
 *
 * @author Steffion
 */
public class Config {
	public FileConfiguration config = null;
	public File configFile = null;
	public String configName;
	public String location;
	public String path;

	/**
	 * Create config from resource in the plugins folder.
	 *
	 * @param configName
	 *            - Name of config e.g. 'config.yml'
	 */
	public Config(final String configName) {
		path = GloPvP.plugin.getDataFolder().toString();
		this.configName = configName;
		location = configName;

		initiateConfig();
	}

	/**
	 * Create config from resource in a different folder.<br>
	 * The root folder is the folder where the server is running from.
	 *
	 * @param path
	 *            - Path to different folder.
	 * @param configName
	 *            - Name of config e.g. 'config.yml'
	 */
	public Config(final String path, final String configName) {
		this.path = path;
		this.configName = configName;
		location = path + "/" + configName;

		initiateConfig();
	}

	/**
	 * Check if defaults from the resource are written in the file.<br>
	 * If content is missing it will add it from the resource.
	 *
	 * @param createdNew
	 *            - Was the config just made?
	 */
	private void checkDefaults(final Boolean createdNew) {
		config = YamlConfiguration.loadConfiguration(configFile);

		Reader reader = null;
		try {
			reader = new InputStreamReader(
					GloPvP.plugin.getResource(configName), "UTF8");
		} catch (final UnsupportedEncodingException e) {
			GloPvP.plugin.getLogger().log(Level.SEVERE,
					"Unsupported Encoding Exception:", e);
		} catch (final Exception e) {
			GloPvP.plugin.getLogger().log(Level.SEVERE, "Exception:", e);
		}

		if (reader != null) {
			final YamlConfiguration yamlConfiguration = YamlConfiguration
					.loadConfiguration(reader);
			boolean changed = false;
			if (createdNew) {
				for (final String key : yamlConfiguration.getKeys(true)) {
					config.set(key, yamlConfiguration.get(key));
					GloPvP.plugin.getLogger().log(
							Level.INFO,
							"New file created: Added '" + key + "' to "
									+ location);
					changed = true;
				}
			} else {
				for (final String key : yamlConfiguration.getKeys(true)) {
					if (config.get(key) == null) {
						config.set(key, yamlConfiguration.get(key));
						GloPvP.plugin.getLogger().log(
								Level.INFO,
								"Found missing setting: Added '" + key
								+ "' to " + location);
						changed = true;
					}
				}
			}

			if (changed) {
				saveConfig();
			}
		}
	}

	/**
	 * Gets the requested boolean by path.<br>
	 * If the boolean does not exist but a default value has been specified,
	 * this will return the default value.<br>
	 * If the boolean does not exist and no default value was specified, this
	 * will return false.
	 *
	 * @param path
	 *            - Path of the boolean to get.
	 * @return Requested boolean.
	 */
	public Boolean getBoolean(final String path) {
		return getConfig().getBoolean(path);
	}

	/**
	 * Gets the config.
	 *
	 * @return The FileConfiguration of the config.
	 */
	private FileConfiguration getConfig() {
		if (config == null) {
			loadConfig();
		}

		return config;
	}

	/**
	 * Gets the requested String by path with locale support.<br>
	 * If the String does not exist but a default value has been specified, this
	 * will return the default value by the default language.<br>
	 * If the String does not exist and no default value was specified, this
	 * will return the GB value.
	 * 
	 * @param player
	 *            - The player you want the locale string from.
	 * @param path
	 *            - Path of the String to get.
	 * @return Requested String.
	 */
	public String getLocaleString(final Player player, final String path) {
		String locale = GloPvP.locale.getString("users."
				+ player.getUniqueId().toString() + ".language");

		if (config.getString(locale + "." + path) == null) {
			locale = GloPvP.locale.getString("general.defaultLanguage");
			if (config.getString(locale + "." + path) == null) {
				Messager.sendMessage(player, Level.SEVERE, GloPvP.messages,
						"messager.missingValue", true,"setting", path, "config",
						configName);
				return null;
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

		return config.getString(locale + "." + path);
	}

	/**
	 * Gets the requested String by path.<br>
	 * If the String does not exist but a default value has been specified, this
	 * will return the default value.<br>
	 * If the String does not exist and no default value was specified, this
	 * will return null.
	 *
	 * @param path
	 *            - Path of the String to get.
	 * @return Requested String.
	 */
	public String getString(final String path) {
		return getConfig().getString(path);
	}

	/**
	 * Initiate the config.
	 */
	public void initiateConfig() {
		if (configFile == null) {
			configFile = new File(path, configName);
		}

		if (!configFile.exists()) {
			if (GloPvP.plugin.getResource(configName) == null) {
				GloPvP.plugin
				.getLogger()
						.log(Level.SEVERE,
								"The file "
										+ configName
										+ " is missing the the resource folder. Contact the developer for help");
				return;
			}

			GloPvP.plugin.getLogger().log(Level.INFO,
					location + " has been created");
			checkDefaults(true);
		}

		loadConfig(true);
	}

	/**
	 * Load the config.
	 */
	public void loadConfig() {
		loadConfig(true);
	}

	/**
	 * Load the config.
	 *
	 * @param firstLoad
	 *            - Is this the first time loading the file?
	 */
	private void loadConfig(final Boolean firstLoad) {
		if (configFile == null) {
			configFile = new File(path, configName);
		}

		checkDefaults(false);

		if (firstLoad) {
			GloPvP.plugin.getLogger().log(Level.INFO,
					location + " has been loaded");
		} else {
			GloPvP.plugin.getLogger().log(Level.INFO,
					location + " has been reloaded");
		}
	}

	/**
	 * Reload the config.
	 */
	public void reloadConfig() {
		loadConfig(false);
	}

	/**
	 * Save the config.
	 */
	public void saveConfig() {
		if ((config == null) || (configFile == null)) {
			return;
		}

		try {
			getConfig().save(configFile);
		} catch (final IOException e) {
			GloPvP.plugin.getLogger().log(Level.SEVERE,
					location + " could not be saved:", e);
		}

		GloPvP.plugin.getLogger().log(Level.INFO,
				configName + " has been saved");
	}

	/**
	 * Sets the specified path to the given value.<br>
	 * If value is null, the entry will be removed. Any existing entry will be
	 * replaced, regardless of what the new value is.
	 *
	 * @param path
	 *            - Path of the object to set.
	 * @param value
	 *            - New value to set the path to.
	 */
	public void set(final String path, final Object value) {
		getConfig().set(path, value);
	}
}