package com.github.codeboy.mcide.ide;

import com.github.codeboy.mcide.config.Message;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OwnedCodeProject extends CodeProject{
    private UUID ownerId;
    public OwnedCodeProject(String language, String title, UUID ownerId, MCCodeFile... MCCodeFiles) {
        super(language, title, MCCodeFiles);
        this.ownerId=ownerId;
    }

    public CodePlayer getOwner() {
        return CodePlayer.getCodePlayer(ownerId);
    }

    public void setOwner(CodePlayer owner) {
        this.ownerId = owner.getPlayerId();
    }

    public void save() {
        getOwner().save();
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(getOwner().getPlayerId());
    }

    @Override
    public void editFile(MCCodeFile file, Player player) {
        if (!isOwner(player)) {
            player.sendMessage(Message.NOT_PROJECT_OWNER);
        }else super.editFile(file, player);
    }

    @Override
    public void open(Player player) {
        if (!isOwner(player)) {
            player.sendMessage(Message.NOT_PROJECT_OWNER);
            return;
        }
        openProjectMenu(player);
    }
}
