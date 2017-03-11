package ca.poutineqc.base.commands;

import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import ca.poutineqc.base.commands.inventories.PInventoryArenaColor;
import ca.poutineqc.base.commands.inventories.PInventoryArenaColors;
import ca.poutineqc.base.commands.inventories.PInventoryGameJoin;
import ca.poutineqc.base.instantiable.PPlayer;
import ca.poutineqc.base.instantiable.Playable;
import ca.poutineqc.base.lang.Language;
import ca.poutineqc.base.lang.Message;
import ca.poutineqc.base.lang.PMessages;
import ca.poutineqc.base.plugin.Library;
import ca.poutineqc.base.plugin.PPlugin;
import ca.poutineqc.base.utils.Utils;

public enum PCommand implements Command {

	HELP("help", PMessages.HELP_HELP, "/%command% help [category] [page]", CommandType.GENERAL) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {

			Language responseLanguage = Library.getLanguage(commandSender);

			String header = Utils.color("&8&m" + StringUtils.repeat(" ", 15) + "&r&8| " + plugin.getPrimaryColor()
					+ plugin.getDescription().getName() + " " + plugin.getSecondaryColor()
					+ ChatColor.stripColor(responseLanguage.get(PMessages.KEYWORD_HELP)) + "&8|&m"
					+ StringUtils.repeat(" ", 35));

			if (args.length == 1) {
				sendGeneralHelp(plugin, commandSender, responseLanguage, cmdValue, header);
				return;
			}

			int pageNumber = 1;
			CommandType commandType = null;
			List<Command> requestedCommands;

			try {
				pageNumber = Integer.parseInt(args[1]);
				if (pageNumber < 1)
					pageNumber = 1;

				requestedCommands = plugin.getCommandManager().getRequiredCommands(plugin, commandSender, commandType);
				if (pageNumber > Math.ceil((double) requestedCommands.size() / 3))
					pageNumber = (int) Math.ceil((double) requestedCommands.size() / 3);

			} catch (NumberFormatException e) {
				switch (args[1].toLowerCase()) {
				case "player":
					commandType = CommandType.PLAYER;
					break;
				case "setup":
					commandType = CommandType.SETUP;
					break;
				case "admin":
					commandType = CommandType.ADMIN;
					break;
				case "general":
					commandType = CommandType.GENERAL;
					break;
				}

				requestedCommands = plugin.getCommandManager().getRequiredCommands(plugin, commandSender, commandType);

				if (args.length > 2) {
					try {
						pageNumber = Integer.parseInt(args[2]);
						if (pageNumber < 1)
							pageNumber = 1;

						if (pageNumber > Math.ceil((double) requestedCommands.size() / 3))
							pageNumber = (int) Math.ceil((double) requestedCommands.size() / 3);

					} catch (NumberFormatException ex) {
					}
				}
			}

			if (requestedCommands.size() == 0)
				pageNumber = 0;

			commandSender.sendMessage(Utils.color(header));
			commandSender.sendMessage(Utils.color(plugin.getPrimaryColor()
					+ ChatColor.stripColor(responseLanguage.get(PMessages.KEYWORD_HELP_CATEGORY)) + ": &7"
					+ (commandType == null ? "ALL" : commandType.toString()) + ", " + plugin.getPrimaryColor()
					+ ChatColor.stripColor(responseLanguage.get(PMessages.KEYWORD_HELP_PAGE)) + ": &7"
					+ String.valueOf(pageNumber) + "&8/&7" + (int) (Math.ceil((double) requestedCommands.size() / 3))));

			if (pageNumber == 0) {
				commandSender.sendMessage(
						ChatColor.RED + ChatColor.stripColor(responseLanguage.get(PMessages.HELP_NO_PERMISSIONS)));
				commandSender.sendMessage("\n");
				return;
			}

			for (int i = 3 * (pageNumber - 1); i < requestedCommands.size() && i < (3 * (pageNumber - 1)) + 3; i++) {
				commandSender.sendMessage(
						plugin.getPrimaryColor() + requestedCommands.get(i).getUsage().replace("%command%", cmdValue));
				commandSender.sendMessage(Utils.color(" &8- &7"
						+ ChatColor.stripColor(responseLanguage.get(requestedCommands.get(i).getHelpMessage()))));
			}

			commandSender.sendMessage("\n");

		}

		private void sendGeneralHelp(PPlugin plugin, CommandSender commandSender, Language responseLanguage,
				String cmdValue, String header) {
			commandSender.sendMessage(Utils.color(header));
			commandSender.sendMessage(Utils.color(plugin.getPrimaryColor() + "/" + cmdValue + " help general &8- "
					+ ChatColor.stripColor(responseLanguage.get(PMessages.HELP_DESCRIPTION_GENERAL))));
			commandSender.sendMessage(Utils.color(plugin.getPrimaryColor() + "/" + cmdValue + " help player &8- "
					+ ChatColor.stripColor(responseLanguage.get(PMessages.HELP_DESCRIPTION_PLAYER))));
			commandSender.sendMessage(Utils.color(plugin.getPrimaryColor() + "/" + cmdValue + " help setup &8- "
					+ ChatColor.stripColor(responseLanguage.get(PMessages.HELP_DESCRIPTION_SETUP))));
			commandSender.sendMessage(Utils.color(plugin.getPrimaryColor() + "/" + cmdValue + " help admin &8- "
					+ ChatColor.stripColor(responseLanguage.get(PMessages.HELP_DESCRIPTION_ADMIN))));
			commandSender.sendMessage(Utils.color(plugin.getPrimaryColor() + "/" + cmdValue + " help all &8- "
					+ ChatColor.stripColor(responseLanguage.get(PMessages.HELP_DESCRIPTION_ALL))));
			commandSender.sendMessage("\n");
			return;
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (CommandType category : CommandType.values())
					if (category.name().toLowerCase().startsWith(args[1].toLowerCase()))
						tabCompletion.add(category.name().toLowerCase());
		}
	},
	LANGUAGE("language", PMessages.HELP_LANGUAGE, "%plugin%.player.language", "/%command% language <language>",
			CommandType.GENERAL) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Player player = (Player) commandSender;
			Language responseLanguage = Library.getLanguage(player.getUniqueId());

			if (args.length == 1) {

				responseLanguage.sendMessage(plugin, player, PMessages.LANGUAGE_LIST);
				for (Entry<String, Language> language : Library.getLanguageManager().entrySet())
					player.sendMessage(plugin.getSecondaryColor() + "- " + language.getValue().getLanguageName());
				return;
			}

			Language language = Library.getLanguageManager().getLanguage(args[1]);
			if (language == null) {
				responseLanguage.sendMessage(plugin, player,
						responseLanguage.get(PMessages.LANGUAGE_NOT_FOUND).replace("%cmd%", cmdValue));
				return;
			}

			PPlayer basePlayer = Library.getPPlayer(player.getUniqueId());
			if (basePlayer == null) {
				basePlayer = Library.newPPlayer(player);
			}

			basePlayer.setLanguage(language);
			basePlayer.sendMessage(plugin, PMessages.LANGUAGE_CHANGED);
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Entry<String, Language> lang : Library.getLanguageManager().entrySet())
					if (lang.getValue().getLanguageName().toLowerCase().startsWith(args[1].toLowerCase()))
						tabCompletion.add(lang.getValue().getLanguageName());
		}
	},

	INFO("info", PMessages.HELP_INFO, "%plugin%.player.info", "/%command% info <arena>", CommandType.PLAYER) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.ERROR_MISSING_ARENA_PARAMETER);
				local.sendMessage(plugin, commandSender, local.get(PMessages.INFO_TIP).replace("%cmd%", cmdValue));
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				local.sendMessage(plugin, commandSender, local.get(PMessages.INFO_TIP).replace("%cmd%", cmdValue));
				return;
			}

			commandSender.sendMessage(game.getInformation(commandSender));

		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	JOIN("join", PMessages.HELP_JOIN, "%plugin%.player.join", "/%command% join <arena>", CommandType.PLAYER) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Player player = (Player) commandSender;
			PPlayer pPlayer = Library.getPPlayer(player);
			if (pPlayer == null)
				pPlayer = Library.newPPlayer(player);

			Playable game = args.length > 1 ? plugin.getGameFromArenaName(args[1]) : null;
			if (game == null) {
				new PInventoryGameJoin(plugin, pPlayer, 1);
				return;
			}

			game.addPlayer(pPlayer, true);
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	QUIT("join", PMessages.HELP_QUIT, "%plugin%.player.quit", "/%command% quit", CommandType.PLAYER) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Player player = (Player) commandSender;

			Playable game = args.length > 1 ? plugin.getGameFromArenaName(args[1]) : null;
			if (game == null) {
				Language local = Library.getLanguage(player.getUniqueId());
				local.sendError(plugin, player, local.get(PMessages.ERROR_QUIT_NOT_IN_GAME));
				return;
			}

			game.removePlayer(player);
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			return;
		}
	},

	LIST("list", PMessages.HELP_LIST, "%plugin%.player.list", "/%command% list", CommandType.PLAYER) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			JOIN.execute(plugin, commandSender, cmdValue, new String[0]);
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			return;
		}
	},

	NEW("new", PMessages.HELP_NEW, "%plugin%.admin.edit.new", "/%command% new <arenaName>", CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			Language local = Library.getLanguage(commandSender);

			if (args.length <= 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			if (plugin.getGameFromArenaName(args[1]) != null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.EDIT_NEW_EXISTS).replace("%arena%", args[1]));
				return;
			}

			if (args[1].length() > 32) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NEW_LONG_NAME);
				return;
			}

			plugin.newArena(args[1]);
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_NEW_SUCCESS).replace("%arena%", args[1]));
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			return;
		}
	},

	DELETE("delete", PMessages.HELP_DELETE, "%plugin%.admin.edit.delete", "/%command% delete <arenaName>",
			CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			if (game.getPlayers().size() > 0) {
				local.sendError(plugin, commandSender, PMessages.ERROR_DELETE_PLAYERS_IN_GAME);
				return;
			}

			plugin.deleteArena(game);
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_DELETE).replace("%arena%", args[1]));
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETZONE("setzone", PMessages.HELP_SETZONE, "%plugin%.admin.edit.zone", "/%command% setzone <arenaName>",
			CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			Player player = (Player) commandSender;

			Plugin worldEdit = Bukkit.getPluginManager().getPlugin(Library.WORLD_EDIT_NAME);
			if (!(worldEdit instanceof com.sk89q.worldedit.bukkit.WorldEditPlugin)) {
				local.sendError(plugin, commandSender, PMessages.ERROR_WORLD_EDIT_MISSING);
				return;
			}

			com.sk89q.worldedit.bukkit.selections.Selection selection = ((com.sk89q.worldedit.bukkit.WorldEditPlugin) plugin)
					.getSelection(player);
			if (selection == null) {
				local.sendError(plugin, commandSender, PMessages.ERROR_WORLD_EDIT_SELECTION_MISSING);
				return;
			}

			game.getArena().setMaxPoint(selection.getMaximumPoint());
			game.getArena().setMinPoint(selection.getMinimumPoint());
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_ZONE).replace("%arena%", args[1]));

		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETLOBBY("setlobby", PMessages.HELP_SETLOBBY, "%plugin%.admin.edit.lobby", "/%command% setlobby <arenaName>",
			CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			Player player = (Player) commandSender;

			game.getArena().setLobby(player.getLocation());
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_LOBBY).replace("%arena%", args[1]));

		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETSTART("setstart", PMessages.HELP_SETLOBBY, "%plugin%.admin.edit.start", "/%command% setstart <arenaName>",
			CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			Player player = (Player) commandSender;

			if (game.getMaxStart() == 1)
				game.getArena().setStart(0, player.getLocation());
			else {
				PPlayer pPlayer = Library.getPPlayer(player);
				if (pPlayer == null)
					pPlayer = Library.newPPlayer(player);

				new PInventoryArenaColor(plugin, pPlayer, game);
			}
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETSPECTATE("setspectate", PMessages.HELP_SETSPECTATE, "%plugin%.admin.edit.spectate",
			"/%command% setlobby <arenaName>", CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}

			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			Player player = (Player) commandSender;

			game.getArena().setSpectate(player.getLocation());
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_SPECTATE).replace("%arena%", args[1]));
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETMINPLAYERS("setminplayers", PMessages.HELP_SETMINPLAYERS, "%plugin%.admin.edit.setminplayers",
			"/%command% setminplayers <arenaName> <amount>", CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			if (args.length == 2) {
				local.sendError(plugin, commandSender, PMessages.AMOUNTPLAYERS_NO_NUMBER);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			int amount = 1;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.AMOUNTPLAYERS_NO_NUMBER).replace("%arena%", args[1]));
				return;
			}

			game.getArena().setMinPlayer(amount);
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_MINPLAYERS).replace("%arena%", args[1]));
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETMAXPLAYERS("setmaxplayers", PMessages.HELP_SETMAXPLAYERS, "%plugin%.admin.edit.setmaxplayers",
			"/%command% setminplayers <arenaName> <amount>", CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			if (args.length == 2) {
				local.sendError(plugin, commandSender, PMessages.AMOUNTPLAYERS_NO_NUMBER);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}

			int amount = 1;
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException ex) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.AMOUNTPLAYERS_NO_NUMBER).replace("%arena%", args[1]));
				return;
			}

			game.getArena().setMaxPlayer(amount);
			local.sendMessage(plugin, commandSender, local.get(PMessages.EDIT_MAXPLAYERS).replace("%arena%", args[1]));
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	SETCOLORS("setcolors", PMessages.HELP_COLORS, "%plugin%.admin.edit.setcolors",
			"/%command% setcolors <arenaName>", CommandType.SETUP) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {
			if (!(commandSender instanceof Player)) {
				commandSender.sendMessage("You cannot use this command from here!");
				return;
			}
			
			Language local = Library.getLanguage(commandSender);

			if (args.length == 1) {
				local.sendError(plugin, commandSender, PMessages.EDIT_NONAME);
				return;
			}

			Playable game = plugin.getGameFromArenaName(args[1]);
			if (game == null) {
				local.sendError(plugin, commandSender,
						local.get(PMessages.ERROR_MISSING_ARENA).replace("%arena%", args[1]));
				return;
			}
			
			Player player = (Player) commandSender;

			PPlayer pPlayer = Library.getPPlayer(player);
			if (pPlayer == null)
				pPlayer = Library.newPPlayer(player);

			new PInventoryArenaColors(plugin, pPlayer, game);
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			if (args.length == 2)
				for (Playable game : plugin.getGames())
					if (game.getArena().getName().startsWith(args[1].toLowerCase()))
						tabCompletion.add(game.getArena().getName());
		}
	},

	RELOAD("reload", PMessages.HELP_RELOAD, "%plugin%.admin.reload", "/%command% reload", CommandType.ADMIN) {
		@Override
		public void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args) {

			plugin.reload();

			Language responseLanguage = (commandSender instanceof Player)
					? Library.getLanguage(((Player) commandSender).getUniqueId())
					: Library.getLanguageManager().getDefault();

			commandSender.sendMessage(responseLanguage.get(PMessages.RELOAD));
		}

		@Override
		public void complete(PPlugin plugin, List<String> tabCompletion, String[] args) {
			return;
		}
	};

	private CommandType type;
	private String cmdChoice;
	private String usage;
	private String permission;
	private Message helpMessage;

	private PCommand(String cmdChoice, Message helpMessage, String permission, String usage, CommandType type) {
		this.cmdChoice = cmdChoice;
		this.usage = usage;
		this.permission = permission;
		this.helpMessage = helpMessage;
		this.type = type;
	}

	private PCommand(String cmdChoice, Message helpMessage, String usage, CommandType type) {
		this.cmdChoice = cmdChoice;
		this.usage = usage;
		this.permission = null;
		this.helpMessage = helpMessage;
		this.type = type;
	}

	@Override
	public boolean isOfType(CommandType type) {
		return (type == null) ? true : this.type.equals(type);
	}

	@Override
	public boolean equals(String cmdChoice) {
		return this.cmdChoice.equalsIgnoreCase(cmdChoice);
	}

	@Override
	public String getUsage() {
		return usage;
	}

	@Override
	public String getName() {
		return cmdChoice;
	}

	@Override
	public boolean hasPermission(PPlugin plugin, CommandSender commandSender) {
		return (permission != null) ? commandSender
				.hasPermission(permission.replace("%plugin%", plugin.getDescription().getName().toLowerCase())) : true;
	}

	@Override
	public Message getHelpMessage() {
		return helpMessage;
	}

	@Override
	public abstract void execute(PPlugin plugin, CommandSender commandSender, String cmdValue, String[] args);

	@Override
	public abstract void complete(PPlugin plugin, List<String> tabCompletion, String[] args);

}
