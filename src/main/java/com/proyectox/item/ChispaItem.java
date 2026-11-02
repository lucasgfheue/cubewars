package com.proyectox.item;

import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * PX-09 CHISPA — pistola de arco eléctrico. Mientras se mantiene el gatillo lanza rayos continuos
 * (40 bloques) que electrocutan al enemigo de la mira y saltan a los que estén cerca, como la
 * electricidad: 7 de daño a cada uno, cinco veces por segundo.
 */
public class ChispaItem extends GunItem {
    public static final double RANGE = 40.0;
    public static final double JUMP = 7.0;
    public static final int JUMPS = 3;
    public static final float DAMAGE = 7.0F;

    public ChispaItem(Properties properties) {
        super("chispa", "PX-09", 0xFFF59A, Fx.CHISPA, properties);
    }

    @Override
    public int maxCharge() {
        return 300;
    }

    @Override
    public int regenTicks() {
        return 1;
    }

    @Override
    public int regenDelay() {
        return 10;
    }

    @Override
    public int interval() {
        return 2;
    }

    @Override
    public double hipSpread() {
        return 0.5;
    }

    @Override
    public double aimSpread() {
        return 0.0;
    }

    @Override
    public float zoom() {
        return 0.2F;
    }

    @Override
    public float kick() {
        return 0.12F;
    }

    @Override
    protected boolean muzzleFlash() {
        return false;
    }

    @Override
    protected void fire(Shot shot, ItemStack stack, int heldTicks) {
        ServerLevel level = shot.level();
        // un rayo cada 2 ticks; daño en uno de cada dos (5 veces por segundo), sea cual sea la paridad
        boolean damageTick = (heldTicks / 2) % 2 == 0;
        Aim.Ray ray = Aim.pierce(level, shot.player(), shot.eye(), shot.eyeDir(), RANGE, 1);
        int owner = shot.player().getId();
        if (ray.hits().isEmpty()) {
            Fx.line(level, Fx.ARC, Fx.CHISPA, shot.muzzle(), ray.end(), 1.0F, owner);
            if (ray.hitBlock() && damageTick) Fx.impact(level, Fx.CHISPA, ray.end(), ray.normal());
        } else {
            DamageSource source = PXDamage.source(level, PXDamage.ARCO, shot.player(), shot.player());
            Entity current = ray.hits().get(0);
            Fx.line(level, Fx.ARC, Fx.CHISPA, shot.muzzle(), current.getBoundingBox().getCenter(), 1.0F, owner);
            if (damageTick) {
                Hits.hurt(current, source, DAMAGE, shot.player());
                Fx.impact(level, Fx.CHISPA, current.getBoundingBox().getCenter(), shot.look().reverse());
            }
            // la descarga salta de un enemigo al siguiente más cercano
            Set<Entity> done = new HashSet<>();
            done.add(current);
            for (int i = 0; i < JUMPS; i++) {
                LivingEntity next = nearest(level, shot, current, done);
                if (next == null) break;
                Fx.line(level, Fx.ARC, Fx.CHISPA, current.getBoundingBox().getCenter(), next.getBoundingBox().getCenter(), 0.7F, -1);
                if (damageTick) Hits.hurt(next, source, DAMAGE, shot.player());
                done.add(next);
                current = next;
            }
        }
        if ((heldTicks / 2) % 3 == 0) FX.sound(level, shot.player(), PXSounds.CHISPA_DISPARO.get(), 0.8F, 0.8F + level.random.nextFloat() * 0.4F);
    }

    @Nullable
    private static LivingEntity nearest(ServerLevel level, Shot shot, Entity from, Set<Entity> done) {
        Vec3 c = from.getBoundingBox().getCenter();
        LivingEntity best = null;
        double bestD = JUMP * JUMP;
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, new AABB(c, c).inflate(JUMP), x -> Aim.targetable(shot.player(), x) && !done.contains(x))) {
            Vec3 ec = e.getBoundingBox().getCenter();
            double d = ec.distanceToSqr(c);
            if (d >= bestD) continue;
            if (level.clip(new ClipContext(c, ec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, from)).getType() != HitResult.Type.MISS) continue;
            bestD = d;
            best = e;
        }
        return best;
    }
}
