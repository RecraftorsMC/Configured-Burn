package mc.recraftors.configured_burn;

import io.netty.util.internal.UnstableApi;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.*;

public class ConfiguredBurnTime {
	public static final String MOD_ID = "configured_burn";

	private static final Collection<CustomFuelEntry> entries = new LinkedHashSet<>();
	private static final Map<Item, CustomFuelEntry> customFuelTimes = new HashMap<>();
	private static Map<Item, Integer> reconfiguredFuelTimes = null;

	public static Identifier modId(String s) {
		return new Identifier(MOD_ID, s);
	}

	public static void init() {
		PreLaunchUtils.LOGGER.info("Initialized mod [Configured Burn Time]");
	}

	public static void loadEntries(RecipeManager recipeManager) {
		entries.clear();
		customFuelTimes.clear();
		reconfiguredFuelTimes = null;
		recipeManager.listAllOfType(BurnRecipe.SUPPLIER.get()).forEach(r -> {
			CustomFuelEntry entry = r.toEntry();
			Iterator<CustomFuelEntry> iterator = entries.iterator();
			boolean b = true;
			while (iterator.hasNext()) {
				CustomFuelEntry e = iterator.next();
				if (e.time < 0) continue;
				boolean b1 = e.tag != null && entry.tag != null && e.tag.id().equals(entry.tag.id());
				if (e.item == entry.item || b1 || isItemInTag(e.tag, entry.item) || isItemInTag(entry.tag, e.item)) {
					if (entry.priority > e.priority) iterator.remove();
					else b = false;
					break;
				}
			}
			if (b) entries.add(entry);
		});
		PreLaunchUtils.LOGGER.info("[{}] Loaded {} fuel time reconfiguration entries", MOD_ID, entries.size());
	}

	public static Optional<CustomFuelEntry> getFuelEntry(ItemStack stack) {
		Item item = stack.getItem();
		if (stack.isEmpty()) return Optional.empty();
		if (!stack.hasNbt() && customFuelTimes.containsKey(item)) {
			return Optional.of(customFuelTimes.get(item));
		}
		int p = Short.MIN_VALUE;
		CustomFuelEntry c = null;
		for (CustomFuelEntry entry : entries) {
			if (entry.matches(stack) && entry.priority > p) {
				p = entry.priority;
				c = entry;
			}
		}
		if (c != null) {
			customFuelTimes.putIfAbsent(item, c);
			return Optional.of(c);
		} else {
			return Optional.empty();
		}
	}

	public static Optional<Integer> getFuelTime(ItemStack stack) {
		Item item = stack.getItem();
		if (stack.isEmpty()) return Optional.empty();
		if (!stack.hasNbt() && customFuelTimes.containsKey(item)) {
			return Optional.of(customFuelTimes.get(item).time);
		}
		int p = Short.MIN_VALUE;
		int t = 0;
		CustomFuelEntry c = null;
		for (CustomFuelEntry entry : entries) {
			if (entry.matches(stack) && entry.priority > p) {
				p = entry.priority;
				t = entry.time;
				c = entry;
			}
		}
		if (c != null) {
			customFuelTimes.putIfAbsent(item, c);
			return Optional.of(t);
		} else {
			return Optional.empty();
		}
	}

	@UnstableApi
	public static void updateFuelMap(Map<Item, Integer> map) {
		computeReconfiguredEntries();
        map.putAll(reconfiguredFuelTimes);
	}

	private static void computeReconfiguredEntries() {
		if (reconfiguredFuelTimes != null) return;
		final Map<Item, CustomFuelEntry> map = new IdentityHashMap<>();
		entries.forEach(entry -> {
			for (Item item : map.keySet()) {
				CustomFuelEntry v = map.get(item);
				if ((Objects.equals(entry.item, item) || isItemInTag(entry.tag, item)) && entry.priority > v.priority) {
					map.put(item, entry);
				}
			}
		});
		reconfiguredFuelTimes = new IdentityHashMap<>();
		map.forEach((k, v) -> reconfiguredFuelTimes.put(k, v.time));
	}

	public static boolean isItemInTag(TagKey<Item> tag, Item item) {
		if (tag == null || item == null) {
			return false;
		}
		Optional<RegistryEntryList.Named<Item>> o1 = Registries.ITEM.getEntryList(tag);
        return o1.map(registryEntries -> registryEntries.contains(Registries.ITEM.createEntry(item))).orElse(false);
    }

	public record CustomFuelEntry(int time, short priority, Item item, TagKey<Item> tag,
								  BurnRecipe.CompatBurnTime compat, ItemPredicate[] conditions) {
		public boolean matches(ItemStack stack) {
			for (ItemPredicate predicate : this.conditions) {
				// haha nbt go brr
				if (predicate.test(stack)) {
					return false;
				}
			}
			Item i = stack.getItem();
			return i != null && (Objects.equals(this.item, i) || isItemInTag(this.tag, i));
		}

		@Override
		public String toString() {
			return "CustomFuelEntry{" +
					"time=" + time +
					", priority=" + priority +
					", item=" + item +
					", tag=" + tag +
					", compat=" + compat +
					", conditions=" + Arrays.toString(conditions) +
					'}';
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof CustomFuelEntry entry)) return false;
            if (time != entry.time) return false;
			if (priority != entry.priority) return false;
			if (!Objects.equals(item, entry.item)) return false;
			if (!Objects.equals(tag, entry.tag)) return false;
			if (!Objects.equals(compat, entry.compat)) return false;
            return Arrays.mismatch(conditions, entry.conditions) == -1;
        }

		@Override
		public int hashCode() {
			int result = time;
			result = 31 * result + (int) priority;
			result = 31 * result + (item != null ? item.hashCode() : 0);
			result = 31 * result + (tag != null ? tag.hashCode() : 0);
			result = 31 * result + (compat != null ? compat.hashCode() : 0);
			result = 31 * result + Arrays.hashCode(conditions);
			return result;
		}
	}
}
