package minestack.plugin.roles;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RolesCommand {

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("roles")
                .requires(source -> source.getSender().hasPermission("minestack.command.roles"))
                .build();
    }
}
