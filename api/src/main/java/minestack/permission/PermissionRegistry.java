package minestack.permission;

import lombok.NonNull;
import minestack.registry.Registry;
import net.kyori.adventure.util.TriState;

import java.util.UUID;

public interface PermissionRegistry extends Registry {

    TriState permission(@NonNull UUID uniqueId, @NonNull String node);

}
