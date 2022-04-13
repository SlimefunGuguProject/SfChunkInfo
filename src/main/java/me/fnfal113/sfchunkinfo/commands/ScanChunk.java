package me.fnfal113.sfchunkinfo.commands;

import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.github.thebusybiscuit.slimefun4.utils.WorldUtils;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;

public class ScanChunk implements TabExecutor {

    private final Map<String, Integer> AMOUNT = new HashMap<>();
    private final Map<String, String> INFO = new HashMap<>();
    private final Map<String, Double> TIMINGS = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player player = (Player) sender;
            if(args.length == 0) {
                if (player.hasPermission("sfchunkinfo.scan")) {
                    Chunk chunk = player.getLocation().getChunk();

                    getAmount(chunk, player);

                } else {
                    player.sendMessage("你没有权限使用该指令");
                }
            } else {
                if (player.hasPermission("sfchunkinfo.scan.others")) {
                    Player target = Bukkit.getPlayer(args[0]);

                    if(target == null){
                        player.sendMessage("目标不在线或不存在");
                        return true;
                    }

                    Chunk chunk = target.getLocation().getChunk();
                    getAmountOthers(chunk, target, player);
                } else {
                    player.sendMessage("你没有权限使用该指令");
                }
            }
        }

        return true;
    }

    public void getAmount(Chunk chunk, Player player){
        if (!Slimefun.getProtectionManager().hasPermission(Bukkit.getOfflinePlayer(player.getUniqueId()), player.getLocation(),
                Interaction.PLACE_BLOCK)) {
            player.sendMessage("你没有扫描该区块的权限，该区块受到保护");
            return;
        }

        scanChunk(chunk);

        player.sendMessage(ChatColor.GOLD + "该区块中的Slimefun方块数量:", "");

        if (AMOUNT.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "该区块中没有Slimefun方块");
            return;
        }

        sendResults(player);
    }

    public void getAmountOthers(Chunk chunk, Player player, Player sender){
        scanChunk(chunk);

        sender.sendMessage(ChatColor.GOLD + "玩家 " + ChatColor.WHITE + player.getName() + ChatColor.GOLD + " 所在区块的Slimefun方块数量:", "");

        if (AMOUNT.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "玩家 " + ChatColor.WHITE + player.getName() + ChatColor.GOLD + " 所在区块没有Slimefun方块");
            return;
        }

        sendResults(sender);
    }

    public void scanChunk(Chunk chunk){
        for(int y = WorldUtils.getMinHeight(chunk.getWorld()); y <= chunk.getWorld().getMaxHeight() - 1; y++) {
            for(int x = 0; x <= 15; x++) {
                for(int z = 0; z <= 15; z++) {
                    Block sfBlock = chunk.getBlock(x, y, z);

                    if(BlockStorage.check(sfBlock) != null) {
                        TIMINGS.put(Objects.requireNonNull(BlockStorage.check(sfBlock)).getItemName(), TIMINGS.getOrDefault(Objects.requireNonNull(BlockStorage.check(sfBlock)).getItemName(), (double) 0)
                                + Double.parseDouble(Slimefun.getProfiler().getTime(sfBlock).substring(0, Slimefun.getProfiler().getTime(sfBlock).length() - 2)));
                        INFO.put(Objects.requireNonNull(BlockStorage.check(sfBlock)).getItemName(), Objects.requireNonNull(BlockStorage.check(sfBlock)).getAddon().getName());
                        AMOUNT.put(Objects.requireNonNull(BlockStorage.check(sfBlock)).getItemName(),  AMOUNT.getOrDefault(Objects.requireNonNull(BlockStorage.check(sfBlock)).getItemName(), 0) + 1);
                    }
                }
            }
        }
    }

    public void sendResults(Player player){
        AMOUNT.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(e -> player.sendMessage(e.getKey() + ": " + ChatColor.GREEN + e.getValue()));

        player.spigot().sendMessage(hoverInfo(INFO));
        player.spigot().sendMessage(hoverInfoTimings(TIMINGS));

        AMOUNT.clear();
        INFO.clear();
        TIMINGS.clear();
    }

    public TextComponent hoverInfo(Map<String, String> info){
        TextComponent infoAddon = new TextComponent("\n鼠标移至此处查看更多信息");
        infoAddon.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        infoAddon.setItalic(true);
        infoAddon.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text(info.toString().replace("{","").replace("}","").replace(", ", "\n").replace("=", ChatColor.WHITE + " | 来自: "))));

        return infoAddon;
    }

    public TextComponent hoverInfoTimings(Map<String, Double> timings){
        TextComponent infoChunk = new TextComponent("鼠标移至此处查看timings");
        infoChunk.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        infoChunk.setItalic(true);
        infoChunk.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GOLD + "timing" + "\n\n" + timings.toString().replace("{","").replace("}","").replace(", ", " ms\n").replace("=", ChatColor.WHITE + ": ").concat(ChatColor.WHITE + " ms"))));

        return infoChunk;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1){
            List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (Player player : players) {
                playerNames.add(player.getName());
            }

            return playerNames;
        }

        return null;
    }
}
