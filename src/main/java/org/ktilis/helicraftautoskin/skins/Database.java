package org.ktilis.helicraftautoskin.skins;

import net.pinger.disguise.skin.Skin;
import org.ktilis.helicraftautoskin.HeliCraftAutoSkin;

import java.io.File;
import java.sql.*;

public class Database {
    private static Connection conn;
    private static Statement statmt;
    private static ResultSet resSet;

    public static void start() {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbURL = "jdbc:sqlite:"+ HeliCraftAutoSkin.getInstance().getDataFolder()+File.separator+"plugin.db";
            conn = DriverManager.getConnection(dbURL);
            statmt = conn.createStatement();
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void createTables() throws SQLException {
        statmt.execute("CREATE TABLE IF NOT EXISTS skins (" +
                "'id' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'skin_length' TEXT, " +
                "'skin_value' TEXT, " +
                "'skin_signature' TEXT, " +
                "'skin_name' TEXT" +
                ");");
    }
    public static boolean isSkinExists(Long skin_size) {
        try {
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM skins WHERE skin_length=?;");
            stat.setString(1, skin_size.toString());
            ResultSet rs = stat.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean isSkinExists(String skinName) {
        try {
            PreparedStatement stat = conn.prepareStatement("SELECT * FROM skins WHERE skin_name=?;");
            stat.setString(1, skinName);
            ResultSet rs = stat.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Skin getSkin(Long skin_size) {
        try {
            if(!isSkinExists(skin_size)) return SkinsManager.defaultSkin;
            resSet = statmt.executeQuery("SELECT * FROM skins WHERE skin_length='"+skin_size+"';");
            resSet.next();
            String value = resSet.getString("skin_value");
            String signature = resSet.getString("skin_signature");
            return new Skin(value, signature);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static Skin getSkin(String skinName) {
        try {
            if(!isSkinExists(skinName)) return SkinsManager.defaultSkin;
            resSet = statmt.executeQuery("SELECT * FROM skins WHERE skin_name='"+skinName+"';");
            resSet.next();
            String value = resSet.getString("skin_value");
            String signature = resSet.getString("skin_signature");
            return new Skin(value, signature);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean addSkin(Skin skin, Long skin_size, String skinName) {
        try {
            if(isSkinExists(skinName)) {
                return false; //statmt.execute(String.format("UPDATE skins SET skin_name='%s' WHERE skin_length='%s';", skinName, skin_size.toString()));
            }
            PreparedStatement statement = conn.prepareStatement("INSERT INTO skins (skin_length,skin_value,skin_signature,skin_name) " +
                    "VALUES (?, ?, ?, ?);");

            statement.setString(1, skin_size.toString());
            statement.setString(2, skin.getValue());
            statement.setString(3, skin.getSignature());
            statement.setString(4, skinName);

            return !statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean updateSkin(Skin skin, Long skin_size, String skinName) {
        if(!isSkinExists(skinName)) return false;

        try {
            PreparedStatement statement = conn.prepareStatement("UPDATE skins SET " +
                    "skin_length    = ?, " +
                    "skin_value     = ?, " +
                    "skin_signature = ? "  +
                    "WHERE skin_name = ?;"
            );
            statement.setString(1, skin_size.toString());
            statement.setString(2, skin.getValue());
            statement.setString(3, skin.getSignature());
            statement.setString(4, skinName);
            return !statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stop() {
        try {
            statmt.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
