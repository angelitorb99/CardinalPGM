package in.twizmwaz.cardinal.module.modules.appliedRegion.type;

import in.twizmwaz.cardinal.module.modules.appliedRegion.AppliedRegion;
import in.twizmwaz.cardinal.module.modules.filter.FilterModule;
import in.twizmwaz.cardinal.module.modules.filter.FilterState;
import in.twizmwaz.cardinal.module.modules.regions.RegionModule;
import in.twizmwaz.cardinal.module.modules.regions.type.BlockRegion;
import in.twizmwaz.cardinal.module.modules.tntTracker.TntTracker;
import in.twizmwaz.cardinal.util.ChatUtils;
import in.twizmwaz.cardinal.util.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class BlockBreakRegion extends AppliedRegion {
    
    public BlockBreakRegion(RegionModule region, FilterModule filter, String message) {
        super(region, filter, message);
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (region.contains(new BlockRegion(null, event.getBlock().getLocation().toVector().add(new Vector(0.5, 0.5, 0.5)))) && (filter.evaluate(event.getPlayer()).equals(FilterState.DENY) || filter.evaluate(event.getBlock()).equals(FilterState.DENY))) {
            event.setCancelled(true);
            ChatUtils.sendWarningMessage(event.getPlayer(), message);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (region.contains(new BlockRegion(null, event.getBlockClicked().getRelative(event.getBlockFace()).getLocation().toVector().add(new Vector(0.5, 0.5, 0.5))))
                && (filter.evaluate(event.getPlayer()).equals(FilterState.DENY)
                || filter.evaluate(event.getBlockClicked().getRelative(event.getBlockFace())).equals(FilterState.DENY))) {
            event.setCancelled(true);
            ChatUtils.sendWarningMessage(event.getPlayer(), message);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Set<Block> blocksToRemove = new HashSet<>();
        for (Block block : event.blockList()) {
            if (region.contains(new BlockRegion(null, block.getLocation().toVector().add(new Vector(0.5, 0.5, 0.5))))) {
                if (TntTracker.getWhoPlaced(event.getEntity()) != null) {
                    if (Bukkit.getOfflinePlayer(TntTracker.getWhoPlaced(event.getEntity())).isOnline()) {
                        if (filter.evaluate(Bukkit.getPlayer(TntTracker.getWhoPlaced(event.getEntity()))).equals(FilterState.DENY) || filter.evaluate(block).equals(FilterState.DENY)) blocksToRemove.add(block);;
                    } else blocksToRemove.add(block);
                } else blocksToRemove.add(block);
            }
        }
        for (Block block : blocksToRemove) {
            event.blockList().remove(block);
        }
    }
}
