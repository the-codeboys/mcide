package com.github.codeboy.mcide.ide.gui;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.gui.Gui;
import com.github.codeboy.piston4j.api.Runtime;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class LanguageSelector extends Gui {
    private final Consumer<Runtime>consumer;

    public LanguageSelector(Consumer<Runtime> consumer) {
        super(Mcide.getPlugin(Mcide.class), Math.min((Mcide.getPiston().getRuntimes().size()/9+1)*9,54), Message.SELECT_LANGUAGE);
        this.consumer=consumer;
        List<Runtime> runtimes = Mcide.getPiston().getRuntimes();
        for (int i = 0; i < runtimes.size(); i++) {
            if(i>=getInventory().getSize())
                return;
            Runtime runtime = runtimes.get(i);
            addRuntime(runtime);
        }
    }

    private void addRuntime(Runtime runtime){
        ItemStack runtimeItem=createItem(Material.PAPER, runtime.getLanguage(),runtime.getAliases());
        addItem(runtimeItem,p-> {
            p.closeInventory();
            consumer.accept(runtime);
        });
    }
}
