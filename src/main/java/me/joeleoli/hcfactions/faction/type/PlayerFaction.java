package me.joeleoli.hcfactions.faction.type;

import com.doctordark.util.BukkitUtils;
import com.doctordark.util.GenericUtils;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.PersistableLocation;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import me.joeleoli.hcfactions.ConfigurationService;
import me.joeleoli.hcfactions.FactionsPlugin;
import me.joeleoli.hcfactions.faction.FactionMember;
import me.joeleoli.hcfactions.faction.event.*;
import me.joeleoli.hcfactions.faction.struct.Raidable;
import me.joeleoli.hcfactions.file.DataFactory;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.joeleoli.hcfactions.faction.event.cause.FactionLeaveCause;
import me.joeleoli.hcfactions.faction.struct.RegenStatus;
import me.joeleoli.hcfactions.faction.struct.Relation;
import me.joeleoli.hcfactions.faction.struct.Role;
import me.joeleoli.hcfactions.timer.type.TeleportTimer;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class PlayerFaction extends ClaimableFaction implements Raidable {

	private static UUID[] EMPTY_UUID_ARRAY = {};
	// The UUID is the Faction unique ID.
	protected Map<UUID, Relation> requestedRelations = new HashMap<>();
	protected Map<UUID, Relation> relations = new HashMap<>();
	protected Map<UUID, FactionMember> members = new HashMap<>();
	protected Set<String> invitedPlayerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); // we are storing names as offline lookups are slow
	protected PersistableLocation home;
	protected String announcement;
	protected boolean open;
	protected int balance;
	protected int lives;
	protected double deathsUntilRaidable = 1.0D;
	protected long regenCooldownTimestamp;
	@Getter
	@Setter
	private transient UUID focused;
	@Getter
	private long lastDtrUpdateTimestamp;

	public PlayerFaction(String name) {
		super(name);
	}

	public PlayerFaction(Map<String, Object> map) {
		super(map);

		for (Entry<String, FactionMember> entry : GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet()) {
			if (entry.getValue() != null) {
				this.members.put(UUID.fromString(entry.getKey()), entry.getValue());
			}
		}

		this.invitedPlayerNames.addAll(GenericUtils.createList(map.get("invitedPlayerNames"), String.class));

		Object object = map.get("home");
		if (object != null) this.home = ((PersistableLocation) object);

		object = map.get("announcement");
		if (object != null) this.announcement = (String) object;

		for (Entry<String, String> entry : GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet()) {
			relations.put(UUID.fromString(entry.getKey()), Relation.valueOf(entry.getValue()));
		}

		for (Entry<String, String> entry : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet()) {
			requestedRelations.put(UUID.fromString(entry.getKey()), Relation.valueOf(entry.getValue()));
		}

		this.open = (Boolean) map.get("open");
		this.balance = (Integer) map.get("balance");
		this.lives = (Integer) map.get("lives");
		this.deathsUntilRaidable = (Double) map.get("deathsUntilRaidable");
		this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
		this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = super.serialize();

		Map<String, String> relationSaveMap = new HashMap<>(relations.size());
		for (Entry<UUID, Relation> entry : relations.entrySet()) {
			relationSaveMap.put(entry.getKey().toString(), entry.getValue().name());
		}
		map.put("relations", relationSaveMap);

		Map<String, String> requestedRelationsSaveMap = new HashMap<>(requestedRelations.size());
		for (Entry<UUID, Relation> entry : requestedRelations.entrySet()) {
			requestedRelationsSaveMap.put(entry.getKey().toString(), entry.getValue().name());
		}
		map.put("requestedRelations", requestedRelationsSaveMap);

		Set<Entry<UUID, FactionMember>> entrySet = this.members.entrySet();
		Map<String, FactionMember> saveMap = new LinkedHashMap<>(this.members.size());
		for (Entry<UUID, FactionMember> entry : entrySet) {
			saveMap.put(entry.getKey().toString(), entry.getValue());
		}

		map.put("members", saveMap);
		map.put("invitedPlayerNames", new ArrayList<>(invitedPlayerNames));
		if (home != null) map.put("home", home);
		if (announcement != null) map.put("announcement", announcement);
		map.put("open", open);
		map.put("balance", balance);
		map.put("lives", lives);
		map.put("deathsUntilRaidable", deathsUntilRaidable);
		map.put("regenCooldownTimestamp", Long.toString(regenCooldownTimestamp));
		map.put("lastDtrUpdateTimestamp", Long.toString(lastDtrUpdateTimestamp));

		return map;
	}

	public boolean addMember(CommandSender sender, @Nullable Player player, UUID playerUUID, FactionMember factionMember) {
		if (members.containsKey(playerUUID)) {
			return false;
		}

		PlayerJoinFactionEvent eventPre = new PlayerJoinFactionEvent(sender, player, playerUUID, this);
		Bukkit.getPluginManager().callEvent(eventPre);
		if (eventPre.isCancelled()) {
			return false;
		}

		// Set the player as a member before calling the
		// event so we can change the scoreboard.
		lastDtrUpdateTimestamp = System.currentTimeMillis();
		invitedPlayerNames.remove(factionMember.getName());
		members.put(playerUUID, factionMember);
		Bukkit.getPluginManager().callEvent(new PlayerJoinedFactionEvent(sender, player, playerUUID, this));

		return true;
	}

	public boolean removeMember(CommandSender sender, @Nullable Player player, UUID playerUUID, boolean kick, boolean force) {
		if (!this.members.containsKey(playerUUID)) {
			return true;
		}

		// Call pre event.
		PlayerLeaveFactionEvent preEvent = new PlayerLeaveFactionEvent(sender, player, playerUUID, this, FactionLeaveCause.LEAVE, kick, false);
		Bukkit.getPluginManager().callEvent(preEvent);
		if (preEvent.isCancelled()) {
			return false;
		}

		this.members.remove(playerUUID);
		this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));

		// Call after event.
		PlayerLeftFactionEvent event = new PlayerLeftFactionEvent(sender, player, playerUUID, this, FactionLeaveCause.LEAVE, kick, false);
		Bukkit.getPluginManager().callEvent(event);

		return true;
	}

	/**
	 * Gets a list of faction UUIDs that are allied to this {@link PlayerFaction}.
	 *
	 * @return mutable list of UUIDs
	 */
	public Collection<UUID> getAllied() {
		return Maps.filterValues(relations, new Predicate<Relation>() {
			@Override
			public boolean apply(@Nullable Relation relation) {
				return relation == Relation.ALLY;
			}
		}).keySet();
	}

	/**
	 * Gets a list of {@link PlayerFaction}s that are allied to this {@link PlayerFaction}.
	 *
	 * @return mutable list of {@link PlayerFaction}s
	 */
	public List<PlayerFaction> getAlliedFactions() {
		Collection<UUID> allied = getAllied();
		Iterator<UUID> iterator = allied.iterator();
		List<PlayerFaction> results = new ArrayList<>(allied.size());
		while (iterator.hasNext()) {
			Faction faction = FactionsPlugin.getInstance().getFactionManager().getFaction(iterator.next());
			if (faction instanceof PlayerFaction) {
				results.add((PlayerFaction) faction);
			} else iterator.remove();
		}

		return results;
	}

	public Map<UUID, Relation> getRequestedRelations() {
		return requestedRelations;
	}

	public Map<UUID, Relation> getRelations() {
		return relations;
	}

	/**
	 * Gets the members in this {@link PlayerFaction}.
	 * <p>
	 * <p>The key is the {@link UUID} of the member
	 * <p>
	 * <p>The value is the {@link FactionMember}
	 *
	 * @return map of members.
	 */
	public Map<UUID, FactionMember> getMembers() {
		return ImmutableMap.copyOf(members);
	}

	/**
	 * Gets the online {@link Player}s in this {@link Faction}.
	 *
	 * @return set of online {@link Player}s
	 */
	public Set<Player> getOnlinePlayers() {
		return getOnlinePlayers(null);
	}

	/**
	 * Gets the online {@link Player}s in this {@link Faction} that are visible to a {@link
	 * CommandSender}.
	 *
	 * @param sender the {@link CommandSender} to get for
	 * @return a set of online players visible to sender
	 */
	public Set<Player> getOnlinePlayers(CommandSender sender) {
		Set<Entry<UUID, FactionMember>> entrySet = getOnlineMembers(sender).entrySet();
		Set<Player> results = new HashSet<>(entrySet.size());
		for (Entry<UUID, FactionMember> entry : entrySet) {
			results.add(Bukkit.getPlayer(entry.getKey()));
		}

		return results;
	}

	/**
	 * Gets the online members in this {@link Faction}.
	 * <p>
	 * <p>The key is the {@link UUID} of the member
	 * <p>
	 * <p>The value is the {@link FactionMember}
	 *
	 * @return an immutable set of online members
	 */
	public Map<UUID, FactionMember> getOnlineMembers() {
		return getOnlineMembers(null);
	}

	/**
	 * Gets the online members in this {@link Faction} that are visible to a {@link CommandSender}.
	 * <p>
	 * <p>The key is the {@link UUID} of the member
	 * <p>
	 * <p>The value is the {@link FactionMember}
	 *
	 * @param sender the {@link CommandSender} to get for
	 * @return a set of online members visible to sender
	 */
	public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender) {
		Player senderPlayer = sender instanceof Player ? ((Player) sender) : null;
		Map<UUID, FactionMember> results = new HashMap<>();
		for (Entry<UUID, FactionMember> entry : members.entrySet()) {
			Player target = Bukkit.getPlayer(entry.getKey());
			if (target == null || (senderPlayer != null && !senderPlayer.canSee(target))) {
				continue;
			}

			results.put(entry.getKey(), entry.getValue());
		}

		return results;
	}

	/**
	 * Gets the leading {@link FactionMember} of this {@link Faction}.
	 *
	 * @return the leading {@link FactionMember}
	 */
	public FactionMember getLeader() {
		Map<UUID, FactionMember> members = this.members;
		for (Entry<UUID, FactionMember> entry : members.entrySet()) {
			if (entry.getValue().getRole() == Role.LEADER) {
				return entry.getValue();
			}
		}

		return null;
	}

	/**
	 * Gets the {@link FactionMember} with a specific name.
	 *
	 * @param memberName the id to search for
	 * @return the {@link FactionMember} or null if is not a member
	 * @deprecated uses hanging offline player method
	 */
	@Deprecated
	public FactionMember getMember(String memberName) {
		UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId(); // TODO: breaking
		return uuid == null ? null : members.get(uuid);
	}

	/**
	 * Gets the {@link FactionMember} of a {@link Player}.
	 *
	 * @param player the {@link Player} to get for
	 * @return the {@link FactionMember} or null if is not a member
	 */
	public FactionMember getMember(Player player) {
		return this.getMember(player.getUniqueId());
	}

	/**
	 * Gets the {@link FactionMember} with a specific {@link UUID}.
	 *
	 * @param memberUUID the {@link UUID} to get for
	 * @return the {@link FactionMember} or null if is not a member
	 */
	public FactionMember getMember(UUID memberUUID) {
		return members.get(memberUUID);
	}

	/**
	 * Gets the names of the players that have been invited to join this {@link PlayerFaction}.
	 *
	 * @return set of invited player names
	 */
	public Set<String> getInvitedPlayerNames() {
		return invitedPlayerNames;
	}

	public Location getHome() {
		return home == null ? null : home.getLocation();
	}

	public void setHome(@Nullable Location home) {
		if (home == null && this.home != null) {
			TeleportTimer timer = FactionsPlugin.getInstance().getTimerManager().getTeleportTimer();
			for (Player player : getOnlinePlayers()) {
				Location destination = timer.getDestination(player);
				if (Objects.equal(destination, this.home.getLocation())) {
					timer.clearCooldown(player);
					player.sendMessage(ChatColor.RED + "Your faction home was unset, so your " + timer.getDisplayName() + ChatColor.RED + " timer has been cancelled");
				}
			}
		}

		this.home = home == null ? null : new PersistableLocation(home);
	}

	public String getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(@Nullable String announcement) {
		this.announcement = announcement;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	@Override
	public boolean isRaidable() {
		return deathsUntilRaidable <= 0;
	}

	@Override
	public double getDeathsUntilRaidable() {
		return this.getDeathsUntilRaidable(true);
	}

	@Override
	public double getMaximumDeathsUntilRaidable() {
		if (members.size() == 1) {
			return 1.1;
		}
		if (members.size() == 2) {
			return 2.1;
		}

		return Math.min(ConfigurationService.MAXIMUM_DTR, members.size() * 0.9);
	}

	public double getDeathsUntilRaidable(boolean updateLastCheck) {
		if (updateLastCheck) this.updateDeathsUntilRaidable();
		return deathsUntilRaidable;
	}

	public ChatColor getDtrColour() {
		this.updateDeathsUntilRaidable();
		if (deathsUntilRaidable < 0) {
			return ChatColor.RED;
		} else if (deathsUntilRaidable < 1) {
			return ChatColor.YELLOW;
		} else {
			return ChatColor.GREEN;
		}
	}

	/**
	 * Updates the deaths until raidable value depending how much is gained every x seconds as set in
	 * configuration.
	 */
	private void updateDeathsUntilRaidable() {
		if (this.getRegenStatus() == RegenStatus.REGENERATING) {
			long now = System.currentTimeMillis();
			long millisPassed = now - this.lastDtrUpdateTimestamp;
			if (millisPassed >= ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES) {
				long remainder = millisPassed % ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES; // the remaining time until the next update
				int multiplier = (int) (((double) millisPassed + remainder) / ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES);
				double increase = multiplier * ConfigurationService.DTR_INCREMENT_BETWEEN_UPDATES;

				this.lastDtrUpdateTimestamp = now - remainder;
				this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
			}
		}
	}

	@Override
	public double setDeathsUntilRaidable(double deathsUntilRaidable) {
		return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
	}

	private double setDeathsUntilRaidable(double deathsUntilRaidable, boolean limit) {
		deathsUntilRaidable = Math.round(deathsUntilRaidable * 100.0) / 100.0; // remove trailing numbers after decimal
		if (limit) {
			deathsUntilRaidable = Math.min(deathsUntilRaidable, getMaximumDeathsUntilRaidable());
		}

		// the DTR is the same, don't call an event
		if (Math.abs(deathsUntilRaidable - this.deathsUntilRaidable) != 0) {
			FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				deathsUntilRaidable = Math.round(event.getNewDtr() * 100.0) / 100.0;
				if (deathsUntilRaidable > 0 && this.deathsUntilRaidable <= 0) {
					// Inform the server for easier log lookups for 'insiding' etc.
					FactionsPlugin.getInstance().getLogger().info("Faction " + getName() + " is now raidable.");
				}

				this.lastDtrUpdateTimestamp = System.currentTimeMillis();
				return this.deathsUntilRaidable = deathsUntilRaidable;
			}
		}

		return this.deathsUntilRaidable;
	}

	protected long getRegenCooldownTimestamp() {
		return regenCooldownTimestamp;
	}

	@Override
	public long getRemainingRegenerationTime() {
		return regenCooldownTimestamp == 0L ? 0L : regenCooldownTimestamp - System.currentTimeMillis();
	}

	@Override
	public void setRemainingRegenerationTime(long millis) {
		long systemMillis = System.currentTimeMillis();
		this.regenCooldownTimestamp = systemMillis + millis;

		// needs to be multiplied by 2 because as soon as they lose regeneration delay, the timestamp will update
		this.lastDtrUpdateTimestamp = systemMillis + (ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES * 2);
	}

	@Override
	public RegenStatus getRegenStatus() {
		if (getRemainingRegenerationTime() > 0L) {
			return RegenStatus.PAUSED;
		} else if (getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
			return RegenStatus.REGENERATING;
		} else {
			return RegenStatus.FULL;
		}
	}

	public void printDetails(CommandSender sender) {
		String leaderName = null;
		HashSet<String> allyNames = new HashSet<String>(1);
		Iterator<Entry<UUID, Relation>> combinedKills = this.relations.entrySet().iterator();

		PlayerFaction playerFaction;

		while (combinedKills.hasNext()) {
			Entry<UUID, Relation> memberNames = combinedKills.next();
			Faction captainNames = FactionsPlugin.getInstance().getFactionManager().getFaction(memberNames.getKey());
			
			if (captainNames instanceof PlayerFaction) {
				playerFaction = (PlayerFaction) captainNames;
				allyNames.add(playerFaction.getDisplayName(sender) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
			}
		}

		HashSet<String> memberNames = new HashSet<>();
		HashSet<String> captainNames = new HashSet<>();
		HashSet<String> coleaderNames = new HashSet<>();
		Iterator<Entry<UUID, FactionMember>> playerFaction1 = this.members.entrySet().iterator();

		while (playerFaction1.hasNext()) {
			Entry<UUID, FactionMember> entry = playerFaction1.next();
			FactionMember factionMember = entry.getValue();
			Player target = factionMember.toOnlinePlayer();

			int kills;
			ChatColor color;

			if(target == null) {
				kills = 0;
				color = ChatColor.GRAY;
			} else {
				kills = DataFactory.getKills(target);
				color = ChatColor.GREEN;
			}

			if(FactionsPlugin.getInstance().getDeathBanManager().getDeathBans().containsKey(factionMember.getUniqueId())) {
				color = ChatColor.RED;
			}

			String memberName = color + factionMember.getName() + ChatColor.GOLD + '[' + kills + ']';

			switch (factionMember.getRole()) {
				case LEADER: {
					leaderName = memberName;
					continue;
				}
				case COLEADER: {
					coleaderNames.add(memberName);
					continue;
				}
				case CAPTAIN: {
					captainNames.add(memberName);
					continue;
				}
				case MEMBER: {
					memberNames.add(memberName);
				}
			}
		}

		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
		sender.sendMessage(ChatColor.AQUA + this.getDisplayName(sender) + ChatColor.GRAY + " (" + this.getOnlineMembers().size() + "/" + this.getMembers().size() + " Online) " + ChatColor.GRAY + " - " + ChatColor.YELLOW + "Coords: " + ChatColor.RED + (this.home == null ? "None" : ChatColor.RED.toString() + this.home.getLocation().getBlockX() + " | " + this.home.getLocation().getBlockZ()));

		if (!allyNames.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "Allies: " + StringUtils.join(allyNames, ChatColor.GRAY + ", "));
		}

		if (leaderName != null) {
			sender.sendMessage(ChatColor.YELLOW + "Leader: " + ChatColor.RED + leaderName);
		}

		if (!coleaderNames.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "Co-Leaders: " + ChatColor.RED + StringUtils.join(coleaderNames, ChatColor.GRAY + ", "));
		}

		if (!captainNames.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "Captains: " + ChatColor.RED + StringUtils.join(captainNames, ChatColor.GRAY + ", "));
		}

		if (!memberNames.isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "Members: " + ChatColor.RED + StringUtils.join(memberNames, ChatColor.GRAY + ", "));
		}

		if (sender instanceof Player) {
			Faction playerFaction2 = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction((Player) sender);
			if (playerFaction2 != null && playerFaction2.equals(this)) {
				sender.sendMessage(ChatColor.YELLOW + "Faction Lives: " + ChatColor.RED + this.lives + ChatColor.YELLOW + ", " + ChatColor.YELLOW + "Balance: " + ChatColor.RED + '$' + this.balance);
			}
		}

		sender.sendMessage(ChatColor.YELLOW + "DTR: " + this.getRegenStatus().getSymbol() + this.getDtrColour() + JavaUtils.format(getDeathsUntilRaidable(false)));

		long dtrRegenRemaining = this.getRemainingRegenerationTime();

		if (dtrRegenRemaining > 0L) {
			sender.sendMessage(ChatColor.YELLOW + "DTR Freeze: " + ChatColor.GREEN + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));
		}

		if (sender instanceof Player) {
			Faction playerFaction2 = FactionsPlugin.getInstance().getFactionManager().getPlayerFaction((Player) sender);
			if (playerFaction2 != null && playerFaction2.equals(this) && this.announcement != null) {
				sender.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.LIGHT_PURPLE + this.announcement);
			}
		}

		sender.sendMessage(ChatColor.GOLD + BukkitUtils.STRAIGHT_LINE_DEFAULT);
	}

	/**
	 * Sends a message to all online {@link FactionMember}s.
	 *
	 * @param message the message to send
	 */
	public void broadcast(String message) {
		broadcast(message, EMPTY_UUID_ARRAY);
	}

	/**
	 * Sends an array of messages to all online {@link FactionMember}s.
	 *
	 * @param messages the messages to send.
	 */
	public void broadcast(String[] messages) {
		broadcast(messages, EMPTY_UUID_ARRAY);
	}

	/**
	 * Sends a message to all online {@link FactionMember}s ignoring those selected in the var-args.
	 *
	 * @param message the message to send.
	 * @param ignore  the {@link FactionMember} with {@link UUID}s not to send message to
	 */
	public void broadcast(String message, @Nullable UUID... ignore) {
		this.broadcast(new String[]{message}, ignore);
	}

	/**
	 * Sends an array of messages to all online {@link FactionMember}s ignoring those selected in the
	 * var-args.
	 *
	 * @param messages the message to send
	 * @param ignore   the {@link FactionMember} with {@link UUID}s not to send message to
	 */
	public void broadcast(String[] messages, UUID... ignore) {
		Preconditions.checkNotNull(messages, "Messages cannot be null");
		Preconditions.checkArgument(messages.length > 0, "Message array cannot be empty");
		Collection<Player> players = getOnlinePlayers();
		Collection<UUID> ignores = ignore.length == 0 ? Collections.emptySet() : Sets.newHashSet(ignore);

		for (Player player : players) {
			if (!ignores.contains(player.getUniqueId())) {
				for (String message : messages) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
				}
			}
		}
	}

}