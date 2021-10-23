package com.github.codeboy.mcide.ide;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.Util;
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

public class CodePlayer {
    private static final HashMap<UUID, CodePlayer> codePlayers = new HashMap<>();
    private final UUID playerId;
    private List<OwnedCodeProject> projects = new ArrayList<>();

    public CodePlayer(UUID playerId) {
        this.playerId = playerId;
        register(this);
    }

    public static void register(CodePlayer codePlayer) {
        codePlayers.put(codePlayer.playerId, codePlayer);
    }

    private static CodePlayer createCodePlayer(UUID id) {
        File saveFile = new File(Mcide.getPlugin(Mcide.class).getProjectsPath() + id);
        CodePlayer player = saveFile.exists() ? fromFile(saveFile) : new CodePlayer(id);
        player.init();
        return player;
    }

    private static CodePlayer fromFile(File file) {
        String json = Util.readFile(file);
        CodePlayer player = Mcide.gson().fromJson(json, CodePlayer.class);
        codePlayers.put(player.getPlayerId(), player);
        return player;
    }

    public static CodePlayer getCodePlayer(UUID id) {
        return codePlayers.containsKey(id) ? codePlayers.get(id) : createCodePlayer(id);
    }

    public static CodePlayer getCodePlayer(Player player) {
        return getCodePlayer(player.getUniqueId());
    }

    private void init() {
        for (CodeProject project : getProjects()) {
            project.init();
        }
    }

    public void save() {
        Mcide plugin = Mcide.getPlugin(Mcide.class);
        String json = Mcide.gson().toJson(this);
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

    //region getters and setters
    public List<OwnedCodeProject> getProjects() {
        return projects;
    }

    public void setProjects(List<OwnedCodeProject> projects) {
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
