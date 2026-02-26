package minestack.permission;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import minestack.plugin.MinestackPlugin;
import net.kyori.adventure.util.TriState;

import java.util.UUID;

@RequiredArgsConstructor
public class PermissionRegistryImpl implements PermissionRegistry {
    private final MinestackPlugin plugin;

    @Override
    public TriState permission(@NonNull UUID uniqueId, @NonNull String node) {
        return TriState.NOT_SET;
    }
}
