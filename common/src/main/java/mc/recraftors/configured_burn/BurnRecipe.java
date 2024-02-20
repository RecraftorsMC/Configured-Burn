package mc.recraftors.configured_burn;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class BurnRecipe implements Recipe<Inventory> {
    private static RecipeType<BurnRecipe> recipeType;
    public static final Supplier<RecipeType<BurnRecipe>> SUPPLIER = () -> recipeType;

    public static void init(RecipeType<BurnRecipe> type) {
        if (recipeType == null) recipeType = type;
    }

    private final Identifier id;

    private final Item item;
    private final TagKey<Item> tag;
    private final short priority;
    private final int time;
    private final CompatBurnTime compatModule;
    private final ItemPredicate[] conditions;

    public BurnRecipe(Identifier id, Item item, TagKey<Item> tag, short priority, int time, CompatBurnTime compatModule, ItemPredicate[] conditions) {
        this.id = id;
        this.item = item;
        this.tag = tag;
        this.priority = priority;
        this.time = time;
        this.compatModule = compatModule == null ? CompatBurnTime.DEFAULT : compatModule;
        this.conditions = conditions;
    }

    void write(PacketByteBuf buf) {
        Identifier i1 = this.item == null ? Registry.ITEM.getDefaultId() : Registry.ITEM.getId(this.item);
        Identifier i2 = this.tag == null ? new Identifier("null") : this.tag.id();
        buf.writeIdentifier(i1);
        buf.writeIdentifier(i2);
        buf.writeShort(this.priority);
        buf.writeInt(this.time);
        this.compatModule.write(buf);
        // conditions are deemed irrelevant on the client so not sent
    }

    ConfiguredBurnTime.CustomFuelEntry toEntry() {
        return new ConfiguredBurnTime.CustomFuelEntry(this.time, this.priority, this.item, this.tag, this.compatModule, this.conditions);
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return new ItemStack(Registry.ITEM.get(Registry.ITEM.getDefaultId()));
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return new ItemStack(Registry.ITEM.get(Registry.ITEM.getDefaultId()));
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.SUPPLIER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SUPPLIER.get();
    }

    public static class CompatBurnTime {
        public static final CompatBurnTime DEFAULT = new CompatBurnTime(0) {
            @Override
            void write(PacketByteBuf buf) {
                buf.writeByte(0);
            }
        };

        private final int createOverheatTime;

        private CompatBurnTime(int createOverheatTime) {
            this.createOverheatTime = createOverheatTime;
        }

        public static CompatBurnTime of(int createOverheatTime) {
            if (createOverheatTime == -1) {
                return DEFAULT;
            }
            return new CompatBurnTime(createOverheatTime);
        }

        public int getCreateOverheatTime() {
            return createOverheatTime;
        }

        void write(PacketByteBuf buf) {
            buf.writeByte(1);
            buf.writeInt(this.createOverheatTime);
        }

        @Override
        public String toString() {
            return String.format("%s[createOverheatTime=%d]", this.getClass().getSimpleName(), this.createOverheatTime);
        }
    }

    public static class Serializer implements RecipeSerializer<BurnRecipe> {
        private static Serializer instance;
        public static final Supplier<Serializer> SUPPLIER = () -> instance;

        public static RecipeSerializer<?> init() {
            if (instance == null) {
                instance = new Serializer();
            }
            return instance;
        }

        @Override
        public BurnRecipe read(Identifier id, JsonObject json) {
            Item item = JsonHelper.getItem(json, "item");
            TagKey<Item> tag;
            if (!json.has("tag")) tag = null;
            else tag = TagKey.of(Registry.ITEM.getKey(), Identifier.tryParse(JsonHelper.asString(json, "tag")));
            short priority = JsonHelper.getShort(json, "priority", (short) 0);
            int time = JsonHelper.getInt(json, "time");
            CompatBurnTime compatModule = readCompatModule(json);
            ItemPredicate[] conditions;
            if (json.has("conditions")) conditions = ItemPredicate.deserializeAll(json.get("conditions"));
            else conditions = new ItemPredicate[0];
            if (item == null && tag == null) {
                return null;
            }
            return new BurnRecipe(id, item, tag, priority, time, compatModule, conditions);
        }

        @Override
        public BurnRecipe read(Identifier id, PacketByteBuf buf) {
            Identifier i1 = buf.readIdentifier();
            Identifier i2 = buf.readIdentifier();
            Item item = Registry.ITEM.get(i1);
            TagKey<Item> tag = (i2.equals(new Identifier("null"))) ? null : TagKey.of(Registry.ITEM.getKey(), i2);
            short priority = buf.readShort();
            int time = buf.readInt();
            CompatBurnTime compatModule = readCompatModule(buf);
            // conditions are deemed irrelevant on the client so not read
            if (i1 == Registry.ITEM.getDefaultId() && tag == null) {
                return null;
            }
            return new BurnRecipe(id, item, tag, priority, time, compatModule, new ItemPredicate[0]);
        }

        @Override
        public void write(PacketByteBuf buf, BurnRecipe recipe) {
            recipe.write(buf);
        }

        public CompatBurnTime readCompatModule(JsonObject object) {
            if (!JsonHelper.hasJsonObject(object, "compat")) return CompatBurnTime.DEFAULT;
            JsonObject json = object.getAsJsonObject("compat");
            int createOverheat = JsonHelper.getInt(json, "createOverheatTime", -1);
            return new CompatBurnTime(createOverheat);
        }

        public CompatBurnTime readCompatModule(PacketByteBuf buf) {
            boolean b = buf.readByte() == 0;
            if (b) return CompatBurnTime.DEFAULT;
            int createOverheat = buf.readInt();
            return CompatBurnTime.of(createOverheat);
        }
    }

    @Override
    public String toString() {
        return this.toEntry().toString();
    }
}
