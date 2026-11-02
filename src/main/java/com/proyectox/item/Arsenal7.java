package com.proyectox.item;

import com.proyectox.entity.SpecBolt;
import com.proyectox.entity.SpecField;
import com.proyectox.fx.Fx;
import com.proyectox.registry.PXDamage;
import com.proyectox.registry.PXEffects;
import com.proyectox.registry.PXSounds;
import com.proyectox.util.Aim;
import com.proyectox.util.FX;
import com.proyectox.util.Hits;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Las 36 armas de la serie v7 (PX-66…PX-101): su ficha y lo que hace cada disparo o golpe.
 *
 * <ul>
 *   <li>NEÓN (PX-66…PX-77): tecnología urbana y componentes electrónicos.</li>
 *   <li>CHATARRA (PX-78…PX-89): armas improvisadas, oxidadas y modificadas.</li>
 *   <li>XENO (PX-90…PX-101): tecnología extraterrestre y formas desconocidas.</li>
 * </ul>
 */
public final class Arsenal7 {
    private Arsenal7() {}

    private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);

    // ================================================================ ayudas

    /** Lanza un proyectil desde la boca del cañón hacia el punto de mira. */
    private static SpecBolt bolt(GunItem.Shot shot, int style, double speed, double range, float damage) {
        return launch(shot, style, shot.aimDir(range), speed, damage);
    }

    private static SpecBolt launch(GunItem.Shot shot, int style, Vec3 dir, double speed, float damage) {
        SpecBolt b = SpecBolt.create(shot.level(), shot.player(), style, shot.muzzle(), dir.normalize().scale(speed), damage);
        shot.level().addFreshEntity(b);
        return b;
    }

    /** Lanza un proyectil con algo de elevación (granadas, botellas, bidones). */
    private static SpecBolt lob(GunItem.Shot shot, int style, double speed, double up, float damage) {
        Vec3 dir = shot.aimDir(48.0).add(0.0, up, 0.0);
        return launch(shot, style, dir, speed, damage);
    }

    /** Gira una dirección en horizontal (grados). */
    private static Vec3 yaw(Vec3 dir, double degrees) {
        double a = Math.toRadians(degrees);
        double c = Math.cos(a);
        double s = Math.sin(a);
        return new Vec3(dir.x * c - dir.z * s, dir.y, dir.x * s + dir.z * c);
    }

    private static DamageSource source(GunItem.Shot shot, ResourceKey<DamageType> type) {
        return PXDamage.source(shot.level(), type, shot.player(), shot.player());
    }

    /** Bala instantánea (o perdigón) desde los ojos: daña a la primera criatura y dibuja la trazadora. */
    private static void bullet(GunItem.Shot shot, int style, Vec3 dir, double range, float damage, ResourceKey<DamageType> type) {
        ServerLevel level = shot.level();
        ServerPlayer player = shot.player();
        Aim.Ray ray = Aim.pierce(level, player, shot.eye(), dir, range, 1);
        DamageSource ds = source(shot, type);
        for (Entity e : ray.hits()) Hits.hurt(e, ds, damage, player);
        float what = !ray.hits().isEmpty() ? 2.0F : ray.hitBlock() ? 1.0F : 0.0F;
        Fx.line(level, Fx.BULLET, style, shot.muzzle(), ray.end(), what, player.getId());
    }

    /** Haz instantáneo que atraviesa hasta {@code maxHits} criaturas (se detiene en los bloques). */
    private static Vec3 beam(GunItem.Shot shot, int style, double range, int maxHits, float damage, ResourceKey<DamageType> type, float width) {
        ServerLevel level = shot.level();
        ServerPlayer player = shot.player();
        Aim.Ray ray = Aim.pierce(level, player, shot.eye(), shot.eyeDir(), range, maxHits);
        DamageSource ds = source(shot, type);
        for (Entity e : ray.hits()) Hits.hurt(e, ds, damage, player);
        Fx.line(level, Fx.BEAM7, style, shot.muzzle(), ray.end(), width, player.getId());
        return ray.end();
    }

    private static boolean visible(ServerLevel level, ServerPlayer player, Vec3 from, Entity e) {
        return level.clip(new ClipContext(from, e.getBoundingBox().getCenter(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))
                .getType() == HitResult.Type.MISS;
    }

    // ================================================================ NEÓN (PX-66 … PX-77)

    /** PX-66 PÍXEL — pistola láser: pulso de 8 de daño. */
    public static SpecGun pixel() {
        return new SpecGun("pixel", "PX-66", 0xFF2BD6, Fx.PIXEL, PXWeapon.baseProperties(), SpecGun.spec().semi(4, 6).ammo(40, 3, 8)
                .spread(0.8, 0.1).zoom(0.25F).kick(0.35F).sound(PXSounds.PIXEL_DISPARO, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.PIXEL, 3.2, 128.0, 8.0F)));
    }

    /** PX-67 VOLTIO — subfusil de arco: 12 pulsos por segundo de 5 que saltan a otra criatura cercana (2). */
    public static SpecGun voltio() {
        return new SpecGun("voltio", "PX-67", 0x20F0FF, Fx.VOLTIO, PXWeapon.baseProperties(), SpecGun.spec().auto(12.0).ammo(120, 2, 10)
                .spread(2.0, 0.7).zoom(0.25F).kick(0.2F).sound(PXSounds.VOLTIO_DISPARO, 0.9F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.VOLTIO, 3.0, 90.0, 5.0F)));
    }

    /** PX-68 ESTÁTICA — escopeta de pulsos: 8 descargas de 3,5. */
    public static SpecGun estatica() {
        return new SpecGun("estatica", "PX-68", 0x9DFF2E, Fx.ESTATICA, PXWeapon.baseProperties(), SpecGun.spec().semi(14, 14).ammo(24, 10, 10)
                .spread(1.2, 0.5).pellets(7.0).zoom(0.2F).kick(0.85F).sound(PXSounds.ESTATICA_DISPARO, 1.6F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    Vec3 base = shot.eyeDir();
                    for (int i = 0; i < 8; i++) bullet(shot, Fx.ESTATICA, Aim.spread(base, shot.level().random, 7.0), 24.0, 3.5F, PXDamage.NEON);
                }));
    }

    /** PX-69 DIODO — láser de carga: haz que atraviesa a todas las criaturas, de 12 a 30 según la carga. */
    public static SpecGun diodo() {
        return new SpecGun("diodo", "PX-69", 0xC8FF30, Fx.DIODO, PXWeapon.baseProperties(), SpecGun.spec().charge(20, 10).ammo(16, 15, 10)
                .spread(0.4, 0.0).zoom(0.4F).kick(0.6F).chargeSound(PXSounds.DIODO_CARGA).sound(PXSounds.DIODO_DISPARO, 1.8F, 1.0F)
                .payload((g, shot, stack, p, h) -> beam(shot, Fx.DIODO, 96.0, 64, 12.0F + 18.0F * p, PXDamage.NEON, 0.5F + 0.8F * p)));
    }

    /** PX-70 FUSIBLE — lanzagranadas EMP: rebota y estalla a los 1,2 s (10 y parálisis 2 s en 4 bloques). */
    public static SpecGun fusible() {
        return new SpecGun("fusible", "PX-70", 0xFFB020, Fx.FUSIBLE, PXWeapon.baseProperties(), SpecGun.spec().semi(16, 16).ammo(8, 25, 10)
                .spread(1.0, 0.4).zoom(0.2F).kick(0.6F).sound(PXSounds.FUSIBLE_DISPARO, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> lob(shot, Fx.FUSIBLE, 1.25, 0.12, 10.0F)));
    }

    /** PX-71 ANTENA — fusil de precisión: rayo de 22 que atraviesa a 2 criaturas. */
    public static SpecGun antena() {
        return new SpecGun("antena", "PX-71", 0x9B5CFF, Fx.ANTENA, PXWeapon.baseProperties(), SpecGun.spec().semi(24, 24).ammo(12, 20, 12)
                .spread(0.3, 0.0).zoom(0.7F).scoped().kick(0.8F).sound(PXSounds.ANTENA_DISPARO, 3.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> beam(shot, Fx.ANTENA, 200.0, 3, 22.0F, PXDamage.NEON, 0.35F)));
    }

    /** PX-72 VINILO — lanzadiscos: disco de 9 que corta todo lo que toca y rebota 4 veces. */
    public static SpecGun vinilo() {
        return new SpecGun("vinilo", "PX-72", 0xFF4FD8, Fx.VINILO, PXWeapon.baseProperties(), SpecGun.spec().semi(8, 10).ammo(20, 8, 10)
                .spread(0.6, 0.2).zoom(0.2F).kick(0.3F).noFlash().sound(PXSounds.VINILO_DISPARO, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.VINILO, 1.6, 64.0, 9.0F)));
    }

    /** PX-73 BAJO — cañón de graves: onda de 40° y 10 bloques que atraviesa paredes (12 a cada criatura). */
    public static SpecGun bajo() {
        return new SpecGun("bajo", "PX-73", 0x40C8FF, Fx.BAJO, PXWeapon.baseProperties(), SpecGun.spec().semi(16, 16).ammo(12, 15, 10)
                .spread(0.0, 0.0).zoom(0.15F).kick(0.7F).noFlash().sound(PXSounds.BAJO_DISPARO, 2.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    ServerLevel level = shot.level();
                    ServerPlayer player = shot.player();
                    DamageSource ds = source(shot, PXDamage.SONICO);
                    for (LivingEntity e : Aim.cone(level, player, shot.eye(), shot.look(), 10.0, 20.0)) Hits.hurt(e, ds, 12.0F, player);
                    Fx.line(level, Fx.CONE7, Fx.BAJO, shot.muzzle(), shot.eye().add(shot.look().scale(10.0)), 20.0F, player.getId());
                }));
    }

    /** PX-74 NEXO — lanzadrones: tres minidrones que buscan enemigos y estallan (8 en 1,5 bloques). */
    public static SpecGun nexo() {
        return new SpecGun("nexo", "PX-74", 0x5CFF9A, Fx.NEXO, PXWeapon.baseProperties(), SpecGun.spec().semi(30, 30).cost(3).ammo(30, 6, 12)
                .spread(1.0, 0.5).zoom(0.2F).kick(0.4F).sound(PXSounds.NEXO_DISPARO, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    Vec3 dir = shot.aimDir(48.0);
                    for (int i = -1; i <= 1; i++) launch(shot, Fx.NEXO, yaw(dir, i * 14.0).add(0.0, 0.08, 0.0), 0.9, 8.0F);
                }));
    }

    /** PX-75 NEÓN — katana de neón: barrido de 120° y 3,6 bloques (10 de daño). */
    public static SpecMelee neon() {
        SpecMelee.Spec s = SpecMelee.spec().sweep(3.6, 120.0).damage(10.0F).interval(10).type(PXDamage.NEON).sound(PXSounds.NEON_TAJO, 1.0F);
        return new SpecMelee("neon", "PX-75", 0xFF2BD6, Fx.NEON, MeleeItem.meleeProperties(2200).attributes(SpecMelee.attributes(s)), s);
    }

    /** PX-76 RELÉ — bate eléctrico: golpe de 9 que paraliza 1 s. */
    public static SpecMelee rele() {
        SpecMelee.Spec s = SpecMelee.spec().smash(3.8, 0.0F, 0.0).damage(9.0F).interval(14).type(PXDamage.ELECTROCUCION)
                .sound(PXSounds.RELE_GOLPE, 1.0F).onHit((level, player, target) -> PXEffects.stun(target, 20, player));
        return new SpecMelee("rele", "PX-76", 0xFFB020, Fx.RELE, MeleeItem.meleeProperties(2200).attributes(SpecMelee.attributes(s)), s);
    }

    /** PX-77 FRECUENCIA — proyector de ondas: haz de 40 bloques que hace 3 diez veces por segundo y atraviesa a 2. */
    public static SpecGun frecuencia() {
        return new SpecGun("frecuencia", "PX-77", 0x9B5CFF, Fx.FRECUENCIA, PXWeapon.baseProperties(), SpecGun.spec().beam(2).ammo(100, 2, 10)
                .spread(0.0, 0.0).zoom(0.25F).kick(0.05F).sound(PXSounds.FRECUENCIA_ONDA, 0.9F, 1.0F).soundEvery(10)
                .payload((g, shot, stack, p, h) -> beam(shot, Fx.FRECUENCIA, 40.0, 3, 3.0F, PXDamage.NEON, 0.5F)));
    }

    // ================================================================ CHATARRA (PX-78 … PX-89)

    /** PX-78 TUBERÍA — pistola de tubo: bala de 14 (una cada 0,7 s). */
    public static SpecGun tuberia() {
        return new SpecGun("tuberia", "PX-78", 0xC8783C, Fx.TUBERIA, PXWeapon.baseProperties(), SpecGun.spec().semi(14, 14).ammo(10, 12, 10)
                .spread(1.2, 0.4).zoom(0.22F).kick(0.85F).sound(PXSounds.TUBERIA_DISPARO, 3.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> bullet(shot, Fx.TUBERIA, shot.eyeDir(), 120.0, 14.0F, PXDamage.BALA)));
    }

    /** PX-79 CHAPA — escopeta de chatarra: 9 perdigones de 3. */
    public static SpecGun chapa() {
        return new SpecGun("chapa", "PX-79", 0xC85A3C, Fx.CHAPA, PXWeapon.baseProperties(), SpecGun.spec().semi(18, 18).ammo(12, 14, 10)
                .spread(1.5, 0.7).pellets(8.0).zoom(0.2F).kick(1.0F).sound(PXSounds.CHAPA_DISPARO, 3.5F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    Vec3 base = shot.eyeDir();
                    for (int i = 0; i < 9; i++) bullet(shot, Fx.CHAPA, Aim.spread(base, shot.level().random, 8.0), 32.0, 3.0F, PXDamage.PERDIGON);
                }));
    }

    /** PX-80 CLAVO — clavadora automática: 10 clavos por segundo de 4. */
    public static SpecGun clavo() {
        return new SpecGun("clavo", "PX-80", 0xE8B830, Fx.CLAVO, PXWeapon.baseProperties(), SpecGun.spec().auto(10.0).ammo(150, 2, 10)
                .spread(1.8, 0.8).zoom(0.22F).kick(0.15F).noFlash().sound(PXSounds.CLAVO_DISPARO, 0.9F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.CLAVO, 3.0, 64.0, 4.0F)));
    }

    /** PX-81 SERRUCHO — lanzasierras: disco de 11 que corta todo lo que toca y rebota 3 veces. */
    public static SpecGun serrucho() {
        return new SpecGun("serrucho", "PX-81", 0x5C9AE0, Fx.SERRUCHO, PXWeapon.baseProperties(), SpecGun.spec().semi(12, 12).ammo(12, 12, 10)
                .spread(0.6, 0.2).zoom(0.2F).kick(0.4F).noFlash().sound(PXSounds.SERRUCHO_LANZAR, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.SERRUCHO, 1.5, 48.0, 11.0F)));
    }

    /** PX-82 PERNO — ballesta de chatarra: varilla de 10 a 28 según la carga que atraviesa a 2 criaturas. */
    public static SpecGun perno() {
        return new SpecGun("perno", "PX-82", 0xB87A4A, Fx.PERNO, PXWeapon.baseProperties(), SpecGun.spec().charge(24, 10).ammo(10, 18, 10)
                .spread(0.5, 0.0).zoom(0.35F).kick(0.6F).noFlash().chargeSound(PXSounds.PERNO_CARGA).sound(PXSounds.PERNO_DISPARO, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.PERNO, 2.2 + 1.6 * p, 96.0, 10.0F + 18.0F * p)));
    }

    /** PX-83 MOLOTOV — lanzacócteles: la botella estalla en un charco de fuego de 4 s. */
    public static SpecGun molotov() {
        return new SpecGun("molotov", "PX-83", 0xFF7A1A, Fx.MOLOTOV, PXWeapon.baseProperties(), SpecGun.spec().semi(20, 20).ammo(10, 20, 10)
                .spread(1.0, 0.4).zoom(0.2F).kick(0.4F).noFlash().sound(PXSounds.MOLOTOV_LANZAR, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> lob(shot, Fx.MOLOTOV, 1.1, 0.15, 6.0F)));
    }

    /** PX-84 BIDÓN — mortero de hombro: bidón de 24 en 4,5 bloques. */
    public static SpecGun bidon() {
        return new SpecGun("bidon", "PX-84", 0xE03A28, Fx.BIDON, PXWeapon.baseProperties(), SpecGun.spec().semi(30, 30).ammo(6, 40, 12)
                .spread(0.8, 0.3).zoom(0.2F).kick(0.9F).sound(PXSounds.BIDON_DISPARO, 2.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> lob(shot, Fx.BIDON, 1.6, 0.1, 24.0F)));
    }

    /** PX-85 CACHARRO — cañón de chatarra: un trasto de 12 que rebota 3 veces (también en las criaturas). */
    public static SpecGun cacharro() {
        return new SpecGun("cacharro", "PX-85", 0xD84A30, Fx.CACHARRO, PXWeapon.baseProperties(), SpecGun.spec().semi(12, 12).ammo(16, 12, 10)
                .spread(1.2, 0.5).zoom(0.2F).kick(0.7F).sound(PXSounds.CACHARRO_DISPARO, 1.4F, 1.0F)
                .payload((g, shot, stack, p, h) -> lob(shot, Fx.CACHARRO, 1.8, 0.05, 12.0F)));
    }

    /** PX-86 MANIVELA — ametralladora de manivela: 15 balas por segundo de 4. */
    public static SpecGun manivela() {
        return new SpecGun("manivela", "PX-86", 0xB8A060, Fx.MANIVELA, PXWeapon.baseProperties(), SpecGun.spec().auto(15.0).ammo(300, 1, 10)
                .spread(2.5, 1.2).zoom(0.2F).kick(0.15F).sound(PXSounds.MANIVELA_DISPARO, 2.5F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    bullet(shot, Fx.MANIVELA, shot.eyeDir(), 120.0, 4.0F, PXDamage.BALA);
                    if (h % 4 == 0) FX.sound(shot.level(), shot.player(), PXSounds.MANIVELA_MANIVELA.get(), 0.7F, 1.0F);
                }));
    }

    /** PX-87 GARFIO — lanzagarfios: 8 de daño y arrastra a la criatura hacia ti; en un bloque, te lleva hasta él. */
    public static SpecGun garfio() {
        return new SpecGun("garfio", "PX-87", 0xA8B0B8, Fx.GARFIO, PXWeapon.baseProperties(), SpecGun.spec().semi(20, 20).ammo(8, 20, 10)
                .spread(0.5, 0.1).zoom(0.25F).kick(0.5F).noFlash().sound(PXSounds.GARFIO_DISPARO, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.GARFIO, 2.4, 48.0, 8.0F)));
    }

    /** PX-88 PALANCA — mazo de chatarra: golpe de 14 y onda de 7 alrededor (2,5 bloques). */
    public static SpecMelee palanca() {
        SpecMelee.Spec s = SpecMelee.spec().smash(4.0, 7.0F, 2.5).damage(14.0F).interval(18).type(PXDamage.CHATARRA)
                .sound(PXSounds.PALANCA_GOLPE, 1.0F);
        return new SpecMelee("palanca", "PX-88", 0xC86A3A, Fx.PALANCA, MeleeItem.meleeProperties(2400).attributes(SpecMelee.attributes(s)), s);
    }

    /** PX-89 ÓXIDO — machete de chatarra: barrido de 100° y 3 bloques (9 de daño). */
    public static SpecMelee oxido() {
        SpecMelee.Spec s = SpecMelee.spec().sweep(3.0, 100.0).damage(9.0F).interval(9).type(PXDamage.CORTE).sound(PXSounds.OXIDO_TAJO, 1.0F);
        return new SpecMelee("oxido", "PX-89", 0xC0602A, Fx.OXIDO, MeleeItem.meleeProperties(2000).attributes(SpecMelee.attributes(s)), s);
    }

    // ================================================================ XENO (PX-90 … PX-101)

    /** PX-90 ESPORA — lanzaesporas: 4 esporas por segundo que buscan a su presa (7 y veneno 2 s). */
    public static SpecGun espora() {
        return new SpecGun("espora", "PX-90", 0xB4FF50, Fx.ESPORA, PXWeapon.baseProperties(), SpecGun.spec().auto(4.0).ammo(40, 4, 10)
                .spread(1.5, 0.6).zoom(0.2F).kick(0.2F).sound(PXSounds.ESPORA_DISPARO, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.ESPORA, 0.9, 48.0, 7.0F)));
    }

    /** PX-91 GLIFO — rifle de glifos: rayo de 11 que atraviesa a 3 criaturas. */
    public static SpecGun glifo() {
        return new SpecGun("glifo", "PX-91", 0xE650FF, Fx.GLIFO, PXWeapon.baseProperties(), SpecGun.spec().semi(8, 8).ammo(30, 5, 10)
                .spread(0.4, 0.0).zoom(0.35F).kick(0.4F).sound(PXSounds.GLIFO_DISPARO, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> beam(shot, Fx.GLIFO, 80.0, 4, 11.0F, PXDamage.XENO, 0.55F)));
    }

    /** PX-92 ABISMO — lanzapozos: abre un pozo de gravedad de 3 s que atrae y hace 2 cada 0,25 s. */
    public static SpecGun abismo() {
        return new SpecGun("abismo", "PX-92", 0xAA5AFF, Fx.ABISMO, PXWeapon.baseProperties(), SpecGun.spec().semi(40, 40).ammo(4, 60, 12)
                .spread(0.5, 0.2).zoom(0.2F).kick(0.6F).sound(PXSounds.ABISMO_DISPARO, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.ABISMO, 1.2, 36.0, 4.0F)));
    }

    /** PX-93 LARVA — lanzalarvas: cuatro larvas que saltan hacia sus presas (5 cada una). */
    public static SpecGun larva() {
        return new SpecGun("larva", "PX-93", 0xBEFF46, Fx.LARVA, PXWeapon.baseProperties(), SpecGun.spec().semi(20, 20).cost(4).ammo(24, 6, 10)
                .spread(1.0, 0.5).zoom(0.2F).kick(0.35F).sound(PXSounds.LARVA_DISPARO, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    Vec3 dir = shot.aimDir(32.0);
                    for (int i = 0; i < 4; i++) launch(shot, Fx.LARVA, yaw(dir, -12.0 + 8.0 * i).add(0.0, 0.25, 0.0), 0.75, 5.0F);
                }));
    }

    /** PX-94 ECLIPSE — cañón de haz: haz continuo de 48 bloques que atraviesa a todas las criaturas (4 diez veces por segundo). */
    public static SpecGun eclipse() {
        return new SpecGun("eclipse", "PX-94", 0xFFB046, Fx.ECLIPSE, PXWeapon.baseProperties(), SpecGun.spec().beam(2).ammo(200, 2, 12)
                .spread(0.0, 0.0).zoom(0.25F).kick(0.05F).sound(PXSounds.ECLIPSE_RAYO, 1.2F, 1.0F).soundEvery(10)
                .payload((g, shot, stack, p, h) -> beam(shot, Fx.ECLIPSE, 48.0, 128, 4.0F, PXDamage.XENO, 1.0F)));
    }

    /** PX-95 NÉBULA — lanzanebulosas: orbe lento que daña y frena alrededor (3) y deja una nube de 4 s. */
    public static SpecGun nebula() {
        return new SpecGun("nebula", "PX-95", 0xFF78DC, Fx.NEBULA, PXWeapon.baseProperties(), SpecGun.spec().semi(30, 30).ammo(6, 40, 12)
                .spread(0.6, 0.2).zoom(0.2F).kick(0.4F).sound(PXSounds.NEBULA_DISPARO, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> bolt(shot, Fx.NEBULA, 0.45, 32.0, 3.0F)));
    }

    /** PX-96 SIMBIONTE — espada viva: barrido de 140° y 3,8 bloques (11) que te cura 1 por cada criatura alcanzada. */
    public static SpecMelee simbionte() {
        SpecMelee.Spec s = SpecMelee.spec().sweep(3.8, 140.0).damage(11.0F).interval(12).type(PXDamage.XENO).sound(PXSounds.SIMBIONTE_TAJO, 1.0F)
                .onHit((level, player, target) -> {
                    if (player.getHealth() < player.getMaxHealth()) {
                        player.heal(1.0F);
                        FX.sound(level, player, PXSounds.SIMBIONTE_CURAR.get(), 0.6F, 1.2F + level.random.nextFloat() * 0.3F);
                    }
                    Fx.point(level, Fx.PULSE7, Fx.SIMBIONTE, target.getBoundingBox().getCenter(), UP, 0.6F, player.getId());
                });
        return new SpecMelee("simbionte", "PX-96", 0x3CFFA0, Fx.SIMBIONTE, MeleeItem.meleeProperties(2400).attributes(SpecMelee.attributes(s)), s);
    }

    /** PX-97 ÉTER — emisor de levitación: cono de 30° y 12 bloques (6 y levitación 1,5 s). */
    public static SpecGun eter() {
        return new SpecGun("eter", "PX-97", 0x96DCFF, Fx.ETER, PXWeapon.baseProperties(), SpecGun.spec().semi(16, 16).ammo(12, 15, 10)
                .spread(0.0, 0.0).zoom(0.2F).kick(0.5F).noFlash().sound(PXSounds.ETER_DISPARO, 1.4F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    ServerLevel level = shot.level();
                    ServerPlayer player = shot.player();
                    DamageSource ds = source(shot, PXDamage.XENO);
                    for (LivingEntity e : Aim.cone(level, player, shot.eye(), shot.look(), 12.0, 15.0)) {
                        if (!visible(level, player, shot.eye(), e)) continue;
                        Hits.hurt(e, ds, 6.0F, player);
                        if (e.isAlive()) e.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 30, 1, false, true), player);
                    }
                    Fx.line(level, Fx.CONE7, Fx.ETER, shot.muzzle(), shot.eye().add(shot.look().scale(12.0)), 15.0F, player.getId());
                }));
    }

    /** PX-98 CÉNIT — arco celeste: carga y suelta tres flechas de luz que buscan a sus presas (6 a 16 cada una). */
    public static SpecGun cenit() {
        return new SpecGun("cenit", "PX-98", 0xFFECA0, Fx.CENIT, PXWeapon.baseProperties(), SpecGun.spec().charge(20, 12).ammo(12, 16, 10)
                .spread(0.6, 0.0).zoom(0.3F).kick(0.5F).noFlash().chargeSound(PXSounds.CENIT_CARGA).sound(PXSounds.CENIT_DISPARO, 1.0F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    Vec3 dir = shot.aimDir(64.0);
                    for (int i = -1; i <= 1; i++) launch(shot, Fx.CENIT, yaw(dir, i * 6.0), 2.0 + 0.6 * p, 6.0F + 10.0F * p);
                }));
    }

    /** PX-99 UMBRAL — abridor de grietas: abre una grieta de 3 s donde apuntas que atrae y hace 3 cada 0,25 s. */
    public static SpecGun umbral() {
        return new SpecGun("umbral", "PX-99", 0xA05AFF, Fx.UMBRAL, PXWeapon.baseProperties(), SpecGun.spec().semi(50, 50).ammo(3, 80, 12)
                .spread(0.0, 0.0).zoom(0.2F).kick(0.6F).noFlash().sound(PXSounds.UMBRAL_ABRIR, 1.5F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    ServerLevel level = shot.level();
                    ServerPlayer player = shot.player();
                    Vec3 eye = shot.eye();
                    Vec3 look = shot.look();
                    Aim.Ray ray = Aim.pierce(level, player, eye, look, 32.0, 1);
                    Vec3 at;
                    if (!ray.hits().isEmpty()) {
                        at = ray.hits().get(0).getBoundingBox().getCenter();
                    } else if (ray.hitBlock()) {
                        at = ray.end().add(ray.normal().scale(0.8));
                    } else {
                        at = ray.end();
                    }
                    level.addFreshEntity(SpecField.create(level, player, SpecField.RIFT, at, look));
                    Fx.line(level, Fx.BEAM7, Fx.UMBRAL, shot.muzzle(), at, 0.3F, player.getId());
                    Fx.point(level, Fx.PULSE7, Fx.UMBRAL, at, look.reverse(), (float) SpecField.RIFT_RADIUS, -1);
                }));
    }

    /** PX-100 OBSERVADOR — invocador del ojo: un ojo flota 15 s y dispara rayos de 5 a los enemigos (2 ojos como mucho). */
    public static SpecGun observador() {
        return new SpecGun("observador", "PX-100", 0xFF5A46, Fx.OBSERVADOR, PXWeapon.baseProperties(), SpecGun.spec().semi(40, 40)
                .ammo(4, 100, 12).spread(0.0, 0.0).zoom(0.2F).kick(0.3F).noFlash().sound(PXSounds.OBSERVADOR_INVOCAR, 1.2F, 1.0F)
                .payload((g, shot, stack, p, h) -> {
                    ServerLevel level = shot.level();
                    ServerPlayer player = shot.player();
                    Vec3 eye = shot.eye();
                    Vec3 want = eye.add(shot.look().scale(2.5)).add(0.0, 0.8, 0.0);
                    BlockHitResult wall = level.clip(new ClipContext(eye, want, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    Vec3 at = wall.getType() == HitResult.Type.MISS ? want
                            : wall.getLocation().subtract(want.subtract(eye).normalize().scale(0.6));
                    SpecField.limitEyes(level, player);
                    level.addFreshEntity(SpecField.create(level, player, SpecField.EYE, at, shot.look()));
                    Fx.point(level, Fx.PULSE7, Fx.OBSERVADOR, at, UP, 1.2F, -1);
                }));
    }

    /** PX-101 AGUIJÓN — lanza aguijón: estocada de 6 bloques que atraviesa a todas (12 y veneno 2 s). */
    public static SpecMelee aguijon() {
        SpecMelee.Spec s = SpecMelee.spec().thrust(6.0).damage(12.0F).interval(12).type(PXDamage.VENENO).sound(PXSounds.AGUIJON_ESTOCADA, 1.0F)
                .onHit((level, player, target) -> {
                    if (target.isAlive()) target.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1, false, true), player);
                });
        return new SpecMelee("aguijon", "PX-101", 0xAAFF3C, Fx.AGUIJON, MeleeItem.meleeProperties(2400).attributes(SpecMelee.attributes(s)), s);
    }

    /** Todas, en orden (para el visor y las pruebas). */
    public static List<String> keys() {
        return List.of("pixel", "voltio", "estatica", "diodo", "fusible", "antena", "vinilo", "bajo", "nexo", "neon", "rele", "frecuencia",
                "tuberia", "chapa", "clavo", "serrucho", "perno", "molotov", "bidon", "cacharro", "manivela", "garfio", "palanca", "oxido",
                "espora", "glifo", "abismo", "larva", "eclipse", "nebula", "simbionte", "eter", "cenit", "umbral", "observador", "aguijon");
    }
}
