package me.machinemaker.vanillatweaks.mobheads;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.machinemaker.vanillatweaks.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Cat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Fox.Type;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zoglin;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

class Head<E extends LivingEntity> {
    private static final ItemStack skullBase = new ItemStack(Material.PLAYER_HEAD);

    public static Map<EntityType, Head<? extends LivingEntity>> headMap = Maps.newHashMap();
    public static Map<EntityType, List<Head<? extends LivingEntity>>> multiHeadMap = Maps.newHashMap();

    public static Head<Bat> BAT = new Head<>(EntityType.BAT, "Bat", "27653cdf-9109-481b-8f9f-468895a892a2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGEyOWM5ZWNlNDI4ZmEyMzM5NWFjMjAxOWJmMmQwMjc0NDA1MjlmMjUzM2ZjODIwMWU3YjNkYTBmNjBmMjAwNSJ9fX0=", 0.02, 0.02);
    public static Head<Bee> BEE_PLAIN = new Head<>(EntityType.BEE, "Bee", "77342662-8870-445a-869f-f0aef1406b3d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlhYzE2ZjI5NmI0NjFkMDVlYTA3ODVkNDc3MDMzZTUyNzM1OGI0ZjMwYzI2NmFhMDJmMDIwMTU3ZmZjYTczNiJ9fX0=", bee -> !bee.hasNectar() && bee.getAnger() == 0);
    public static Head<Bee> BEE_NECTAR = new Head<>(EntityType.BEE, "Pollinated Bee","7766f1ce-53ca-4557-80e3-0539d2f7d909", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjcyN2QwYWIwM2Y1Y2QwMjJmODcwNWQzZjdmMTMzY2E0OTIwZWFlOGUxZTQ3YjUwNzQ0MzNhMTM3ZTY5MWU0ZSJ9fX0=", bee -> bee.hasNectar() && bee.getAnger() == 0);
    public static Head<Bee> BEE_ANGRY = new Head<>(EntityType.BEE, "Angry Bee", "14feb823-becc-4799-a97d-e529110e11a0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQwMDIyM2YxZmE1NDc0MWQ0MjFkN2U4MDQ2NDA5ZDVmM2UxNWM3ZjQzNjRiMWI3Mzk5NDAyMDhmM2I2ODZkNCJ9fX0=", bee -> !bee.hasNectar() && bee.getAnger() != 0);
    public static Head<Bee> BEE_NECTAR_ANGRY = new Head<>(EntityType.BEE, "Angry Pollinated Bee", "a148a6aa-24ea-49b6-b2be-4e1d1d130757", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZiNzRlMDUyYjc0Mjg4Nzk5YmE2ZDlmMzVjNWQwMjIxY2Y4YjA0MzMxNTQ3ZWMyZjY4ZDczNTk3YWUyYzliIn19fQ==", bee -> bee.hasNectar() && bee.getAnger() != 0);
    public static Head<Blaze> BLAZE = new Head<>(EntityType.BLAZE, "Blaze", "093b9a11-152d-4a5f-9418-50bea849f7c2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGVlMjNkYzdhMTBjNmE4N2VmOTM3NDU0YzBlOTRlZDQyYzIzYWE2NDFhOTFlZDg0NzBhMzA0MmQwNWM1MmM1MiJ9fX0=", 0.001, 0.0005);
    public static Head<Cat> TABBY_CAT = new Head<>(EntityType.CAT, "Tabby Cat", "18d071ee-a17c-46eb-866c-304a4823ac05", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGUyOGQzMGRiM2Y4YzNmZTUwY2E0ZjI2ZjMwNzVlMzZmMDAzYWU4MDI4MTM1YThjZDY5MmYyNGM5YTk4YWUxYiJ9fX0=", cat -> cat.getCatType() == Cat.Type.TABBY, 0.14, 0.02);
    public static Head<Cat> TUXEDO_CAT = new Head<>(EntityType.CAT, "Tuxedo Cat", "f0db2cac-dde4-47de-9c27-c0015e49d8b5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGZkMTBjOGU3NWY2NzM5OGM0NzU4N2QyNWZjMTQ2ZjMxMWMwNTNjYzVkMGFlYWI4NzkwYmNlMzZlZTg4ZjVmOCJ9fX0=", cat -> cat.getCatType() == Cat.Type.BLACK, 0.14, 0.02);
    public static Head<Cat> GINGER_CAT = new Head<>(EntityType.CAT, "Ginger Cat", "11d2442b-0bc1-4475-a499-f07dcc2aa40d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjExM2RiZDNjNmEwNzhhMTdiNGVkYjc4Y2UwN2Q4MzZjMzhkYWNlNTAyN2Q0YjBhODNmZDYwZTdjYTdhMGZjYiJ9fX0=", cat -> cat.getCatType() == Cat.Type.RED, 0.14, 0.02);
    public static Head<Cat> SIAMESE_CAT = new Head<>(EntityType.CAT, "Siamese Cat", "7d487214-5276-49af-bbb1-019b49384d69", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDViM2Y4Y2E0YjNhNTU1Y2NiM2QxOTQ0NDk4MDhiNGM5ZDc4MzMyNzE5NzgwMGQ0ZDY1OTc0Y2M2ODVhZjJlYSJ9fX0=", cat -> cat.getCatType() == Cat.Type.SIAMESE, 0.14, 0.02);
    public static Head<Cat> BRITISH_SHORTHAIR_CAT = new Head<>(EntityType.CAT, "British Shorthair Cat", "4332ff48-8a0e-4164-ae55-2d16caf68190", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTM4OWUwZDVkM2U4MWY4NGI1NzBlMjk3ODI0NGIzYTczZTVhMjJiY2RiNjg3NGI0NGVmNWQwZjY2Y2EyNGVlYyJ9fX0=", cat -> cat.getCatType() == Cat.Type.BRITISH_SHORTHAIR, 0.14, 0.02);
    public static Head<Cat> CALICO_CAT = new Head<>(EntityType.CAT, "Calico Cat", "024560fb-84a5-40cf-b6a1-c8f9d9db2fe9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQwMDk3MjcxYmI2ODBmZTk4MWU4NTllOGJhOTNmZWEyOGI4MTNiMTA0MmJkMjc3ZWEzMzI5YmVjNDkzZWVmMyJ9fX0=", cat -> cat.getCatType() == Cat.Type.CALICO, 0.14, 0.02);
    public static Head<Cat> PERSIAN_CAT = new Head<>(EntityType.CAT, "Persian Cat", "701fa2a8-ef2b-46cd-b9d3-6cd16be17bb4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY0MGM3NDYyNjBlZjkxYzk2YjI3MTU5Nzk1ZTg3MTkxYWU3Y2UzZDVmNzY3YmY4Yzc0ZmFhZDk2ODlhZjI1ZCJ9fX0=", cat -> cat.getCatType() == Cat.Type.PERSIAN, 0.14, 0.02);
    public static Head<Cat> RAGDOLL_CAT = new Head<>(EntityType.CAT, "Ragdoll Cat", "b65e722b-5a35-4561-a8df-db9c7a52041f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM3YTQ1ZDI1ODg5ZTNmZGY3Nzk3Y2IyNThlMjZkNGU5NGY1YmMxM2VlZjAwNzk1ZGFmZWYyZTgzZTBhYjUxMSJ9fX0=", cat -> cat.getCatType() == Cat.Type.RAGDOLL, 0.14, 0.02);
    public static Head<Cat> WHITE_CAT = new Head<>(EntityType.CAT, "White Cat", "db9474c0-f11e-47d3-a6dc-2ebcdd5f37e0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFkMTVhYzk1NThlOThiODlhY2E4OWQzODE5NTAzZjFjNTI1NmMyMTk3ZGQzYzM0ZGY1YWFjNGQ3MmU3ZmJlZCJ9fX0=", cat -> cat.getCatType() == Cat.Type.WHITE, 0.14, 0.02);
    public static Head<Cat> JELLIE_CAT = new Head<>(EntityType.CAT, "Jellie Cat", "f0aaa05b-0283-4663-9b57-52dbf2ca2750", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTBkYjQxMzc2Y2E1N2RmMTBmY2IxNTM5ZTg2NjU0ZWVjZmQzNmQzZmU3NWU4MTc2ODg1ZTkzMTg1ZGYyODBhNSJ9fX0=", cat -> cat.getCatType() == Cat.Type.JELLIE, 0.14, 0.02);
    public static Head<Cat> BLACK_CAT = new Head<>(EntityType.CAT, "Black Cat", "f89934e4-99a0-4dab-9151-7b63831e5fd1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJjMWU4MWZmMDNlODJhM2U3MWUwY2Q1ZmJlYzYwN2UxMTM2MTA4OWFhNDdmMjkwZDQ2YzhhMmMwNzQ2MGQ5MiJ9fX0=", cat -> cat.getCatType() == Cat.Type.ALL_BLACK, 0.14, 0.02);
    public static Head<CaveSpider> CAVE_SPIDER = new Head<>(EntityType.CAVE_SPIDER, "Cave Spider", "1d6c2bf4-35ae-4869-a9d4-fa884f886022", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY4NDNiYjdhOTg0ZTczNGFiNzZhODhjOWExYTBmNWE0MGJmNzk1MjQ4MDlhODUxMWJmMzJkMDU3NTI2ZjdmMyJ9fX0=", 0.01, 0.01);
    public static Head<Chicken> CHICKEN = new Head<>(EntityType.CHICKEN, "Chicken", "c8430ed4-cb5e-4c52-94fe-55fd07f692e8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhZjZlNTg0N2VlYTA5OWUxYjBhYjhjMjBhOWU1ZjNjNzE5MDE1OGJkYTU0ZTI4MTMzZDliMjcxZWMwY2I0YiJ9fX0=", 0.005, 0.001);
    public static Head<Cod> COD = new Head<>(EntityType.COD, "Cod", "7a77df37-8a6d-4dfc-8631-7a902e0d7791", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI0NmUxOWIzMmNmNzg0NTQ5NDQ3ZTA3Yjk2MDcyZTFmNjU2ZDc4ZTkzY2NjYTU2Mzc0ODVlNjc0OTczNDY1MiJ9fX0=", 0.1, 0.01);
    public static Head<Cow> COW = new Head<>(EntityType.COW, "Cow", "3603f051-3b2f-4428-bcdb-88fd633041bc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkNjIxMTAwZmVhNTg4MzkyMmU3OGJiNDQ4MDU2NDQ4Yzk4M2UzZjk3ODQxOTQ4YTJkYTc0N2Q2YjA4YjhhYiJ9fX0=", 0.005, 0.001);
    public static Head<Creeper> CHARGED_CREEPER = new Head<>(EntityType.CREEPER, "Creeper", "3b9bdc01-c62c-42a9-af61-a25c9009c738", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzUxMWU0YTNkNWFkZDZhNTQ0OTlhYmFkMTBkNzk5ZDA2Y2U0NWNiYTllNTIwYWZkMjAwODYwOGE2Mjg4YjdlNyJ9fX0=",Creeper::isPowered);
    public static Head<Dolphin> DOLPHIN = new Head<>(EntityType.DOLPHIN, "Dolphin", "8b7ccd6d-36de-47e0-8d5a-6f6799c6feb8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGU5Njg4Yjk1MGQ4ODBiNTViN2FhMmNmY2Q3NmU1YTBmYTk0YWFjNmQxNmY3OGU4MzNmNzQ0M2VhMjlmZWQzIn19fQ=", 0.25, 0.02);
    public static Head<Donkey> DONKEY = new Head<>(EntityType.DONKEY, "Donkey", "7e3bc228-5c91-4f9c-a8e7-71ea538ca455", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGUyNWVlOTI3M2FkNTc5ZDQ0YmY0MDZmNmY2Mjk1NTg2NDgxZWExOThmZDU3MjA3NmNkMGM1ODgyZGE3ZTZjYyJ9fX0=",0.09, 0.09);
    public static Head<Drowned> DROWNED = new Head<>(EntityType.DROWNED, "Drowned", "2f169660-61be-46bd-acb5-1abef9fe5731", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNmN2NjZjYxZGJjM2Y5ZmU5YTYzMzNjZGUwYzBlMTQzOTllYjJlZWE3MWQzNGNmMjIzYjNhY2UyMjA1MSJ9fX0=", 0.04, 0.02);
    public static Head<ElderGuardian> ELDER_GUARDIAN = new Head<>(EntityType.ELDER_GUARDIAN, "Elder Guardian", "566bf310-f717-45d6-bac2-56325a9d55b3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGEyZDY0ZjRhMDBlOWM4NWY2NzI2MmVkY2FjYjg0NTIzNTgxYWUwZjM3YmRhYjIyZGQ3MDQ1MjRmNjJlMTY5ZiJ9fX0=", 0.3, 0.5);
    public static Head<EnderDragon> ENDER_DRAGON = new Head<>(EntityType.ENDER_DRAGON, Material.DRAGON_HEAD);
    public static Head<Enderman> ENDERMAN = new Head<>(EntityType.ENDERMAN, "Enderman", "0de98464-1274-4dd6-bba8-370efa5d41a8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0=", 0.0002, 0.0001);
    public static Head<Endermite> ENDERMITE = new Head<>(EntityType.ENDERMITE, "Endermite", "00c30a52-dce7-481e-ba38-0fe775b4f2ea", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGM2YjY1YzIyYjQ0NjViYTY3OTNiMjE5NWNkNTA4NGNlODNiODhkY2E2ZTU1ZWI5NDg0NTQwYWNkNzM1MmE1MCJ9fX0=", 0.02, 0.01);
    public static Head<Evoker> EVOKER = new Head<>(EntityType.EVOKER, "Evoker", "f4d177cf-b22d-4407-80f3-480c50cc2d5e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzkwZmJkODhmNjU5ZDM5NjNjNjhjYmJjYjdjNzEyMWQ4MTk1YThiZTY1YmJkMmJmMTI1N2QxZjY5YmNjYzBjNyJ9fX0=", 0.14, 0.02);
    public static Head<Fox> RED_FOX = new Head<>(EntityType.FOX, "Fox", "ea7df60c-0001-444b-8529-561f9c94b842", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdlMDA0MzExMWJjNTcwOTA4NTYyNTkxNTU1NzFjNzkwNmU3MDcwNDZkZjA0MWI4YjU3MjcwNGM0NTFmY2Q4MiJ9fX0=", fox -> fox.getFoxType() == Type.RED, 0.018, 0.004);
    public static Head<Fox> SNOW_FOX = new Head<>(EntityType.FOX, "Snow Fox", "b28c7bb1-b6a9-497b-8a2d-336876e85a9d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE0MzYzNzdlYjRjNGI0ZTM5ZmIwZTFlZDg4OTlmYjYxZWUxODE0YTkxNjliOGQwODcyOWVmMDFkYzg1ZDFiYSJ9fX0=", fox -> fox.getFoxType() == Type.SNOW, 0.018, 0.004);
    public static Head<Ghast> GHAST = new Head<>(EntityType.GHAST, "Ghast", "021a33db-c9c2-4b77-b834-56c4549ce1ab", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzUzZGUzMWEyZDAwNDFhNmVmNzViZjdhNmM4NDY4NDY0ZGIxYWFhNjIwMWViYjFhNjAxM2VkYjIyNDVjNzYwNyJ9fX0=", 0.025, 0.0125);
    public static Head<Guardian> GUARDIAN = new Head<>(EntityType.GUARDIAN, "Guardian", "12d2f3a0-a7db-45b9-8e0d-2e3cd1c77a4e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTJiYTM0NDE2NjcwNDU0YjFhMjA0OTZmODBiOTM5ODUyOWY0OTAwM2ZjNjEzZWI5MzAyNDhlYTliNWQxYTM5MSJ9fX0=", 0.002, 0.001);
    public static Head<Hoglin> HOGLIN = new Head<>(EntityType.HOGLIN, "Hoglin", "1d531575-b373-4747-a24b-2df1c320ea9b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U4ZWFhNmMxOGZiNmVkZDFkYzJiNTViMTZlMDE1MGEyZmU2ZTI5ZDI0YThkOGQ0ZmJhZDE5ZGYzNTM0NTUwNiJ9fX0=", 0.021, 0.02);
    public static Head<Horse> WHITE_HORSE = new Head<>(EntityType.HORSE, "White Horse", "5465197e-5f7b-4b10-a52e-6ef4763bfd2a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdiYzYxNjA5NzMwZjJjYjAxMDI2OGZhYjA4MjFiZDQ3MzUyNjk5NzUwYTE1MDU5OWYyMWMzZmM0ZTkyNTkxYSJ9fX0=", horse -> horse.getColor() == Horse.Color.WHITE, 0.02, 0.01);
    public static Head<Horse> CREAMY_HORSE = new Head<>(EntityType.HORSE, "Creamy Horse", "cc7276a0-d107-4da0-857a-7a94d7b69d74", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDJhMGQ1NGNjMDcxMjY3ZDZiZmQ1ZjUyM2Y4Yzg5ZGNmZGM1ZTgwNWZhYmJiNzYwMTBjYjNiZWZhNDY1YWE5NCJ9fX0=", horse -> horse.getColor() == Horse.Color.CREAMY, 0.02, 0.01);
    public static Head<Horse> CHESTNUT_HORSE = new Head<>(EntityType.HORSE, "Chestnut Horse", "a98ef2d5-9c48-49aa-8212-9d5a00b9f6b0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmM4NzIwZDFmNTUyNjkzYjQwYTlhMzNhZmE0MWNlZjA2YWZkMTQyODMzYmVkOWZhNWI4ODdlODhmMDVmNDlmYSJ9fX0=", horse -> horse.getColor() == Horse.Color.CHESTNUT, 0.02, 0.01);
    public static Head<Horse> BROWN_HORSE = new Head<>(EntityType.HORSE, "Brown Horse", "612aea37-f6ab-4425-b45a-9c3d67a11310", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjc3MTgwMDc3MGNiNGU4MTRhM2Q5MTE4NmZjZDc5NWVjODJlMDYxMDJmZjdjMWVlNGU1YzM4MDEwMmEwYzcwZiJ9fX0=", horse -> horse.getColor() == Horse.Color.BROWN, 0.02, 0.01);
    public static Head<Horse> BLACK_HORSE = new Head<>(EntityType.HORSE, "Black Horse", "de101c2d-7f7c-4bcc-af82-4a1e54d341fe", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjcyM2ZhNWJlNmFjMjI5MmE3MjIzMGY1ZmQ3YWI2NjM0OTNiZDhmN2U2NDgxNjQyNGRjNWJmMjRmMTMzODkwYyJ9fX0=", horse -> horse.getColor() == Horse.Color.BLACK, 0.02, 0.01);
    public static Head<Horse> GRAY_HORSE = new Head<>(EntityType.HORSE, "Gray Horse", "50d151e0-4435-495f-a076-77f133f4e6c2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI1OTg2MTAyMTgxMDgzZmIzMTdiYzU3MTJmNzEwNGRhYTVhM2U4ODkyNjRkZmViYjkxNTlmNmUwOGJhYzkwYyJ9fX0=", horse -> horse.getColor() == Horse.Color.GRAY, 0.02, 0.01);
    public static Head<Horse> DARK_BROWN_HORSE = new Head<>(EntityType.HORSE, "Dark Brown Horse", "8de6a9d9-c214-4bd1-aa52-3d62d2b3f7d1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YyMzQxYWFhMGM4MmMyMmJiYzIwNzA2M2UzMTkyOTEwOTdjNTM5YWRhZDlhYTkxM2ViODAwMWIxMWFhNTlkYSJ9fX0=", horse -> horse.getColor() == Horse.Color.DARK_BROWN, 0.02, 0.01);
    public static Head<Husk> HUSK = new Head<>(EntityType.HUSK, "Husk", "c0394bc5-8928-4d83-94f4-7cd28a31dbbc", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzMzODMxOGJjOTFhMzZjZDVhYjZhYTg4NWM5YTRlZTJiZGFjZGFhNWM2NmIyYTk5ZGZiMGE1NjA5ODNmMjQ4MCJ9fX0=", 0.03, 0.01);
    public static Head<Illusioner> ILLUSIONER = new Head<>(EntityType.ILLUSIONER, "Illusioner", "1e74f846-b3e5-46bd-8708-582f9f34ebe3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM4MjcwMWM2N2Q2YzU0YzkwNzU1ODg5MWRjMTc2MjI1MTEyNTE4NzcxZTA2MWM1ZDhiZDkxODQ3OWU2YmRkOCJ9fX0=", 0.14, 0.02);
    public static Head<IronGolem> IRON_GOLEM = new Head<>(EntityType.IRON_GOLEM, "Iron Golem", "84888e02-b912-4ef0-8693-a2a81ac5b07a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmU3YzA3MTlmYWJlMTE2ZGNlNjA1MTk5YmNhZGM2OWE1Mzg4NjA4NjRlZjE1NzA2OTgzZmY2NjI4MjJkOWZlMyJ9fX0=", 0.055, 0.015);
    public static Head<Llama> CREAMY_LLAMA = new Head<>(EntityType.LLAMA, "Creamy Llama", "dd0a3919-e919-428c-9298-6dcc416fec9d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQ2N2ZkNGJmZjI5MzI2OWNiOTA4OTc0ZGNhODNjMzM0ODVlNDM1ZWQ1YThlMWRiZDY1MjFjNjE2ODcxNDAifX19", llama -> llama.getColor() == Color.CREAMY, 0.04, 0.02);
    public static Head<Llama> WHITE_LLAMA = new Head<>(EntityType.LLAMA, "White Llama", "60d7893f-b634-48b8-8d6e-f07fa14f5115", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODAyNzdlNmIzZDlmNzgxOWVmYzdkYTRiNDI3NDVmN2FiOWE2M2JhOGYzNmQ2Yjg0YTdhMjUwYzZkMWEzNThlYiJ9fX0=", llama -> llama.getColor() == Color.WHITE, 0.04, 0.02);
    public static Head<Llama> BROWN_LLAMA = new Head<>(EntityType.LLAMA, "Brown Llama", "75fb08e5-2419-46fa-bf09-57362138f234", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiMWVjZmY3N2ZmZTNiNTAzYzMwYTU0OGViMjNhMWEwOGZhMjZmZDY3Y2RmZjM4OTg1NWQ3NDkyMTM2OCJ9fX0=", llama -> llama.getColor() == Color.BROWN, 0.04, 0.02);
    public static Head<Llama> GRAY_LLAMA = new Head<>(EntityType.LLAMA, "Gray Llama", "edca7a0d-770f-43d6-8ffc-f6a00e94e477", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2YyNGU1NmZkOWZmZDcxMzNkYTZkMWYzZTJmNDU1OTUyYjFkYTQ2MjY4NmY3NTNjNTk3ZWU4MjI5OWEifX19\\", llama -> llama.getColor() == Color.GRAY, 0.04, 0.02);
    public static Head<MagmaCube> MAGMA_CUBE = new Head<>(EntityType.MAGMA_CUBE, "Magma Cube", "568b0e82-076c-4162-9c78-66617d44dcd8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjgxNzE4ZDQ5ODQ4NDdhNGFkM2VjMDgxYTRlYmZmZDE4Mzc0MzIzOWFlY2FiNjAzMjIxMzhhNzI2MDk4MTJjMyJ9fX0=", 0.005, 0.001);
    public static Head<MushroomCow> RED_MOOSHROOM = new Head<>(EntityType.MUSHROOM_COW, "Red Mooshroom", "c1cfcfe7-8dd8-4a60-b155-b590502b63af", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE4MDYwNmU4MmM2NDJmMTQxNTg3NzMzZTMxODBhZTU3ZjY0NjQ0MmM5ZmZmZDRlNTk5NzQ1N2UzNDMxMWEyOSJ9fX0=", cow -> cow.getVariant() == Variant.RED, 0.002, 0.001);
    public static Head<MushroomCow> BROWN_MOOSHROOM = new Head<>(EntityType.MUSHROOM_COW, "Brown Mooshroom", "50108493-e74b-454c-82f0-0948811c4aed", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U2NDY2MzAyYTVhYjQzOThiNGU0NzczNDk4MDhlNWQ5NDAyZWEzYWQ4ZmM0MmUyNDQ2ZTRiZWQwYTVlZDVlIn19fQ==", cow -> cow.getVariant() == Variant.BROWN, 0.002, 0.001);
    public static Head<Mule> MULE = new Head<>(EntityType.MULE, "Mule", "b9600962-635f-432e-ad95-f7d6688ec135", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFjMjI0YTEwMzFiZTQzNGQyNWFlMTg4NWJmNGZmNDAwYzk4OTRjNjliZmVmNTZhNDkzNTRjNTYyNWMwYzA5YyJ9fX0=", 0.10, 0.05);
    public static Head<Ocelot> OCELOT = new Head<>(EntityType.OCELOT, "Ocelot", "781441b9-d42c-4f98-86ae-b0f7b0996ccf", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTE3NWNjNDNlYThhZTIwMTY4YTFmMTcwODEwYjRkYTRkOWI0ZWJkM2M5OTc2ZTlmYzIyZTlmOTk1YzNjYmMzYyJ9fX0=", 0.04, 0.02);
    public static Head<Panda> AGGRESSIVE_PANDA = new Head<>(EntityType.PANDA, "Aggressive Panda", "30697174-9b31-421c-bc7a-fb6d60cabb6e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTU0NmU0MzZkMTY2YjE3ZjA1MjFiZDg1MzhlYTEzY2Q2ZWUzYjVkZjEwMmViMzJlM2U0MjVjYjI4NWQ0NDA2MyJ9fX0=", panda -> panda.getMainGene() == Gene.AGGRESSIVE, 0.018, 0.004);
    public static Head<Panda> LAZY_PANDA = new Head<>(EntityType.PANDA, "Lazy Panda", "a4de0438-255d-446e-8658-fa0d18979877", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTg3ZjFmNWRiMmUyNGRmNGRhYWVkNDY4NWQ2YWVlNWRlYjdjZGQwMjk2MzBmMDA3OWMxZjhlMWY5NzQxYWNmZCJ9fX0=", panda -> panda.getMainGene() == Gene.LAZY, 0.018, 0.004);
    public static Head<Panda> PLAYFUL_PANDA = new Head<>(EntityType.PANDA, "Playful Panda", "6d3b9dfe-9e27-45de-8a81-6a962f663c03", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGNhZGQ0YmYzYzRjYWNlOTE2NjgwZTFmZWY5MGI1ZDE2YWQ2NjQzOTUxNzI1NjY4YmE2YjQ5OTZiNjljYTE0MCJ9fX0=", panda -> panda.getMainGene() == Gene.PLAYFUL, 0.018, 0.004);
    public static Head<Panda> WORRIED_PANDA = new Head<>(EntityType.PANDA, "Worried Panda", "a26e2c67-e282-4673-8e32-099c2e7ffc9a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI4NmZkMWJmOGNiY2UyM2JjMDhmYjkwNjkxNzE3NjExYWRkYzg1YWI4MjNiNzcxNGFlYzk4YTU2NjBlZmYxNSJ9fX0=", panda -> panda.getMainGene() == Gene.WORRIED, 0.018, 0.004);
    public static Head<Panda> BROWN_PANDA = new Head<>(EntityType.PANDA, "Brown Panda", "a2a3e8df-bc0c-4adf-838b-1d687a922828", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ1ZjZkNjEyNjcyODY3MWI0NGMxYzc3NWY5OTYxNzQyNGUzMzYxMWI1ZDMxYWQyYWNmZjI4MDRlYjk2ZWIwNiJ9fX0=", panda -> panda.getMainGene() == Gene.BROWN && panda.getHiddenGene() == Gene.BROWN, 0.018, 0.004);
    public static Head<Panda> WEAK_PANDA = new Head<>(EntityType.PANDA, "Weak Panda", "c6f2e55c-eff8-40aa-8c90-153691551e01", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M1NmEzNTVmYmUwZTJmYmQyOGU4NWM0ZDgxNWZmYTVkMWY5ZDVmODc5OGRiYzI1OWZmODhjNGFkZGIyMDJhZSJ9fX0=", panda -> panda.getMainGene() == Gene.WEAK && panda.getHiddenGene() == Gene.WEAK, 0.018, 0.004);
    public static Head<Panda> OTHER_PANDA = new Head<>(EntityType.PANDA, "Panda", "d92092c3-4abc-48f3-a52f-d8de6de4c981", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTlkZjQ3ZTAxNWQ1YzFjNjhkNzJiZTExYmI2NTYzODBmYzZkYjUzM2FhYjM4OTQxYTkxYjFkM2Q1ZTM5NjQ5NyJ9fX0=", panda -> true, 0.018, 0.004);
    public static Head<Parrot> RED_PARROT = new Head<>(EntityType.PARROT, "Red Parrot", "06e5e87b-5413-4c85-8c10-42c6cf08d4f9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBhM2Q0N2Y1NGU3MWE1OGJmOGY1N2M1MjUzZmIyZDIxM2Y0ZjU1YmI3OTM0YTE5MTA0YmZiOTRlZGM3NmVhYSJ9fX0=", parrot -> parrot.getVariant() == Parrot.Variant.RED);
    public static Head<Parrot> BLUE_PARROT = new Head<>(EntityType.PARROT, "Blue Parrot", "a11c92dc-d6f9-4b90-95f5-70df0dab0526", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk0YmQzZmNmNGQ0NjM1NGVkZThmZWY3MzEyNmRiY2FiNTJiMzAxYTFjOGMyM2I2Y2RmYzEyZDYxMmI2MWJlYSJ9fX0=", parrot -> parrot.getVariant() == Parrot.Variant.BLUE);
    public static Head<Parrot> GREEN_PARROT = new Head<>(EntityType.PARROT, "Green Parrot", "145468b4-09d4-408c-8a63-b4b54694a1cb", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmExZGMzMzExNTIzMmY4MDA4MjVjYWM5ZTNkOWVkMDNmYzE4YWU1NTNjMjViODA1OTUxMzAwMGM1OWUzNTRmZSJ9fX0=", parrot -> parrot.getVariant() == Parrot.Variant.GREEN);
    public static Head<Parrot> CYAN_PARROT = new Head<>(EntityType.PARROT, "Cyan Parrot", "ca801759-47ad-4812-80c3-fc455f59b8a6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI2OGNlMzdiZTg1MDdlZDY3ZTNkNDBiNjE3ZTJkNzJmNjZmOWQyMGIxMDZlZmIwOGU2YmEwNDFmOWI5ZWYxMCJ9fX0=", parrot -> parrot.getVariant() == Parrot.Variant.CYAN);
    public static Head<Parrot> GRAY_PARROT = new Head<>(EntityType.PARROT, "Gray Parrot", "c39ed838-f42f-4212-8215-401fb411280d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFiZTcyM2FhMTczOTNkOTlkYWRkYzExOWM5OGIyYzc5YzU0YjM1ZGViZTA1YzcxMzhlZGViOGQwMjU2ZGM0NiJ9fX0=", parrot -> parrot.getVariant() == Parrot.Variant.GRAY);
    public static Head<Phantom> PHANTOM = new Head<>(EntityType.PHANTOM, "Phantom", "9290add8-c291-4a5a-8f8a-594f165406a3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U5NTE1M2VjMjMyODRiMjgzZjAwZDE5ZDI5NzU2ZjI0NDMxM2EwNjFiNzBhYzAzYjk3ZDIzNmVlNTdiZDk4MiJ9fX0=", 0.10, 0.01);
    public static Head<Pig> PIG = new Head<>(EntityType.PIG, "Pig", "ad36c426-036c-4637-9826-ed499011f649", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDFlZTc2ODFhZGYwMDA2N2YwNGJmNDI2MTFjOTc2NDEwNzVhNDRhZTJiMWMwMzgxZDVhYzZiMzI0NjIxMWJmZSJ9fX0=", 0.005, 0.001);
    public static Head<Piglin> PIGLIN = new Head<>(EntityType.PIGLIN, "Piglin", "661973b6-702f-42b0-8249-cdb1a39b6f9b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFhZmQ4NTM5MTE4MmE5ZjlkZTRmY2UyOWVhZjAyNTE0Y2MyZTA0NDgxNTc3ZGE1ZWRlYjU4YjE3ZTc1NzEzNSJ9fX0=", 0.01, 0.01);
    public static Head<Pillager> PILLAGER = new Head<>(EntityType.PILLAGER, "Pillager", "4fd83a46-8adf-416b-a93f-ecc6c5495db2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzIyNWYwYjQ5YzUyOTUwNDhhNDA5YzljNjAxY2NhNzlhYThlYjUyYWZmNWUyMDMzZWJiODY1ZjQzNjdlZjQzZSJ9fX0=", 0.035, 0.005);
    public static Head<PolarBear> POLAR_BEAR = new Head<>(EntityType.POLAR_BEAR, "Polar Bear", "8da54a4d-5286-4cb1-b230-2ecf43afb74d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q4NzAyOTExZTYxNmMwZDMyZmJlNzc4ZDE5NWYyMWVjY2U5MDI1YmNiZDA5MTUxZTNkOTdhZjMxOTJhYTdlYyJ9fX0=", 0.10, 0.05);
    public static Head<PufferFish> PUFFERFISH = new Head<>(EntityType.PUFFERFISH, "Pufferfish", "c70b7dc2-1564-43ad-a140-f4922a01375b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTI3MzNkNWRhNTljODJlYWYzMTBiMzgyYWZmNDBiZDUxM2M0NDM1NGRiYmFiZmUxNGIwNjZhNTU2ODEwYTdmOSJ9fX0=", 0.15, 0.01);
    public static Head<Rabbit> TOAST_RABBIT = new Head<>(EntityType.RABBIT, "Toast", "9e2c8ab3-3e67-42be-ae8f-050ec894d1f1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFhNTdjM2QwYTliMTBlMTNmNjZkZjc0MjAwY2I4YTZkNDg0YzY3MjIyNjgxMmQ3NGUyNWY2YzAyNzQxMDYxNiJ9fX0=", rabbit -> rabbit.getName().equals("Toast"), 0.10, 0.05);
    public static Head<Rabbit> BROWN_RABBIT = new Head<>(EntityType.RABBIT, "Brown Rabbit", "95707716-a7c2-4741-bb70-03761cf1887a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ZkNGY4NmNmNzQ3M2ZiYWU5M2IxZTA5MDQ4OWI2NGMwYmUxMjZjN2JiMTZmZmM4OGMwMDI0NDdkNWM3Mjc5NSJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.BROWN, 0.10, 0.05);
    public static Head<Rabbit> WHITE_RABBIT = new Head<>(EntityType.RABBIT, "White Rabbit", "1288e126-41dd-4608-b304-86d84464d5e4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU0MmQ3MTYwOTg3MTQ4YTVkOGUyMGU0NjliZDliM2MyYTM5NDZjN2ZiNTkyM2Y1NWI5YmVhZTk5MTg1ZiJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.WHITE, 0.10, 0.05);
    public static Head<Rabbit> BLACK_RABBIT = new Head<>(EntityType.RABBIT, "Black Rabbit", "5492c4d3-b524-4df5-9267-1b48e86491e1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjJiNDI1ZmYyYTIzNmFiMTljYzkzOTcxOTVkYjQwZjhmMTg1YjE5MWM0MGJmNDRiMjZlOTVlYWM5ZmI1ZWZhMyJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.BLACK, 0.10, 0.05);
    public static Head<Rabbit> BLACK_WHITE_RABBIT = new Head<>(EntityType.RABBIT, "Black and White Rabbit", "dac10930-ace7-4c3e-a977-85f27f19a011", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzVmNzJhMjE5NWViZjQxMTdjNTA1NmNmZTJiNzM1N2VjNWJmODMyZWRlMTg1NmE3NzczZWU0MmEwZDBmYjNmMCJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.BLACK_AND_WHITE, 0.10, 0.05);
    public static Head<Rabbit> GOLD_RABBIT = new Head<>(EntityType.RABBIT, "Gold Rabbit", "87a49ce2-fbf9-43e9-a120-8bf58f5da6b2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY3YjcyMjY1NmZkZWVjMzk5NzRkMzM5NWM1ZTE4YjQ3YzVlMjM3YmNlNWJiY2VkOWI3NTUzYWExNGI1NDU4NyJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.GOLD, 0.10, 0.05);
    public static Head<Rabbit> SALT_PEPPER_RABBIT = new Head<>(EntityType.RABBIT, "Salt and Pepper Rabbit", "7f6a75ab-3039-475b-8df8-7b46be3614f0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTIzODUxOWZmMzk4MTViMTZjNDA2MjgyM2U0MzE2MWZmYWFjOTY4OTRmZTA4OGIwMThlNmEyNGMyNmUxODFlYyJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.SALT_AND_PEPPER, 0.10, 0.05);
    public static Head<Rabbit> KILLER_RABBIT = new Head<>(EntityType.RABBIT, "The Killer Rabbit", "2edafc73-f4d7-4707-b532-8059c3794258", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFkZDc2NzkyOWVmMmZkMmQ0M2U4NmU4NzQ0YzRiMGQ4MTA4NTM0NzEyMDFmMmRmYTE4Zjk2YTY3ZGU1NmUyZiJ9fX0=", rabbit -> rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY, 0.10, 0.05);
    public static Head<Ravager> RAVAGER = new Head<>(EntityType.RAVAGER, "Ravager", "fd58e30c-7570-4401-93dd-ec28b8cd19f4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWI0ZGIyOTg2MTQwZTI1MWUzMmU3MGVkMDhjOGEwODE3MjAzMTNjZTI1NzYzMmJlMWVmOTRhMDczNzM5NGRiIn19fQ==", 0.14, 0.02);
    public static Head<Salmon> SALMON = new Head<>(EntityType.SALMON, "Salmon", "b70bd4aa-826a-47be-a27b-823c5486e826", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzkxZDllNjliNzk1ZGE0ZWFhY2ZjZjczNTBkZmU4YWUzNjdmZWQ4MzM1NTY3MDZlMDQwMzM5ZGQ3ZmUwMjQwYSJ9fX0=", 0.1, 0.01);
    public static Head<Shulker> SHULKER = new Head<>(EntityType.SHULKER, "Shulker", "4e7bb834-53a8-41a3-a799-7124e2c8413c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI5ZTZhZjZiODE5ZjNkOTBlNjdjZTJlNzA1OWZiZWYzMWRhMmFhOTUzZDM1ZTM0NTRmMTAyMWZhOTEyZWZkZSJ9fX0=");
    public static Head<Silverfish> SILVERFISH = new Head<>(EntityType.SILVERFISH, "Silverfish", "24643d74-22c0-4bb6-a017-71bee3f05429", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjI1ZTlmYWUzNzE2NjRkZTFhODAwYzg0ZDAyNTEyNGFiYjhmMTUxMTE4MDdjOGJjMWFiOTEyNmFhY2JkNGY5NSJ9fX0=", 0.02, 0.01);
    public static Head<SkeletonHorse> SKELETON_HORSE = new Head<>(EntityType.SKELETON_HORSE, "Skeleton Horse", "d11eb7d3-a245-49aa-8a1e-9aa4c4e52b53", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmUyMjY3MDViZDJhOWU3YmI4ZDZiMGY0ZGFhOTY5YjllMTJkNGFlNWM2NmRhNjkzYmI1ZjRhNGExZTZhYTI5NiJ9fX0=", 0.20, 0.05);
    public static Head<Slime> SLIME = new Head<>(EntityType.SLIME, "Slime", "a947d318-dacd-4904-8116-d9f68955aaa6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzA2NDI0ZWM3YTE5NmIxNWY5YWQ1NzMzYTM2YTZkMWYyZTZhMGQ0MmZmY2UxZTE1MDhmOTBmMzEyYWM0Y2FlZCJ9fX0=", 0.005, 0.001);
    public static Head<Snowman> SNOW_GOLEM = new Head<>(EntityType.SNOWMAN, "Snow Golem", "4ff3822f-2323-4e0d-b17c-dfdc3686dba0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2FhM2UxN2VmMWIyOWE0Yjg3ZmE0M2RlZTFkYjEyYzQxZmQzOWFhMzg3ZmExM2FmMmEwNzliNWIzNzhmZGU4YiJ9fX0=", 0.02, 0.01);
    public static Head<Spider> SPIDER = new Head<>(EntityType.SPIDER, "Spider", "50f4e841-3ac5-4643-95ce-4a1f440165c3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBjNDEwMDQ1Y2QzNzQ5YzNhOGVkODU2ZGY0MTFmNmMzM2U5YThhNmY5ZTU3ZTUyMTYwOGE4YWQ4ZWQ2ZWIzNyJ9fX0=", 0.002, 0.001);
    public static Head<Squid> SQUID = new Head<>(EntityType.SQUID, "Squid", "118bbae9-61f7-4246-ad07-add3579e42ae", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1MWI3ZDlhNGYzNmNmZTMxZmQ1OWQ4YzkwMGU0MTlhMTM1MTQ0MTA1ZTdhOTgxY2FhNWExNjhkY2ZmMzI1YiJ9fX0=", 0.02, 0.01);
    public static Head<Stray> STRAY = new Head<>(EntityType.STRAY, "Stray", "7c8e03e3-7aa6-4571-9891-ec57a90abca9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTkyYjU1OTcwODVlMzVkYjUzZDliZGEwMDhjYWU3MmIyZjAwY2Q3ZDRjZDhkYzY5ZmYxNzRhNTViNjg5ZTZlIn19fQ==", 0.05, 0.05);
    public static Head<Strider> STRIDER = new Head<>(EntityType.STRIDER, "Strider", "9fde30af-3e79-433b-b4af-ae9cc52b0c96", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWM0MGZhZDFjMTFkZTllNjQyMmI0MDU0MjZlOWI5NzkwN2YzNWJjZTM0NWUzNzU4NjA0ZDNlN2JlN2RmODg0In19fQ==", strider -> !strider.isShivering(), 0.035, 0.05);
    public static Head<Strider> FREEZING_STRIDER = new Head<>(EntityType.STRIDER, "Freezing Strider", "d843d702-9a8c-419a-80d5-da6c51a2c006", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjcxMzA4NWE1NzUyN2U0NTQ1OWMzOGZhYTdiYjkxY2FiYjM4MWRmMzFjZjJiZjc5ZDY3YTA3MTU2YjZjMjMwOSJ9fX0=", Strider::isShivering, 0.035, 0.05);
    public static Head<TraderLlama> CREAMY_TRADER_LLAMA = new Head<>(EntityType.TRADER_LLAMA, "Creamy Trader Llama", "b8e21edd-c25b-4673-9602-6671007f5088", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg5YTJlYjE3NzA1ZmU3MTU0YWIwNDFlNWM3NmEwOGQ0MTU0NmEzMWJhMjBlYTMwNjBlM2VjOGVkYzEwNDEyYyJ9fX0=", traderLlama -> traderLlama.getColor() == Color.CREAMY, 0.29, 0.07);
    public static Head<TraderLlama> WHITE_TRADER_LLAMA = new Head<>(EntityType.TRADER_LLAMA, "White Trader Llama", "47dbdab5-105f-42bc-9580-c61cee9231f3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA4N2E1NTZkNGZmYTk1ZWNkMjg0NGYzNTBkYzQzZTI1NGU1ZDUzNWZhNTk2ZjU0MGQ3ZTc3ZmE2N2RmNDY5NiJ9fX0=", traderLlama -> traderLlama.getColor() == Color.WHITE, 0.29, 0.07);
    public static Head<TraderLlama> BROWN_TRADER_LLAMA = new Head<>(EntityType.TRADER_LLAMA, "Brown Trader Llama", "a957be18-324a-4984-a81b-f556a793a64a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQyNDc4MGIzYzVjNTM1MWNmNDlmYjViZjQxZmNiMjg5NDkxZGY2YzQzMDY4M2M4NGQ3ODQ2MTg4ZGI0Zjg0ZCJ9fX0=", traderLlama -> traderLlama.getColor() == Color.BROWN, 0.29, 0.07);
    public static Head<TraderLlama> GRAY_TRADER_LLAMA = new Head<>(EntityType.TRADER_LLAMA, "Gray Trader Llama", "34bfbc2b-6c59-47df-8cf6-7457ad15165a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmU0ZDhhMGJjMTVmMjM5OTIxZWZkOGJlMzQ4MGJhNzdhOThlZTdkOWNlMDA3MjhjMGQ3MzNmMGEyZDYxNGQxNiJ9fX0=", traderLlama -> traderLlama.getColor() == Color.GRAY, 0.29, 0.07);
    public static Head<TropicalFish> TROPICAL_FISH = new Head<>(EntityType.TROPICAL_FISH, "Tropical Fish", "58b108f2-efc5-4872-93ed-d392a33f5215", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRhMGM4NGRjM2MwOTBkZjdiYWZjNDM2N2E5ZmM2Yzg1MjBkYTJmNzNlZmZmYjgwZTkzNGQxMTg5ZWFkYWM0MSJ9fX0=", 0.10, 0.01);
    public static Head<Turtle> TURTLE = new Head<>(EntityType.TURTLE, "Turtle", "da6f13e0-ce0d-476a-bd56-4d1b80f160a3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0OTMxMjAwYWQ0NjBiNjUwYTE5MGU4ZDQxMjI3YzM5OTlmYmViOTMzYjUxY2E0OWZkOWU1OTIwZDFmOGU3ZCJ9fX0=", 0.10, 0.01);
    public static Head<Vex> VEX = new Head<>(EntityType.VEX, "Vex", "8a0e3d9a-9875-48e1-b9f7-12abc52b181d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI4ZTE0ZWQzMTRlYTllYjQ0MTkzMjQxNTllZDA4ZTc3N2JhMTg3NWFkZTI5ODllYWEwZjUzMTBkYTc3MmU1NiJ9fX0=", 0.02, 0.01);
    public static Head<Villager> ARMORER_VILLAGER = new Head<>(EntityType.VILLAGER, "Armorer Villager", "9f03a40d-b362-47ae-8aca-224005b8a9f9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWVmNjI3ZjU2NmFjMGE3ODI4YmFkOTNlOWU0Yjk2NDNkOTlhOTI4YTEzZDVmOTc3YmY0NDFlNDBkYjEzMzZiZiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.ARMORER);
    public static Head<Villager> BUTCHER_VILLAGER = new Head<>(EntityType.VILLAGER, "Butcher Villager", "8b3e5814-bc3f-426c-b82d-f8963a814cd4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTFiYWQ2NDE4NWUwNGJmMWRhZmUzZGE4NDkzM2QwMjU0NWVhNGE2MzIyMWExMGQwZjA3NzU5MTc5MTEyYmRjMiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.BUTCHER);
    public static Head<Villager> CARTOGRAPHER_VILLAGER = new Head<>(EntityType.VILLAGER, "Cartographer Villager", "a4c8ec34-0799-44b9-bcae-e14c26a511d2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNhZWNmYmU4MDFjZjMyYjVkMWIwYjFmNjY4MDA0OTY2NjE1ODY3OGM1M2Y0YTY1MWZjODNlMGRmOWQzNzM4YiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.CARTOGRAPHER);
    public static Head<Villager> CLERIC_VILLAGER = new Head<>(EntityType.VILLAGER, "Cleric Villager", "a997a7dc-68a0-41b9-b47e-a60f6217be1d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWI5ZTU4MmUyZjliODlkNTU2ZTc5YzQ2OTdmNzA2YjFkZDQ5MjllY2FlM2MwN2VlOTBiZjFkNWJlMzE5YmY2ZiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.CLERIC);
    public static Head<Villager> FARMER_VILLAGER = new Head<>(EntityType.VILLAGER, "Farmer Villager", "8c388a81-b2a5-43dc-b452-4d8221454e12", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDkyNzJkMDNjZGE2MjkwZTRkOTI1YTdlODUwYTc0NWU3MTFmZTU3NjBmNmYwNmY5M2Q5MmI4ZjhjNzM5ZGIwNyJ9fX0=", villager -> villager.getProfession() == Villager.Profession.FARMER);
    public static Head<Villager> FISHERMAN_VILLAGER = new Head<>(EntityType.VILLAGER, "Fisherman Villager", "a31c4706-fb35-427a-a435-e3638ee9e012", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDE4OWZiNGFjZDE1ZDczZmYyYTU4YTg4ZGYwNDY2YWQ5ZjRjMTU0YTIwMDhlNWM2MjY1ZDVjMmYwN2QzOTM3NiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.FISHERMAN);
    public static Head<Villager> FLETCHER_VILLAGER = new Head<>(EntityType.VILLAGER, "Fletcher Villager", "758d3650-3de5-4b1f-b530-2f43994c461f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY2MTFmMTJlMThjZTQ0YTU3MjM4ZWVmMWNhZTAzY2Q5ZjczMGE3YTQ1ZTBlYzI0OGYxNGNlODRlOWM0ODA1NiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.FLETCHER);
    public static Head<Villager> LEATHERWORKER_VILLAGER = new Head<>(EntityType.VILLAGER, "Leatherworker Villager", "5625c69c-79b2-4c65-9244-acffeccafa26", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=", villager -> villager.getProfession() == Villager.Profession.LEATHERWORKER);
    public static Head<Villager> LIBRARIAN_VILLAGER = new Head<>(EntityType.VILLAGER, "Librarian Villager", "7a079427-3943-455a-b5ad-f12db8a52db4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjYWE1NzRiYWJiNDBlZTBmYTgzZjJmZDVlYTIwY2ZmMzFmZmEyNzJmZTExMzU4OGNlZWU0Njk2ODIxMjhlNyJ9fX0=", villager -> villager.getProfession() == Villager.Profession.LIBRARIAN);
    public static Head<Villager> MASON_VILLAGER = new Head<>(EntityType.VILLAGER, "Mason Villager", "5625c69c-79b2-4c65-9244-acffeccafa26", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=", villager -> villager.getProfession() == Villager.Profession.MASON);
    public static Head<Villager> NITWIT_VILLAGER = new Head<>(EntityType.VILLAGER, "Nitwit Villager", "5625c69c-79b2-4c65-9244-acffeccafa26", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=", villager -> villager.getProfession() == Villager.Profession.NITWIT);
    public static Head<Villager> UNEMPLOYED_VILLAGER = new Head<>(EntityType.VILLAGER, "Unemployed Villager", "5625c69c-79b2-4c65-9244-acffeccafa26", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=", villager -> villager.getProfession() == Villager.Profession.NONE);
    public static Head<Villager> SHEPHERD_VILLAGER = new Head<>(EntityType.VILLAGER, "Shepherd Villager", "90621234-a558-47d9-ad55-520a35949240", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFiZjRlOTE1NGFjOTI3MTk0MWM3MzNlYWNjNjJkYzlmYzBhNmRjMWI1ZDY3Yzc4Y2E5OGFmYjVjYjFiZTliMiJ9fX0=", villager -> villager.getProfession() == Villager.Profession.SHEPHERD);
    public static Head<Villager> TOOLSMITH_VILLAGER = new Head<>(EntityType.VILLAGER, "Toolsmith Villager", "5625c69c-79b2-4c65-9244-acffeccafa26", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWUwZTk1OTFlMTFhYWVmNGMyYzUxZDlhYzY5NTE0ZTM0MDQ4NWRlZmNjMmMxMmMzOGNkMTIzODZjMmVjNmI3OCJ9fX0=", villager -> villager.getProfession() == Villager.Profession.TOOLSMITH);
    public static Head<Villager> WEAPONSMITH_VILLAGER = new Head<>(EntityType.VILLAGER, "Weaponsmith Villager", "89758501-e3e9-466e-83bc-8d4de4c8d540", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODQ3NmZmYTQxMGJiZTdmYTcwOTA5OTY1YTEyNWY0YTRlOWE0ZmIxY2UxYjhiM2MzNGJmYjczYWFmZmQ0Y2U0MyJ9fX0=", villager -> villager.getProfession() == Villager.Profession.WEAPONSMITH);
    public static Head<Vindicator> VINDICATOR = new Head<>(EntityType.VINDICATOR, "Vindicator", "f1ecfcb3-fab9-4f74-b309-9a6c2ef56f2f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhYmFmZGUyN2VlMTJiMDk4NjUwNDdhZmY2ZjE4M2ZkYjY0ZTA0ZGFlMWMwMGNjYmRlMDRhZDkzZGNjNmM5NSJ9fX0=", 0.055, 0.015);
    public static Head<WanderingTrader> WANDERING_TRADER = new Head<>(EntityType.WANDERING_TRADER, "Wandering Trader", "943947ea-3e1a-4fdc-85e5-f538379f05e9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=");
    public static Head<Witch> WITCH = new Head<>(EntityType.WITCH, "Witch", "2d5e7ae2-d55c-4b91-84cb-8203c00a9cb7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTUyMGYxMmM2M2M3OTEyMTg2YzRiZTRlMzBjMzNjNWFjYWVjMGRiMGI2YWJkODM2ZDUxN2Q3NGE2MjI3NWQ0YiJ9fX0=", 0.002, 0.001);
    public static Head<Wither> WITHER = new Head<>(EntityType.WITHER, "Wither", "86034734-f8c6-44a2-9cff-f047dad30794", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWRhMTA4MjhmNjNiN2VjZGVmZDc2N2IzMjQ1ZmJkYWExM2MzZWMwYzZiMTM3NzRmMWVlOGQzMDdjMDM0YzM4MyJ9fX0=");
    // other wither heads?
    public static Head<Wolf> WOLF = new Head<>(EntityType.WOLF, "Wolf", "13bc0cd2-ce73-43a1-952b-35772e7fa0bd", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjY0MzlhNDNlNTY4NzAwODgxNWEyZGQxZmY0YTEzNGMxMjIyMWI3ODIzMzY2NzhiOTc5YWQxM2RjZTM5NjY1ZSJ9fX0=", wolf -> !wolf.isAngry(), 0.02, 0.01);
    public static Head<Wolf> ANGRY_WOLF = new Head<>(EntityType.WOLF, "Angry Wolf", "d195f54b-847b-47a6-8a43-7ec20c21f731", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGQxYWE3ZTNiOTU2NGIzODQ2ZjFkZWExNGYxYjFjY2JmMzk5YmJiMjNiOTUyZGJkN2VlYzQxODAyYTI4OWM5NiJ9fX0=", Wolf::isAngry, 0.02, 0.01);
    public static Head<Zoglin> ZOGLIN = new Head<>(EntityType.ZOGLIN, "Zoglin", "06f078bc-9197-476d-befa-36222d02f14d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZhMGFkYTM0MTFmYmE4Yjg4NTgzZDg2NGIyNTI2MDZlOTNkZmRmNjQ3NjkwZDNjZjRjMDE3YjYzYmFiMTJiMCJ9fX0=", 0.045, 0.05);
    public static Head<ZombieHorse> ZOMBIE_HORSE = new Head<>(EntityType.ZOMBIE_HORSE, "Zombie Horse", "0f35156c-5da4-4278-9d2a-80caf51a6145", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjYxOGZmYmUxY2ZhMjA1OGZlODBhMDY1ZjcwYzEyOGMyMjVhMWUwYmM5ZGVhZjhiMzhiMDM5NTQ0M2Y0MDkwOSJ9fX0=");
    public static Head<ZombieVillager> ZOMBIE_ARMORER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Armorer", "7cfb4bb2-3205-42fb-afd6-70fd580fb8a5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2NzllMDM0NzY3ZDUxODY2MGQ5NDE2ZGM1ZWFmMzE5ZDY5NzY4MmFjNDBjODg2ZTNjMmJjOGRmYTFkZTFkIn19fQ==", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.ARMORER, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_BUTCHER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Butcher", "6c399981-91ff-4d93-b283-ca9af1228382", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWNjZThkNmNlNDEyNGNlYzNlODRhODUyZTcwZjUwMjkzZjI0NGRkYzllZTg1NzhmN2Q2ZDg5MjllMTZiYWQ2OSJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.BUTCHER, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_CARTOGRAPHER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Cartographer", "be6c92ff-fd94-4d56-b9ca-20f0050f3b41", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTYwODAwYjAxMDEyZTk2M2U3YzIwYzhiYTE0YjcwYTAyNjRkMTQ2YTg1MGRlZmZiY2E3YmZlNTEyZjRjYjIzZCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.CARTOGRAPHER, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_CLERIC = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Cleric", "26a97cfb-4cbc-4f75-b847-8d41201abd49", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ODU3OGJlMGUxMjE3MjczNGE3ODI0MmRhYjE0OTY0YWJjODVhYjliNTk2MzYxZjdjNWRhZjhmMTRhMGZlYiJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.CLERIC, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_FARMER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Farmer", "469641b7-ec99-4a62-b597-c7da85426aae", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjc3ZDQxNWY5YmFhNGZhNGI1ZTA1OGY1YjgxYmY3ZjAwM2IwYTJjOTBhNDgzMWU1M2E3ZGJjMDk4NDFjNTUxMSJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.FARMER, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_FISHERMAN = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Fisherman", "1812010d-e392-4c3c-b468-6c9066e26c1b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkwNWQ1M2ZlNGZhZWIwYjMxNWE2ODc4YzlhYjgxYjRiZTUyYzMxY2Q0NzhjMDI3ZjBkN2VjZTlmNmRhODkxNCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.FISHERMAN, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_FLETCHER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Fletcher", "7d20b4d0-05c1-468d-a0fd-e4d8673f9c6e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmVhMjZhYzBlMjU0OThhZGFkYTRlY2VhNThiYjRlNzZkYTMyZDVjYTJkZTMwN2VmZTVlNDIxOGZiN2M1ZWY4OSJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.FLETCHER, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_LEATHERWORKER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Leatherworker", "87b57113-d8ca-4fa4-8214-ea6896e2ce4f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.LEATHERWORKER, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_LIBRARIAN = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Librarian", "2069d306-ad23-4bb9-a6d0-d9e2f57757e6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIyMTFhMWY0MDljY2E0MjQ5YzcwZDIwY2E4MDM5OWZhNDg0NGVhNDE3NDU4YmU5ODhjYzIxZWI0Nzk3Mzc1ZSJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.LIBRARIAN, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_MASON = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Mason", "87b57113-d8ca-4fa4-8214-ea6896e2ce4f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.MASON, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_NITWIT = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Nitwit", "87b57113-d8ca-4fa4-8214-ea6896e2ce4f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.NITWIT, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_VILLAGER = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Villager", "87b57113-d8ca-4fa4-8214-ea6896e2ce4f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.NONE, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_SHEPHERD = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Shepherd", "47f729f2-a01c-46c8-982b-82d2ac59437f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkxMzkxYmVmM2E0NmVmMjY3ZDNiNzE3MTA4NmJhNGM4ZDE3ZjJhNmIwZjgzZmEyYWMzMGVmZTkxNGI3YzI0OSJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.SHEPHERD, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_TOOLSMITH = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Toolsmith", "87b57113-d8ca-4fa4-8214-ea6896e2ce4f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmI1NTJjOTBmMjEyZTg1NWQxMjI1NWQ1Y2Q2MmVkMzhiOWNkN2UzMGU3M2YwZWE3NzlkMTc2NDMzMGU2OTI2NCJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.TOOLSMITH, 0.09, 0.02);
    public static Head<ZombieVillager> ZOMBIE_WEAPONSMITH = new Head<>(EntityType.ZOMBIE_VILLAGER, "Zombie Weaponsmith", "920e0f3f-f4f4-4b99-8eac-509a974a1393", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDM3MDg5NGI1Y2MzMDVkODdhYTA4YzNiNGIwODU4N2RiNjhmZjI5ZTdhM2VmMzU0Y2FkNmFiY2E1MGU1NTI4YiJ9fX0=", zombieVillager -> zombieVillager.getVillagerProfession() == Villager.Profession.WEAPONSMITH, 0.09, 0.02);
    public static Head<PigZombie> ZOMBIFIED_PIGLIN = new Head<>(EntityType.ZOMBIFIED_PIGLIN, "Zombified Piglin", "83ae2721-aa53-4604-b1d2-774cb23f3a21", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRmMDMxMjhiMDAyYTcwNzA4ZDY4MjVlZDZjZjU0ZGRmNjk0YjM3NjZkNzhkNTY0OTAzMGIxY2I4YjM0YzZmYSJ9fX0=", 0.001, 0.001);
    
    
    
    ItemStack skull;
    Predicate<E> check;
    double chance;
    double lootingMult;

    private Head(EntityType type, boolean addToMap, double chance, double lootingMult) {
        this.skull = skullBase.clone();
        if (addToMap) headMap.put(type, this);
        this.chance = chance;
        this.lootingMult = lootingMult;
    }

    private Head(EntityType type, ItemStack item, double chance, double lootingMult) {
        this.skull = item;
        headMap.put(type, this);
        this.chance = chance;
        this.lootingMult = lootingMult;
    }

    private Head(EntityType type, String name, String uuid, String encoded, boolean addToMap, Double chance, Double lootingMult) {
        this(type, addToMap, chance, lootingMult);
        SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        if (meta == null) throw new IllegalArgumentException("Failed to configure a texture!");
        meta.setDisplayName(ChatColor.RESET + ChatColor.YELLOW.toString() + ChatColor.BOLD + name);
        GameProfile profile = new GameProfile(UUID.fromString(uuid), null);
        profile.getProperties().put("textures", new Property("textures", encoded));
        ReflectionUtils.getField(meta.getClass(), "profile", GameProfile.class).set(meta, profile);
        this.skull.setItemMeta(meta);
    }

    private Head(EntityType type, String name, String uuid, String encoded) {
        this(type, name, uuid, encoded, true, 1d, 0d);
    }

    private Head(EntityType type, String name, String uuid, String encoded, Predicate<E> check) {
        this(type, name, uuid, encoded, check, 1d, 0d);
    }

    private Head(EntityType type, String name, String uuid, String encoded, Double chance, Double lootingMult) {
        this(type, name, uuid, encoded, true, chance == null ? 1f : chance, lootingMult == null ? 0f : lootingMult);
    }

    private Head(EntityType type, String name, String uuid, String encoded, Predicate<E> check, Double chance, Double lootingMult) {
        this(type, name, uuid, encoded, false, chance == null ? 1f : chance, lootingMult == null ? 0f : lootingMult);
        this.check = check;
        List<Head<? extends LivingEntity>> mobHeads = multiHeadMap.computeIfAbsent(type, k -> Lists.newArrayList());
        mobHeads.add(this);
    }

    Head(EntityType type, Material material) {
        this(type, new ItemStack(material), 1d, 0d);
    }

    public boolean chance(int lootingLevel) {
        return ThreadLocalRandom.current().nextDouble() < chance + (lootingLevel * lootingMult);
    }

    public boolean test(LivingEntity entity) {
        return ((Predicate<LivingEntity>) check).test(entity);
    }

    public ItemStack getSkull() {
        return skull;
    }
}
