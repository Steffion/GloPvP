package nl.Steffion.GloPvP;

import java.util.logging.Level;

import nl.Steffion.GloPvP.util.Config;
import nl.Steffion.GloPvP.util.Locale;
import nl.Steffion.GloPvP.util.Messager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class GloPvP extends JavaPlugin {
	public static Config config;
	public static Config locale;
	public static Config messages;
	public static PluginDescriptionFile pdf;
	public static GloPvP plugin;

	@Override
	public void onDisable() {
		Messager.sendConsoleMessage(Level.INFO, GloPvP.messages,
				"onDisable.author", "author", GloPvP.pdf.getAuthors().get(0));
	}

	@Override
	public void onEnable() {
		GloPvP.plugin = this;
		GloPvP.pdf = getDescription();

		GloPvP.config = new Config("config.yml");
		GloPvP.messages = new Config("messages.yml");
		Locale.initiateLocaleSystem();

		Bukkit.getPluginManager().registerEvents(new Messager(), this);

		Messager.sendConsoleMessage(Level.INFO, GloPvP.messages,
				"onEnable.author", "author", GloPvP.pdf.getAuthors().get(0));
		Messager.sendConsoleMessage(Level.INFO, GloPvP.messages,
				"onEnable.finished", "name", GloPvP.pdf.getName(), "version",
				GloPvP.pdf.getVersion());
	}
}