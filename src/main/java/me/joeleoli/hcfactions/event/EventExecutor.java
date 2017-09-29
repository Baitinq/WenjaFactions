package me.joeleoli.hcfactions.event;

import com.doctordark.util.command.ArgumentExecutor;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.event.argument.*;

/**
 * Handles the execution and tab completion of the event command.
 */
public class EventExecutor extends ArgumentExecutor {

	public EventExecutor(FactionsPlugin plugin) {
		super("event");

		addArgument(new EventCancelArgument(plugin));
		addArgument(new EventCreateArgument(plugin));
		addArgument(new EventDeleteArgument(plugin));
		addArgument(new EventRenameArgument(plugin));
		addArgument(new EventSetAreaArgument(plugin));
		addArgument(new EventSetCapzoneArgument(plugin));
		addArgument(new EventAddLootTableArgument(plugin));
		addArgument(new EventDelLootTableArgument(plugin));
		addArgument(new EventSetLootArgument(plugin));
		addArgument(new EventStartArgument(plugin));
		addArgument(new EventUptimeArgument(plugin));
	}
}
