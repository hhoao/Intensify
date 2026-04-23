package org.hhoa.mc.intensify.command;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.hhoa.mc.intensify.config.TranslatableTexts;
import org.hhoa.mc.intensify.registry.ConfigRegistry;

public class CommandIntensify extends CommandBase {
    private static final List<String> SUB_COMMANDS =
            Arrays.asList("stone_dropout_rate", "upgrade_multiplier", "attribute_multiplier");

    @Override
    public String getName() {
        return "intensify";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/intensify <stone_dropout_rate|upgrade_multiplier|attribute_multiplier> <value>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException {
        if (args.length != 2) {
            throw new WrongUsageException(getUsage(sender));
        }

        double value = parseDouble(args[1], 0.0D);
        switch (args[0]) {
            case "stone_dropout_rate":
                ConfigRegistry.stoneDropoutProbabilityConfig.getTotalRate().set(value);
                sender.sendMessage(TranslatableTexts.SET_STONE_DROP_RATE_TIP.component(value));
                break;
            case "upgrade_multiplier":
                ConfigRegistry.UPGRADE_MULTIPLIER.set(value);
                sender.sendMessage(TranslatableTexts.SET_UPGRADE_MULTIPLIER_TIP.component(value));
                break;
            case "attribute_multiplier":
                ConfigRegistry.ATTRIBUTE_MULTIPLIER.set(value);
                sender.sendMessage(TranslatableTexts.SET_ATTRIBUTE_MULTIPLIER_TIP.component(value));
                break;
            default:
                throw new WrongUsageException(getUsage(sender));
        }

        ConfigRegistry.save();
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server,
            ICommandSender sender,
            String[] args,
            @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, SUB_COMMANDS);
        }
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
