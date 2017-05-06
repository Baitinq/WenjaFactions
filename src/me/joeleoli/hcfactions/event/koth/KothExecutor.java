package me.joeleoli.hcfactions.event.koth;

import com.doctordark.util.command.ArgumentExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.event.koth.argument.KothHelpArgument;
import me.joeleoli.hcfactions.event.koth.argument.KothNextArgument;
import me.joeleoli.hcfactions.event.koth.argument.KothScheduleArgument;
import me.joeleoli.hcfactions.event.koth.argument.KothSetCapDelayArgument;

public class KothExecutor extends ArgumentExecutor {

	private KothScheduleArgument kothScheduleArgument;

	public KothExecutor(FactionsPlugin plugin) {
		super("koth");

		addArgument(new KothHelpArgument(this));
		addArgument(new KothNextArgument(plugin));
		addArgument(this.kothScheduleArgument = new KothScheduleArgument(plugin));
		addArgument(new KothSetCapDelayArgument(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			this.kothScheduleArgument.onCommand(sender, command, label, args);
			return true;
		}

		return super.onCommand(sender, command, label, args);
	}

}