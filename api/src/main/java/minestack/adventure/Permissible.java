package minestack.adventure;

import lombok.NonNull;
import net.kyori.adventure.util.TriState;

public interface Permissible {

    TriState permission(@NonNull String node);

}
