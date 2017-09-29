package me.joeleoli.hcfactions.timer;

import com.doctordark.util.command.ArgumentExecutor;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.timer.argument.TimerCheckArgument;
import me.joeleoli.hcfactions.timer.argument.TimerSetArgument;

/**
 * Handles the execution and tab completion of the timer command.
 */
public class TimerExecutor extends ArgumentExecutor {

	public TimerExecutor(FactionsPlugin plugin) {
		super("timer");

		addArgument(new TimerCheckArgument(plugin));
		addArgument(new TimerSetArgument(plugin));
	}
}
