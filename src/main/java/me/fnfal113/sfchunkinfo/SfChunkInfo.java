package me.fnfal113.sfchunkinfo;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import me.fnfal113.sfchunkinfo.commands.ScanChunk;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SfChunkInfo extends JavaPlugin implements SlimefunAddon {

    private static SfChunkInfo instance;

    @Override
    public void onEnable() {
        setInstance(this);
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

        if (getConfig().getBoolean("auto-update", true) && getDescription().getVersion().startsWith("Build ")) {
            new GuizhanBuildsUpdater(this, getFile(), "ybw0014", "SfChunkInfo", "main", false).start();
        }

    }

    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Override
    public String getBugTrackerURL() {
        return "https://github.com/ybw0014/SfChunkInfo/issues";
    }

    private static void setInstance(SfChunkInfo ins) {
        instance = ins;
    }

    public static SfChunkInfo getInstance() {
        return instance;
    }

}
