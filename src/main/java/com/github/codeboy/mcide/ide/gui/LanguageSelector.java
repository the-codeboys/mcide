package com.github.codeboy.mcide.ide.gui;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.piston4j.api.Runtime;
import ml.codeboy.bukkitbootstrap.gui.MultiPageGui;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class LanguageSelector extends MultiPageGui {
    private final Consumer<Runtime> consumer;

    public LanguageSelector(Consumer<Runtime> consumer) {
        super(Mcide.getPlugin(Mcide.class),Mcide.getPiston().getRuntimes().size(),Message.SELECT_LANGUAGE);
        this.consumer = consumer;
        List<Runtime> runtimes = Mcide.getPiston().getRuntimes();
        for (Runtime runtime : runtimes) {
            addRuntime(runtime);
        }
    }

    private void addRuntime(Runtime runtime) {
        ItemStack runtimeItem = createItem(Material.PAPER, runtime.getLanguage(), runtime.getAliases());
        addItem(runtimeItem, p -> {
            p.closeInventory();
            consumer.accept(runtime);
        });
    }
}
