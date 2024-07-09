package org.ktilis.helicraftautoskin.skins;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.ktilis.helicraftautoskin.HeliCraftAutoSkin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class SkinProvider {
    public static final Skin defaultSkin = new Skin( // Steve skin
            "ewogICJ0aW1lc3RhbXAiIDogMTY3NTI3NjYzOTM4NywKICAicHJvZmlsZUlkIiA6ICI5NDFjNDM1MDUxMzI0YWVkOGUyOWYzZjgwYTcwNzk4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJCcmFuZFdlWF85MiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYjI1MTYxNWNhZTY5MGExMjQ4ZjU1MDAxNjc1YWJiZDQ5YTlmYTE2NjczNjY5NWQ3NDYwMzk3YjVhNDFlMjAyIgogICAgfQogIH0KfQ==",
            "qqXGDSXcoay7bp4Z2MKZ/wqKEC8hN3f+7jWom3oxY7Zy69UV74YWvUw+9v5GlKWJtMaamr+i9LjqtJ3BpBEb/5CP29XiyopiTD9ah3c3lNSM7PqLYWVGxFr4XeV522/Z6tL9FnZmqR6yTo3lHg1ootRkzzgqS0h9F23GVYi+BuU6IFIXwCJB4dfefVFvXadT2mxCE1ozo3Bt2A1/bwkJ1bmuTQQvaiE8bB9qMgrSfj8Saxfa5VXocv/2N6xDngDU6Nd7tIcf/h46HabNyp8qCWCs4korYd3kD5kSxwUk+buOaxXbjhICTYyAKQ33kAHL6Pm9rmLQwYJ8xfRwGKnFhSkMbXOVbWYH3XIcfOP8LSEfRrZ92oqo/vlcfJR8XMgjKI3xRRjDUsrP9Qj5xoNXR5BHRsvhVVYkBIkbYMd+y7Z2pfz/hdySyP0pvsmfsjQCrUvhiHgOEEizCRvqSjQhWuthmHuXu3ypmYzbfgZ0I0VGYFBpggZu8UvAWBlJYTCFcVH9wyhOeVEdd6eMzeTSF69aEYJcDw8wJ3XEVZSyIFZcZlhX8u3efTauT2GJ1WJFSJbOTG1Gq5I/diNQkQMSsNFch5NaW4Vug08AuUdUzxt224IVVMj9h1fDk9jIW5XG85Zk7TkTmWzmRssjb68YzxyO6n0kUHyIoEAauAtZxzU="
    );

    public static Long tryGetFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            return conn.getContentLengthLong();
        } catch (IOException e) {
            return Long.MIN_VALUE;
        } finally {
            assert conn != null;
            conn.disconnect();
        }
    }

    private static Skin mineSkinReq(String skinUrl) throws IOException {

        Long skinSize = tryGetFileSize(new URL(skinUrl));
        if (Database.isSkinExists(skinSize)) {
            return Database.getSkin(skinSize);
        }

        URL url = new URL(HeliCraftAutoSkin.MINESKIN_REQUEST_URL);
        JsonObject postData = new JsonObject();
        postData.addProperty("variant", "classic");
        postData.addProperty("visibility", 0);
        postData.addProperty("url", skinUrl);

        String mineSkinApiKey = HeliCraftAutoSkin.getInstance().getConfig().getString("mineskin-apikey");

        HttpsURLConnection conn;
        conn = (HttpsURLConnection) url.openConnection();
        try {
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.toString().length()));
            conn.setRequestProperty("User-Agent", "HeliCraftAutoSkin/helicraft.ru");
            conn.setRequestProperty("accept", "application/json");
            if (!Objects.equals(mineSkinApiKey, "")) {
                conn.setRequestProperty("Authorization", "Bearer " + mineSkinApiKey);
            }
            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(postData.toString());
            }

            conn.connect();

            StringBuilder jsonString = new StringBuilder();
            try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()))) {
                String line;
                while ((line = bf.readLine()) != null) {
                    jsonString.append(line);
                }
            }
            JsonObject res = JsonParser.parseString(jsonString.toString()).getAsJsonObject();
            //HeliCraftAutoSkin.getInstance().getLogger().info(res.toString());
            if (res.get("success") != null) {
                if (!res.get("success").getAsBoolean()) {
                    throw new IOException("MineSkin error: "
                            + res.get("error").getAsString());
                }
            }
            JsonObject texture = res.getAsJsonObject("data")
                    .getAsJsonObject("texture");
            return new Skin(
                    texture.get("value").getAsString(),
                    texture.get("signature").getAsString()
            );
        } catch (IOException e) {
            if(conn.getResponseCode() == 400 || conn.getResponseCode() == 429 || conn.getResponseCode() == 500) {
                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader bf = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()))) {
                    String line;
                    while ((line = bf.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                }
                HeliCraftAutoSkin.getInstance().getLogger().log(Level.SEVERE, "MineSkin error: " + stringBuilder);
            } else {
                e.fillInStackTrace();
            }
            return defaultSkin;
        }
    }

    public static void setSkin(Player player, Skin skin) {
        PacketProvider.setSkin(player, skin);
        PacketProvider.refreshPlayer(player);
    }

    public static Skin getSkin(String skinURL, String name) {
        try {
            if(Database.isSkinExists(name)) {
                if(Database.isSkinExists(tryGetFileSize(new URL(skinURL)))) {
                    return Database.getSkin(name);
                } else {
                    HeliCraftAutoSkin.getInstance().getLogger().info("Updating skin for "+name+"...");
                    Skin skin = mineSkinReq(skinURL);
                    Database.updateSkin(skin, tryGetFileSize(new URL(skinURL)), name);
                    return skin;
                }
            }
        } catch (IOException e) {
            e.fillInStackTrace();
            return defaultSkin;
        }

        try {
            Skin skin = mineSkinReq(skinURL);
            Database.addSkin(skin, tryGetFileSize(new URL(skinURL)), name);
            return skin;
        } catch (IOException e) {
            e.fillInStackTrace();
            return defaultSkin;
        }
    }

    public static void updateSkin(Player p) {
        String skinURL = Objects.requireNonNull(
                HeliCraftAutoSkin.getInstance().getConfig().getString("skins-url"))
                    .replaceAll("<nick>", p.getDisplayName());
        Skin skin = getSkin(skinURL, p.getDisplayName());
        setSkin(p, skin);
    }
}
