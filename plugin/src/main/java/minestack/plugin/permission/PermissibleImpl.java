package minestack.plugin.permission;

import minestack.builtin.player.PlayerActor;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Set;

public final class PermissibleImpl extends PermissibleBase {
    private static final Field HUMAN_ENTITY_PERMISSIBLE_FIELD;

    static {
        try {
            Field humanEntityPermissibleField = Class.forName("org.bukkit.craftbukkit.entity.CraftHumanEntity").getDeclaredField("perm");
            humanEntityPermissibleField.setAccessible(true);
            HUMAN_ENTITY_PERMISSIBLE_FIELD = humanEntityPermissibleField;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final PlayerActor actor;

    public PermissibleImpl(PlayerActor actor) {
        super(actor.player());
        this.actor = actor;
    }

    @Override
    public boolean isPermissionSet(@NotNull String node) {
        return !actor.permission(node).equals(TriState.NOT_SET);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return isPermissionSet(perm.getName());
    }

    @Override
    public boolean hasPermission(@NotNull String node) {
        return actor.permission(node).toBooleanOrElse(false);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return new PermissionAttachment(plugin, this);
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Set.of();
    }

    @Override
    public boolean isOp() {
        return false;
    }

    @Override
    public void setOp(boolean value) {

    }

    public static void hookPlayer(Player player, PermissibleImpl permissible) throws IllegalAccessException {
        HUMAN_ENTITY_PERMISSIBLE_FIELD.set(player, permissible);
    }
}
