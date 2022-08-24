package cool.rtz.velocitypersistentserver;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.moandjiezana.toml.Toml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    Path dataDir;
    static final int CONFIG_VERSION = 1;
    @Inject
    public Config(@DataDirectory Path dataDir) {
        this.dataDir = dataDir;
    }

    private String getFilename(String uuid) {
        return uuid+".txt";
    }

    public String loadFile(String uuid) throws IOException {

        String filename = getFilename(uuid);
        File loadLocation = new File(dataDir.toFile(), filename);

        if (!loadLocation.isFile()) {
            // If this location does not exist, create it and write the default server from the Velocity.toml config file
            String defaultServer = PersistentServer.server.getConfiguration().getAttemptConnectionOrder().get(0);
            saveFile(uuid, defaultServer);
            return defaultServer;
        }

        String server = getFileContents(loadLocation);
        return server;
    }

    private String getFileContents(File file) throws FileNotFoundException {
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();
            return everything.trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveFile(String uuid, String server) throws IOException {
        String filename = getFilename(uuid);
        File dataDirectoryFile = this.dataDir.toFile();

        // Make sure the directory exists
        if (!dataDirectoryFile.exists())
            dataDirectoryFile.mkdir(); // TODO ensure it succeeds

        // Create a reference to the file location
        File saveLocation = new File(dataDir.toFile(), filename);

        // Check if file already exists, if not, create it
        if (saveLocation.createNewFile()) {
            // PersistentServer.logger.info("File created: " + saveLocation.getName());
        } else {
            // PersistentServer.logger.info("File already exists.");
        }

        // Write to file
        try {
            FileWriter myWriter = new FileWriter(saveLocation);
            myWriter.write(server);
            myWriter.close();
            // PersistentServer.logger.info("Successfully wrote to the file.");
        } catch (IOException e) {
            // PersistentServer.logger.info("An error occurred.");
            e.printStackTrace();
        }
    }
}
