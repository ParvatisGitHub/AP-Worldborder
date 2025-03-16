package com.psg.worldborder;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;

public class CraftTeleporter {
    public static boolean teleportCraft(Craft craft, Player player, int x, int y, int z) {
        if (craft == null) {
            player.sendMessage("You must be piloting a craft!");
            return false;
        }

        try {

            MovecraftLocation currentLoc = craft.getHitBox().getMidPoint();
            int dx = x - currentLoc.getX();
            int dy = y - currentLoc.getY();
            int dz = z - currentLoc.getZ();

            craft.translate(dx, dy, dz);
            player.sendMessage("Teleported craft to " + x + ", " + y + ", " + z);
            return true;
        } catch (Exception e) {
            player.sendMessage("Failed to teleport craft: " + e.getMessage());
            return false;
        }
    }
}