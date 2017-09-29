package me.joeleoli.hcfactions.event.conquest;

import com.doctordark.util.command.ArgumentExecutor;
import me.joeleoli.hcfactions.FactionsPlugin;

public class ConquestExecutor extends ArgumentExecutor {

	public ConquestExecutor(FactionsPlugin plugin) {
		super("conquest");
		addArgument(new ConquestSetpointsArgument(plugin));
	}
}
