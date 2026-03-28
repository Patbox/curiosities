package eu.pb4.curiosities.entity;

import eu.pb4.curiosities.item.CuriositiesItems;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jspecify.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

import java.util.Optional;

public class JukeboxMinecart extends AbstractMinecart implements PolymerEntity, ContainerSingleItem, WorldlyContainer {
    private final MusicHandler musicHandler = new MusicHandler();
    private int playingTicks = -1;
    private @Nullable Holder<JukeboxSong> song;
    private ItemStack musicDisc = ItemStack.EMPTY;

    protected JukeboxMinecart(EntityType<?> entityType, Level level) {
        super(entityType, level);
        EntityAttachment.ofTicking(musicHandler, this);
        this.setCustomDisplayBlockState(Optional.of(Blocks.JUKEBOX.defaultBlockState()));
    }

    private static void spawnMusicParticles(LevelAccessor level, Vec3 pos) {
        if (level instanceof ServerLevel serverLevel) {
            float f = (float) level.getRandom().nextInt(4) / 24.0F;
            serverLevel.sendParticles(ParticleTypes.NOTE, pos.x(), pos.y(), pos.z(), 0, f, 0.0F, 0.0F, 1.0F);
        }

    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 pos) {
        if (this.musicDisc.isEmpty()) {
            var stack = player.getItemInHand(hand);
            if (!stack.has(DataComponents.JUKEBOX_PLAYABLE)) {
                return InteractionResult.FAIL;
            }
            this.setTheItem(stack.split(1));
            return InteractionResult.SUCCESS_SERVER;
        } else {
            this.popOutTheItem();
            return InteractionResult.SUCCESS_SERVER;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.song != null) {
            if (!this.song.value().hasFinished(this.playingTicks) && this.playingTicks++ % 20 == 0) {
                level().gameEvent(GameEvent.JUKEBOX_PLAY, this.blockPosition(), GameEvent.Context.of(this));
                spawnMusicParticles(level(), this.position().add(0, this.getBbHeight(), 0));
            }
        }
    }

    public void popOutTheItem() {
        if (!this.level().isClientSide()) {
            ItemStack itemStack = this.getTheItem().copy();
            if (!itemStack.isEmpty()) {
                this.removeTheItem();
                this.musicHandler.stopPlaying();
                this.playingTicks = -1;
                this.song = null;
                Vec3 vec3 = this.getPosition(0).add(0, this.getBbHeight(), 0).offsetRandomXZ(this.random, 0.7F);
                ItemStack itemStack2 = itemStack.copy();
                ItemEntity itemEntity = new ItemEntity(this.level(), vec3.x(), vec3.y(), vec3.z(), itemStack2);
                itemEntity.setDefaultPickUpDelay();
                this.level().addFreshEntity(itemEntity);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.musicDisc = input.read("item", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        this.playingTicks = input.getIntOr("ticks_playing", -1);
        this.song = input.read("song", JukeboxSong.CODEC).orElse(null);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.storeNullable("item", ItemStack.OPTIONAL_CODEC, this.musicDisc);
        output.putInt("ticks_playing", this.playingTicks);
        output.storeNullable("song", JukeboxSong.CODEC, this.song);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    protected Item getDropItem() {
        return CuriositiesItems.JUKEBOX_MINECART;
    }

    @Override
    protected void destroy(ServerLevel level, DamageSource damageSource) {
        super.destroy(level, damageSource);
        this.popOutTheItem();
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.FURNACE_MINECART;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return itemStack.has(DataComponents.JUKEBOX_PLAYABLE);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    @Override
    public ItemStack getTheItem() {
        return this.musicDisc;
    }

    @Override
    public void setTheItem(ItemStack item) {
        this.musicDisc = item;
        var jukeboxPlayable = this.musicDisc.get(DataComponents.JUKEBOX_PLAYABLE);
        if (jukeboxPlayable == null) {
            this.musicHandler.stopPlaying();
            this.tickCount = -1;
            this.song = null;
            return;
        }
        var song = jukeboxPlayable.song();
        if (this.song != song) {
            this.musicHandler.stopPlaying();
            this.tickCount = -1;
        }
        this.song = song;

        if (song != null) {
            this.musicHandler.startPlaying(song.value());
            this.tickCount = 0;
        }
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    private class MusicHandler extends ElementHolder {
        private final ItemDisplayElement audioSource = new ItemDisplayElement();

        private MusicHandler() {
            this.audioSource.setInvisible(true);
            this.audioSource.setTeleportDuration(3);
            this.audioSource.setOffset(new Vec3(0, getBbHeight() / 2, 0));
        }

        public void startPlaying(JukeboxSong song) {
            this.stopPlaying();
            this.addElement(this.audioSource);
            this.sendPacket(VirtualEntityUtils.createClientboundSoundEntityPacket(this.audioSource.getEntityId(), song.soundEvent(), SoundSource.RECORDS, 4.0F, 1.0F, RandomSource.create().nextLong()));
            this.sendPacket(new ClientboundSystemChatPacket(song.description(), true));
        }

        public void stopPlaying() {
            this.removeElement(this.audioSource);
        }

        @Override
        public void tick() {
            super.tick();
        }
    }
}
