package org.halvors.electrometrics.client.gui.configuration;

import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries.IConfigEntry;
import cpw.mods.fml.client.config.IConfigElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import org.halvors.electrometrics.Electrometrics;
import org.halvors.electrometrics.client.gui.configuration.category.CategoryEntryClient;
import org.halvors.electrometrics.client.gui.configuration.category.CategoryEntryGeneral;
import org.halvors.electrometrics.client.gui.configuration.category.CategoryEntryIntegration;
import org.halvors.electrometrics.client.gui.configuration.category.CategoryEntryMachine;
import org.halvors.electrometrics.common.ConfigurationManager;
import org.halvors.electrometrics.common.Reference;
import org.halvors.electrometrics.common.util.LanguageUtils;

import java.util.ArrayList;
import java.util.List;

public class GuiConfiguration extends GuiConfig {
    private static final List<IConfigElement> configElements = new ArrayList<>();

    static {
        register(Configuration.CATEGORY_GENERAL, CategoryEntryGeneral.class);
        register(ConfigurationManager.CATEGORY_MACHINE, CategoryEntryMachine.class);
        register(ConfigurationManager.CATEGORY_INTEGRATION, CategoryEntryIntegration.class);
        register(ConfigurationManager.CATEGORY_CLIENT, CategoryEntryClient.class);
    }

    public GuiConfiguration(GuiScreen parent) {
        super(parent, configElements, Reference.ID, false, false, Reference.NAME);

        titleLine2 = Electrometrics.getConfiguration().getConfigFile().getAbsolutePath();
    }

    private static void register(String category, Class<? extends IConfigEntry> configEntryClass) {
        configElements.add(new DummyCategoryElement(LanguageUtils.localize("gui.config.category." + category), "gui.config.category." + category, configEntryClass));
    }
}