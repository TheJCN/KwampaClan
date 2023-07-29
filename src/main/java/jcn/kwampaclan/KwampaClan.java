package jcn.kwampaclan;

import jcn.kwampaclan.Command.CreateCommand;
import jcn.kwampaclan.Command.GuiCommand;
import jcn.kwampaclan.Command.InviteAcceptCommand;
import jcn.kwampaclan.Command.LeaveCommand;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class KwampaClan extends JavaPlugin implements Listener {
    public static final String PLUGINPREFIX = "[KwampaClan]";

    private Connection connection;
    private LuckPerms luckPerms;
    private Logger logger;
    private MySQLDatabaseManager databaseManager; // Added MySQLDatabaseManager instance


    @Override
    public void onEnable() {
        this.logger = Bukkit.getLogger();
        this.logger.info(PLUGINPREFIX + " Запущен");

        // Создание папки плагина
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // Загрузка и создание конфигурационного файла
        saveDefaultConfig();

        // Подключение к базе данных MySQL
        if (!setupDatabase()) {
            logger.severe("Не удалось подключиться к базе данных. Плагин будет отключен.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS clans (id INTEGER PRIMARY KEY AUTO_INCREMENT, clancreator VARCHAR(255), idclan VARCHAR(255), clanname VARCHAR(255), clanprefix VARCHAR(255), members VARCHAR(255))");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Подгрузка LuckPerms
        if (!setupLuckPerms()) {
            logger.severe("LuckPerms не найден или не активирован. Плагин будет отключен.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        CreateCommand createCommand = new CreateCommand(connection, luckPerms);

        Map<Player, Player> time = new HashMap<>();
        InviteAcceptCommand inviteAcceptCommand = new InviteAcceptCommand(connection, luckPerms, time);

        GuiCommand guiCommand = new GuiCommand(connection);

        LeaveCommand leaveCommand = new LeaveCommand(connection, luckPerms);

        // Регистрация основной команды
        MainCommandClan mainCommandClan = new MainCommandClan(connection, luckPerms, createCommand, time, guiCommand, leaveCommand);
        getCommand("clan").setExecutor(mainCommandClan);

        Bukkit.getServer().getPluginManager().registerEvents(new KwampaEventHandler(connection), this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderApi") != null) {
            new Placeholder(this).register();
        }
    }

    @Override
    public void onDisable() {
        logger.info(PLUGINPREFIX + " Отключен");
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean setupDatabase() {
        String host = getConfig().getString("mysql.host");
        int port = getConfig().getInt("mysql.port");
        String database = getConfig().getString("mysql.database");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");

        databaseManager = new MySQLDatabaseManager(host, port, database, username, password); // Create the MySQLDatabaseManager instance
        if (!databaseManager.connect()) {
            return false;
        }
        connection = databaseManager.getConnection();
        return true;
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = getServer().getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
            return true;
        }
        return false;
    }

    public MySQLDatabaseManager getMySQLDatabaseManager() {
        return databaseManager;
    }
}