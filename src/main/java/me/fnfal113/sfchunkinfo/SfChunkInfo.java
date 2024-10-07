package me.fnfal113.sfchunkinfo;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.fnfal113.sfchunkinfo.commands.ScanChunk;
import net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public final class SfChunkInfo extends JavaPlugin implements SlimefunAddon {

    private static SfChunkInfo instance;

    @Override
    public void onEnable() {
        setInstance(this);

        if (!getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin")) {
            getLogger().log(Level.SEVERE, "本插件需要 鬼斩前置库插件(GuizhanLibPlugin) 才能运行!");
            getLogger().log(Level.SEVERE, "从此处下载: https://50l.cc/gzlib");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Metrics(this, 13713);

        getLogger().info("**********************************************");
        getLogger().info("*                SfChunkInfo                 *");
        getLogger().info("*        作者: FN_FAL113 汉化: ybw0014         *");
        getLogger().info("*             Slimefun 附属插件                *");
        getLogger().info("*         扫描区块中的Slimefun方块数量           *");
        getLogger().info("**********************************************");


        Objects.requireNonNull(getCommand("sfchunkinfo")).setExecutor(new ScanChunk());

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if (getConfig().getBoolean("auto-update", true) && getDescription().getVersion().startsWith("Build")) {
            GuizhanUpdater.start(this, getFile(), "SlimefunGuguProject", "SfChunkInfo", "main");
        }
    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/SlimefunGuguProject/SfChunkInfo/issues";
    }

    private static void setInstance(SfChunkInfo ins) {
        instance = ins;
    }

    public static SfChunkInfo getInstance() {
        return instance;
    }

}
