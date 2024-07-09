package org.ktilis.helicraftautoskin.skins.listeners;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.ktilis.helicraftautoskin.HeliCraftAutoSkin;
import org.ktilis.helicraftautoskin.skins.SkinProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class UpdateListener {
    private static HttpServer server;

    public void init(Integer port) {
        try {
            server = HttpServer.create();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/update", new ReqHandler());
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
        try {
            server.bind(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            HeliCraftAutoSkin.getInstance().getLogger().log(Level.SEVERE, "Failed to bind "+port+" port!");
            e.fillInStackTrace();
        }
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    static class ReqHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange ex) throws IOException {
            Map<String, String> params = queryToMap(ex.getRequestURI().getQuery());
            if(!params.containsKey("player")) {
                String res = "{\"error\":\"No 'player' parameter.\"}";
                ex.sendResponseHeaders(418, res.length());
                OutputStream os = ex.getResponseBody();
                os.write(res.getBytes());
                os.close();
                return;
            }

            ex.sendResponseHeaders(200, -1);
            ex.close();

            String playerName = params.get("player");
            Player player = Bukkit.getPlayer(playerName);

            if (player != null) {
                Bukkit.getServer().getScheduler().runTask(HeliCraftAutoSkin.getInstance(), () -> {
                    SkinProvider.updateSkin(player);
                });
            } else {
                String skinURL = Objects.requireNonNull(HeliCraftAutoSkin.getInstance().getConfig().getString("skins-url"))
                        .replaceAll("<nick>", playerName);
                SkinProvider.getSkin(skinURL, playerName); // Меняет скин в бд
            }


        }

        public Map<String, String> queryToMap(String query) {
            if(query == null) {
                return null;
            }
            Map<String, String> result = new HashMap<>();
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                }else{
                    result.put(entry[0], "");
                }
            }
            return result;
        }
    }
}
