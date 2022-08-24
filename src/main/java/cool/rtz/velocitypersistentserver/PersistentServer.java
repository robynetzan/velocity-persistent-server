package cool.rtz.velocitypersistentserver;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(id = "velocitypersistentserver", name = "Velocity Persistent Server", version = "0.1.0-SNAPSHOT",
        url = "https://www.rtz.cool", description = "I did it!", authors = {"robynetzan"})
public class PersistentServer {
    public static ProxyServer server;
    public static Logger logger;
    private final Path dataDirectory;
    private Config config;

    @Inject
    public PersistentServer(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.config = new Config(dataDirectory);

        logger.info("enabled");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.register(new PersistentServer(this.server, this.logger, this.dataDirectory));
    }

    private void register(Object x) {
        this.server.getEventManager().register(this, x);
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) throws IOException {
        // save data to file
        String cachedServer = event.getPlayer().getCurrentServer().get().getServerInfo().getName();
        String uuid = event.getPlayer().getUniqueId().toString();
        config.saveFile(uuid, cachedServer);
    }

    @Subscribe
    public void onLogin(PlayerChooseInitialServerEvent event) throws IOException {
        String cachedServer = config.loadFile(event.getPlayer().getUniqueId().toString());
        server.getAllServers().forEach(e -> {
             if (Objects.equals(e.getServerInfo().getName(), cachedServer)) {
                 logger.info("connecting player "+event.getPlayer().getUsername()+" to server "+cachedServer);
                 event.setInitialServer(e);
             }
        });
    }
}