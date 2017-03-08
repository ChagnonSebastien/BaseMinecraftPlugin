package ca.poutineqc.base.lang;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import ca.poutineqc.base.data.StringSavableValue;
import ca.poutineqc.base.data.UniversalSavableValue;
import ca.poutineqc.base.data.YAML;
import ca.poutineqc.base.plugin.Library;
import ca.poutineqc.base.plugin.PConfigKey;
import ca.poutineqc.base.plugin.PPlugin;

public class Language extends HashMap<Message, String> implements UniversalSavableValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5172580943430082727L;

	public static final int MAX_STRING_LENGTH = 16;
	private static final String PRIMAL_KEY = "value";

	public static final String FOLDER_NAME = "languageFiles";

	YAML yamlFile;
	private boolean prefixBeforeEveryMessage;
	private boolean serverLanguage;

	public Language(PPlugin plugin, String fileName, boolean builtIn, boolean serverLanguage) {
		prefixBeforeEveryMessage = plugin.getConfig().getBoolean(PConfigKey.PREFIX.getKey(), true);

		this.serverLanguage = serverLanguage;
		yamlFile = new YAML(plugin, fileName, builtIn, FOLDER_NAME);
	}

	Language(Language defaultLanguage) {
		this.putAll(defaultLanguage);
		
		this.prefixBeforeEveryMessage = defaultLanguage.prefixBeforeEveryMessage;
		
		this.serverLanguage = true;
		this.yamlFile = defaultLanguage.yamlFile;
	}

	public String getMessage(PPlugin plugin, Message message) {
		if (this.containsKey(message))
			return this.get(message).replaceAll("%p%", "&" + plugin.getPrimaryColor().getChar()).replaceAll("%s%",
					"&" + plugin.getSecondaryColor().getChar());
		else
			return Library.getLanguageManager().getDefault().get(message)
					.replaceAll("%p%", "&" + plugin.getPrimaryColor().getChar())
					.replaceAll("%s%", "&" + plugin.getSecondaryColor().getChar());
	}

	public void sendMessage(PPlugin plugin, Player player, Message message) {
		sendMessage(plugin, player, getMessage(plugin, message));
	}

	public void sendMessage(PPlugin plugin, Player player, String message) {
		if (prefixBeforeEveryMessage)
			player.sendMessage(ChatColor.translateAlternateColorCodes('&',
					getMessage(plugin, PMessages.PREFIX).replace("%plugin%", plugin.getPrefix()) + " " + message));
		else
			player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public void addMessages(Collection<Message> messages) {
		for (Message message : messages)
			this.put(message, yamlFile.getYAML().getString(message.getKey(), message.getDefaultMessage()));
	}

	public String getLanguageName() {
		if (this.serverLanguage) 
			return ChatColor.stripColor(this.get(PMessages.KEYWORD_SERVER));
			
		return ChatColor.stripColor(this.get(PMessages.LANGUAGE_NAME));
	}

	@Override
	public String toSString() {
		return pad(serverLanguage ? LanguagesManager.DEFAULT : yamlFile.getFileName().replace(".yml", ""));
	}

	@Override
	public String toString() {
		return "Language:{name:" + this.getLanguageName() + "}";
	}

	@Override
	public int getMaxToStringLength() {
		return MAX_STRING_LENGTH;
	}

	public static String getKey(String value) {
		return StringSavableValue.unpad(value);
	}

	public static String getKey(ConfigurationSection cs) {
		return cs.getString(PRIMAL_KEY);
	}

	@Override
	public ConfigurationSection toConfigurationSection() {
		ConfigurationSection cs = new YamlConfiguration();
		cs.set(PRIMAL_KEY, serverLanguage ? LanguagesManager.DEFAULT : yamlFile.getFileName().replace(".yml", ""));
		return cs;
	}

	public static String getKey(JsonObject json) {
		return json.get(PRIMAL_KEY).getAsString();
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject json = new JsonObject();
		json.addProperty(PRIMAL_KEY, serverLanguage ? LanguagesManager.DEFAULT : yamlFile.getFileName().replace(".yml", ""));
		return json;
	}
}
