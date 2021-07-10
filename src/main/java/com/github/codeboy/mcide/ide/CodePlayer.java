package com.github.codeboy.mcide.ide;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.Util;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.piston4j.api.ExecutionResult;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.github.codeboy.mcide.config.Message.createMessage;

public class CodePlayer {
    private static final HashMap<UUID, CodePlayer> codePlayers = new HashMap<>();
    private final UUID playerId;
    private List<CodeProject> projects = new ArrayList<>();

    public CodePlayer(UUID playerId) {
        this.playerId = playerId;
        register(this);
    }

    public static void register(CodePlayer codePlayer) {
        codePlayers.put(codePlayer.playerId, codePlayer);
    }

    private static CodePlayer createCodePlayer(UUID id) {
        File saveFile = new File(Mcide.getPlugin(Mcide.class).getProjectsPath() + id);
        return saveFile.exists() ? fromFile(saveFile) : new CodePlayer(id);
    }

    private static CodePlayer fromFile(File file) {
        String json = Util.readFile(file);
        CodePlayer player = Mcide.gson().fromJson(json, CodePlayer.class);
        codePlayers.put(player.getPlayerId(),player);
        return player;
    }

    public static CodePlayer getCodePlayer(UUID id) {
        return codePlayers.containsKey(id) ? codePlayers.get(id) : createCodePlayer(id);
    }

    public static CodePlayer getCodePlayer(Player player) {
        return getCodePlayer(player.getUniqueId());
    }

    public void save() {
        Mcide plugin = Mcide.getPlugin(Mcide.class);
        String json = Mcide.gson().toJson(this);
        System.out.println(json);
        try {
            List<String> lines = Arrays.asList(json.split("\n"));
            Path path = Paths.get(plugin.getProjectsPath() + playerId);
            File file = path.toFile();
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runProject(CodeProject project) {
        OfflinePlayer offlinePlayer = getPlayer();
        if (!offlinePlayer.isOnline())
            throw new IllegalStateException("Can not run project while owner is offline!");
        Player player = offlinePlayer.getPlayer();
        player.sendMessage(createMessage(Message.EXECUTION_START, project.getTitle()));
        ExecutionResult result = project.run();
    }

    //region getters and setters
    public List<CodeProject> getProjects() {
        return projects;
    }

    public void setProjects(List<CodeProject> projects) {
        this.projects = projects;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public OfflinePlayer getPlayer() {
        return Bukkit.getPlayer(getPlayerId());
    }
    //endregion
}
