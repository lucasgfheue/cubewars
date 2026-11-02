package com.proyectox.entity;

import com.proyectox.client.ClientOnly;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEntities;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.Blast;
import com.proyectox.util.FX;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Proyectiles que se quedan clavados: el arpón del ARPÓN (18 de daño al clavarse y, 1 s después, estalla:
 * 10 en radio 3,5) y la mina del MINADOR (se pega a la superficie, se arma en 0,6 s y estalla cuando una
 * criatura se acerca a menos de 2,6 bloques: 30 en radio 4; como mucho 8 minas por jugador).
 */
public class Sticky extends PXProjectile {
    public static final int HARPOON = 0;
    public static final int MINE = 1;

    public static final float HARPOON_DAMAGE = 18.0F;
    public static final float HARPOON_BLAST = 10.0F;
    public static final double HARPOON_RADIUS = 3.5;
    public static final int HARPOON_FUSE = 20;

    public static final float MINE_DAMAGE = 30.0F;
    public static final double MINE_RADIUS = 4.0;
    public static final double MINE_TRIGGER = 2.6;
    public static final int MINE_ARM = 12;
    public static final int MINE_LIFE = 1200;
    public static final int MAX_MINES = 8;

    private static final EntityDataAccessor<Integer> KIND = SynchedEntityData.defineId(Sticky.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> STUCK = SynchedEntityData.defineId(Sticky.class, EntityDataSerializers.BOOLEAN);
    /** Cara del bloque a la que se pegó la mina (valor 3D de {@link Direction}), o -1. */
    private static final EntityDataAccessor<Integer> FACE = SynchedEntityData.defineId(Sticky.class, EntityDataSerializers.INT);

    /** Minas activas de cada jugador, de la más vieja a la más nueva. */
    private static final Map<UUID, Deque<Integer>> MINES = new ConcurrentHashMap<>();

    private int stuckTicks;
    /** Orientación en el momento de clavarse (el arpón no debe girar después). */
    private float stuckYaw;
    private float stuckPitch;
    private int hostId = -1;
    private Vec3 hostOffset = Vec3.ZERO;
    @Nullable
    private BlockPos anchor;

    public Sticky(EntityType<? extends Sticky> type, Level level) {
        super(type, level);
    }

    public static Sticky create(ServerLevel level, @Nullable Entity owner, int kind, Vec3 pos, Vec3 velocity, int life) {
        Sticky s = new Sticky(PXEntities.ANCLADO.get(), level);
        s.entityData.set(KIND, kind);
        s.setNoGravity(kind == HARPOON);
        s.launch(owner, pos, velocity);
        s.damage = kind == HARPOON ? HARPOON_DAMAGE : MINE_DAMAGE;
        s.maxLife = life;
        return s;
    }

    public static void forget(Player player) {
        MINES.remove(player.getUUID());
    }

    public int kind() {
        return this.entityData.get(KIND);
    }

    public boolean isStuck() {
        return this.entityData.get(STUCK);
    }

    /** Normal de la superficie a la que está pegada la mina (o null). */
    @Nullable
    public Vec3 face() {
        int f = this.entityData.get(FACE);
        return f < 0 ? null : Vec3.atLowerCornerOf(Direction.from3DDataValue(f).getNormal());
    }

    /** Ticks desde que se clavó (también en el cliente, para el parpadeo). */
    public int stuckTicks() {
        return this.stuckTicks;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(KIND, HARPOON);
        builder.define(STUCK, false);
        builder.define(FACE, -1);
    }

    @Override
    protected double getDefaultGravity() {
        return kind() == MINE ? 0.05 : 0.0;
    }

    @Override
    public void tick() {
        if (isStuck()) {
            this.baseTick();
            this.setDeltaMovement(Vec3.ZERO);
            this.stuckTicks++;
            if (this.level() instanceof ServerLevel level) {
                stuckTick(level);
            } else {
                ClientOnly.projectileTick(this);
            }
            return;
        }
        super.tick();
        if (isStuck()) {
            // el tick de vuelo intenta girarlo hacia su velocidad, que ya es nula: se conserva la orientación del impacto
            this.setYRot(this.stuckYaw);
            this.setXRot(this.stuckPitch);
            this.yRotO = this.stuckYaw;
            this.xRotO = this.stuckPitch;
        }
    }

    private void stuckTick(ServerLevel level) {
        if (kind() == HARPOON) {
            Entity host = this.hostId >= 0 ? level.getEntity(this.hostId) : null;
            if (host != null && host.isAlive()) {
                Vec3 p = host.position().add(this.hostOffset);
                this.setPos(p.x, p.y, p.z);
            }
            if (this.stuckTicks % 5 == 0 && this.stuckTicks < HARPOON_FUSE) {
                FX.sound(level, this.position(), PXSounds.ARPON_PITIDO.get(), 0.7F, 1.4F + this.stuckTicks / (float) HARPOON_FUSE * 0.6F);
            }
            if (this.stuckTicks >= HARPOON_FUSE) harpoonBlast(level, host != null && host.isAlive() ? host : null);
            return;
        }
        if (this.anchor != null && level.getBlockState(this.anchor).isAir()) {
            mineBlast(level, null);
            return;
        }
        if (this.stuckTicks == MINE_ARM) FX.sound(level, this.position(), PXSounds.MINADOR_ARMAR.get(), 0.7F, 1.8F);
        if (this.stuckTicks >= MINE_ARM && this.stuckTicks % 2 == 0) {
            Entity owner = this.getOwner();
            if (!Aim.sphere(level, owner != null ? owner : this, this.position(), MINE_TRIGGER).isEmpty()) {
                mineBlast(level, null);
                return;
            }
        }
        if (this.stuckTicks >= MINE_LIFE) {
            Fx.impact(level, Fx.MINADOR, this.position(), face() != null ? face() : new Vec3(0, 1, 0));
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!(this.level() instanceof ServerLevel level) || isStuck() || !this.isAlive()) return;
        Entity target = result.getEntity();
        if (kind() == MINE) {
            // contra una criatura la mina estalla en el acto
            mineBlast(level, target);
            return;
        }
        Vec3 at = contact(target);
        strike(level, target, PXDamage.ARPON, HARPOON_DAMAGE);
        stick(at);
        this.hostId = target.getId();
        this.hostOffset = at.subtract(target.position());
        Fx.impact(level, Fx.ARPON, at, this.heading().reverse());
        FX.sound(level, at, PXSounds.ARPON_CLAVAR.get(), 1.1F, 0.9F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!(this.level() instanceof ServerLevel level) || isStuck() || !this.isAlive()) return;
        Vec3 n = normal(result);
        this.anchor = result.getBlockPos();
        if (kind() == HARPOON) {
            stick(result.getLocation().subtract(this.heading().scale(0.1)));
            Fx.impact(level, Fx.ARPON, result.getLocation(), n);
            FX.sound(level, result.getLocation(), PXSounds.ARPON_CLAVAR.get(), 1.1F, 1.1F);
        } else {
            stick(result.getLocation().add(n.scale(0.06)));
            this.entityData.set(FACE, result.getDirection().get3DDataValue());
            FX.sound(level, result.getLocation(), PXSounds.MINADOR_ADHERIR.get(), 1.0F, 1.2F);
            register(level);
        }
    }

    private void stick(Vec3 at) {
        Vec3 v = this.getDeltaMovement();
        if (v.lengthSqr() > 1.0E-8) face(this, v);
        this.stuckYaw = this.getYRot();
        this.stuckPitch = this.getXRot();
        this.setPos(at.x, at.y, at.z);
        this.setDeltaMovement(Vec3.ZERO);
        this.noPhysics = true;
        this.stuckTicks = 0;
        this.entityData.set(STUCK, true);
    }

    @Override
    protected void expire() {
        if (this.level() instanceof ServerLevel level && kind() == HARPOON) {
            harpoonBlast(level, null);
        } else {
            this.discard();
        }
    }

    /** Lleva la cuenta de las minas del jugador: con más de 8, la más vieja estalla. */
    private void register(ServerLevel level) {
        Entity owner = this.getOwner();
        if (owner == null) return;
        Deque<Integer> mines = MINES.computeIfAbsent(owner.getUUID(), k -> new ArrayDeque<>());
        synchronized (mines) {
            mines.removeIf(id -> !(level.getEntity(id) instanceof Sticky s && s.isAlive()));
            mines.addLast(this.getId());
            while (mines.size() > MAX_MINES) {
                Integer old = mines.pollFirst();
                if (old != null && level.getEntity(old) instanceof Sticky s && s.isAlive() && s.kind() == MINE) s.mineBlast(level, null);
            }
        }
    }

    private void harpoonBlast(ServerLevel level, @Nullable Entity host) {
        Vec3 c = this.position();
        Blast.damage(level, this, this.getOwner(), c, HARPOON_RADIUS, HARPOON_BLAST, host, PXDamage.ARPON, 0.6F);
        Fx.explosion(level, Fx.ARPON, c, (float) HARPOON_RADIUS);
        FX.sound(level, c, PXSounds.ARPON_EXPLOSION.get(), 1.8F, 1.0F + this.random.nextFloat() * 0.15F);
        this.discard();
    }

    private void mineBlast(ServerLevel level, @Nullable Entity direct) {
        if (!this.isAlive()) return;
        Vec3 c = this.position();
        Blast.damage(level, this, this.getOwner(), c, MINE_RADIUS, MINE_DAMAGE, direct, PXDamage.MINA, 0.5F);
        Fx.explosion(level, Fx.MINADOR, c, (float) MINE_RADIUS);
        FX.sound(level, c, PXSounds.MINADOR_EXPLOSION.get(), 2.0F, 0.95F + this.random.nextFloat() * 0.1F);
        this.discard();
    }
}
