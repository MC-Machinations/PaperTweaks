/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2023 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.papertweaks.modules.mobs.moremobheads;

import java.util.function.Predicate;
import org.bukkit.DyeColor;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.ZombieVillager;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MobHeadCustomizations {

    private MobHeadCustomizations() {
    }

    static void addCustomizations(final MobHead head) {
        if (!head.requiresCustomization()) {
            return;
        }
        final String table = head.lootTable().contains("sheep") ? "sheep.json" : head.lootTable();
        switch (table) {
            case "cat.json":
                cat(head);
                break;
            case "creeper.json":
                creeper(head);
                break;
            case "llama.json":
                llama(head);
                break;
            case "panda.json":
                panda(head);
                break;
            case "trader_llama.json":
                traderLlama(head);
                break;
            case "goat.json":
                goat(head);
                break;
            case "bee.json":
                bee(head);
                break;
            case "fox.json":
                fox(head);
                break;
            case "parrot.json":
                parrot(head);
                break;
            case "horse.json":
                horse(head);
                break;
            case "wither.json":
                break; // TODO implement wither
            case "rabbit.json":
                rabbit(head);
                break;
            case "axolotl.json":
                axolotl(head);
                break;
            case "zombie_villager.json":
                zombieVillager(head);
                break;
            case "mooshroom.json":
                mooshroom(head);
                break;
            case "villager.json":
                villager(head);
                break;
            case "sheep.json":
                sheep(head);
                break;
            case "frog.json":
                frog(head);
                break;
            case "wolf.json":
                wolf(head);
                break;
            default:
                MoreMobHeads.LOGGER.error("{} doesn't have a custom handler", head.lootTable());
        }
    }

    private static void cat(final MobHead head) {
        final Cat.Type type = switch (head.name()) {
            case "Tabby Cat" -> Cat.Type.TABBY;
            case "Tuxedo Cat" -> Cat.Type.BLACK;
            case "Ginger Cat" -> Cat.Type.RED;
            case "Siamese Cat" -> Cat.Type.SIAMESE;
            case "British Shorthair Cat" -> Cat.Type.BRITISH_SHORTHAIR;
            case "Calico Cat" -> Cat.Type.CALICO;
            case "Persian Cat" -> Cat.Type.PERSIAN;
            case "Ragdoll Cat" -> Cat.Type.RAGDOLL;
            case "White Cat" -> Cat.Type.WHITE;
            case "Jellie Cat" -> Cat.Type.JELLIE;
            case "Black Cat" -> Cat.Type.ALL_BLACK;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid cat name");
        };
        final Predicate<Cat> catPredicate = cat -> cat.getCatType() == type;
        head.predicate(catPredicate);
    }

    private static void creeper(final MobHead head) {
        //noinspection SwitchStatementWithTooFewBranches
        final Predicate<Creeper> creeperPredicate = switch (head.name()) {
            case "Charged Creeper" -> Creeper::isPowered;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid creeper name");
        };
        head.predicate(creeperPredicate);
    }

    private static void llama(final MobHead head) {
        final Llama.Color color = switch (head.name()) {
            case "Creamy Llama" -> Llama.Color.CREAMY;
            case "White Llama" -> Llama.Color.WHITE;
            case "Brown Llama" -> Llama.Color.BROWN;
            case "Gray Llama" -> Llama.Color.GRAY;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid llama name");
        };
        final Predicate<Llama> llamaPredicate = llama -> llama.getColor() == color;
        head.predicate(llamaPredicate);
    }

    private static void panda(final MobHead head) {
        final Predicate<Panda> pandaPredicate = switch (head.name()) {
            case "Aggressive Panda" -> panda -> panda.getMainGene() == Panda.Gene.AGGRESSIVE;
            case "Lazy Panda" -> panda -> panda.getMainGene() == Panda.Gene.LAZY;
            case "Playful Panda" -> panda -> panda.getMainGene() == Panda.Gene.PLAYFUL;
            case "Worried Panda" -> panda -> panda.getMainGene() == Panda.Gene.WORRIED;
            case "Brown Panda" -> panda -> panda.getMainGene() == Panda.Gene.BROWN && panda.getHiddenGene() == Panda.Gene.BROWN;
            case "Weak Panda" -> panda -> panda.getMainGene() == Panda.Gene.WEAK && panda.getHiddenGene() == Panda.Gene.WEAK;
            case "Panda" -> panda -> panda.getMainGene() == Panda.Gene.NORMAL;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid panda name");
        };
        head.predicate(pandaPredicate);
    }

    private static void traderLlama(final MobHead head) {
        final Llama.Color color = switch (head.name()) {
            case "Creamy Trader Llama" -> Llama.Color.CREAMY;
            case "White Trader Llama" -> Llama.Color.WHITE;
            case "Brown Trader Llama" -> Llama.Color.BROWN;
            case "Gray Trader Llama" -> Llama.Color.GRAY;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid trader llama name");
        };
        final Predicate<TraderLlama> llamaPredicate = llama -> llama.getColor() == color;
        head.predicate(llamaPredicate);
    }

    private static void goat(final MobHead head) {
        final Predicate<Goat> goatPredicate = switch (head.name()) {
            case "Goat" -> Predicate.not(Goat::isScreaming);
            case "Screaming Goat" -> Goat::isScreaming;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid goat name");
        };
        head.predicate(goatPredicate);
    }

    private static void bee(final MobHead head) {
        final Predicate<Bee> beePredicate = switch (head.name()) {
            case "Angry Pollinated Bee" -> bee -> bee.hasStung() && bee.hasNectar();
            case "Angry Bee" -> bee -> bee.hasStung() && !bee.hasNectar();
            case "Pollinated Bee" -> bee -> !bee.hasNectar() && bee.hasNectar();
            case "Bee" -> bee -> !bee.hasNectar() && !bee.hasStung();
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid bee name");
        };
        head.predicate(beePredicate);

    }

    private static void fox(final MobHead head) {
        final Fox.Type type = switch (head.name()) {
            case "Fox" -> Fox.Type.RED;
            case "Snow Fox" -> Fox.Type.SNOW;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid fox name");
        };
        final Predicate<Fox> foxPredicate = fox -> fox.getFoxType() == type;
        head.predicate(foxPredicate);
    }

    private static void parrot(final MobHead head) {
        final Parrot.Variant variant = switch (head.name()) {
            case "Red Parrot" -> Parrot.Variant.RED;
            case "Blue Parrot" -> Parrot.Variant.BLUE;
            case "Green Parrot" -> Parrot.Variant.GREEN;
            case "Light Blue Parrot" -> Parrot.Variant.CYAN;
            case "Gray Parrot" -> Parrot.Variant.GRAY;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid parrot name");
        };
        final Predicate<Parrot> parrotPredicate = parrot -> parrot.getVariant() == variant;
        head.predicate(parrotPredicate);
    }

    private static void horse(final MobHead head) {
        final Horse.Color color = switch (head.name()) {
            case "White Horse" -> Horse.Color.WHITE;
            case "Creamy Horse" -> Horse.Color.CREAMY;
            case "Chestnut Horse" -> Horse.Color.CHESTNUT;
            case "Brown Horse" -> Horse.Color.BROWN;
            case "Black Horse" -> Horse.Color.BLACK;
            case "Gray Horse" -> Horse.Color.GRAY;
            case "Dark Brown Horse" -> Horse.Color.DARK_BROWN;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid horse name");
        };
        final Predicate<Horse> horsePredicate = horse -> horse.getColor() == color;
        head.predicate(horsePredicate);
    }

    // TODO
    // private static void wither(final MobHead head) {
    // }

    private static void rabbit(final MobHead head) {
        final Predicate<Rabbit> rabbitPredicate = switch (head.name()) {
            case "Toast" -> rabbit -> "Toast".equals(rabbit.getCustomName());
            case "Brown Rabbit" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.BROWN;
            case "White Rabbit" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.WHITE;
            case "Black Rabbit" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.BLACK;
            case "Black and White Rabbit" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.BLACK_AND_WHITE;
            case "Gold Rabbit" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.GOLD;
            case "Salt and Pepper Rabbit" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.SALT_AND_PEPPER;
            case "The Killer Bunny" -> rabbit -> rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid rabbit name");
        };
        head.predicate(rabbitPredicate);
    }

    private static void axolotl(final MobHead head) {
        final Axolotl.Variant variant = switch (head.name()) {
            case "Lucy Axolotl" -> Axolotl.Variant.LUCY;
            case "Wild Axolotl" -> Axolotl.Variant.WILD;
            case "Gold Axolotl" -> Axolotl.Variant.GOLD;
            case "Cyan Axolotl" -> Axolotl.Variant.CYAN;
            case "Blue Axolotl" -> Axolotl.Variant.BLUE;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid axolotl name");
        };
        final Predicate<Axolotl> axolotlPredicate = axolotl -> axolotl.getVariant() == variant;
        head.predicate(axolotlPredicate);
    }

    private static void zombieVillager(final MobHead head) {
        final Villager.@Nullable Profession profession = switch (head.name()) {
            case "Zombie Armorer" -> Villager.Profession.ARMORER;
            case "Zombie Butcher" -> Villager.Profession.BUTCHER;
            case "Zombie Cartographer" -> Villager.Profession.CARTOGRAPHER;
            case "Zombie Cleric" -> Villager.Profession.CLERIC;
            case "Zombie Farmer" -> Villager.Profession.FARMER;
            case "Zombie Fisherman" -> Villager.Profession.FISHERMAN;
            case "Zombie Fletcher" -> Villager.Profession.FLETCHER;
            case "Zombie Leatherworker" -> Villager.Profession.LEATHERWORKER;
            case "Zombie Librarian" -> Villager.Profession.LIBRARIAN;
            case "Zombie Mason" -> Villager.Profession.MASON;
            case "Zombie Nitwit" -> Villager.Profession.NITWIT;
            case "Zombie Villager" -> null;
            case "Zombie Shepherd" -> Villager.Profession.SHEPHERD;
            case "Zombie Toolsmith" -> Villager.Profession.TOOLSMITH;
            case "Zombie Weaponsmith" -> Villager.Profession.WEAPONSMITH;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid zombie villager name");
        };
        final Predicate<ZombieVillager> zombieVillagerPredicate = zombieVillager -> zombieVillager.getVillagerProfession() == profession;
        head.predicate(zombieVillagerPredicate);
    }

    private static void mooshroom(final MobHead head) {
        final MushroomCow.Variant variant = switch (head.name()) {
            case "Red Mooshroom" -> MushroomCow.Variant.RED;
            case "Brown Mooshroom" -> MushroomCow.Variant.BROWN;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid mooshroom cow name");
        };
        final Predicate<MushroomCow> mushroomCowPredicate = mushroomCow -> mushroomCow.getVariant() == variant;
        head.predicate(mushroomCowPredicate);
    }

    private static void villager(final MobHead head) {
        final Villager.@Nullable Profession profession = switch (head.name()) {
            case "Armorer Villager" -> Villager.Profession.ARMORER;
            case "Butcher Villager" -> Villager.Profession.BUTCHER;
            case "Cartographer Villager" -> Villager.Profession.CARTOGRAPHER;
            case "Cleric Villager" -> Villager.Profession.CLERIC;
            case "Farmer Villager" -> Villager.Profession.FARMER;
            case "Fisherman Villager" -> Villager.Profession.FISHERMAN;
            case "Fletcher Villager" -> Villager.Profession.FLETCHER;
            case "Leatherworker Villager" -> Villager.Profession.LEATHERWORKER;
            case "Librarian Villager" -> Villager.Profession.LIBRARIAN;
            case "Mason Villager" -> Villager.Profession.MASON;
            case "Nitwit Villager" -> Villager.Profession.NITWIT;
            case "Villager" -> null;
            case "Shepherd Villager" -> Villager.Profession.SHEPHERD;
            case "Toolsmith Villager" -> Villager.Profession.TOOLSMITH;
            case "Weaponsmith Villager" -> Villager.Profession.WEAPONSMITH;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid villager name");
        };
        final Predicate<Villager> villagerPredicate = villager -> villager.getProfession() == profession;
        head.predicate(villagerPredicate);
    }

    private static void sheep(final MobHead head) {
        if (head.name().equals("jeb_ Sheep")) {
            final Predicate<Sheep> sheepPredicate = sheep -> "jeb_".equals(sheep.getCustomName());
            head.predicate(sheepPredicate);
            return;
        }
        final DyeColor dyeColor = switch (head.name()) {
            case "Green Sheep" -> DyeColor.GREEN;
            case "White Sheep" -> DyeColor.WHITE;
            case "Blue Sheep" -> DyeColor.BLUE;
            case "Orange Sheep" -> DyeColor.ORANGE;
            case "Purple Sheep" -> DyeColor.PURPLE;
            case "Red Sheep" -> DyeColor.RED;
            case "Pink Sheep" -> DyeColor.PINK;
            case "Brown Sheep" -> DyeColor.BROWN;
            case "Light Blue Sheep" -> DyeColor.LIGHT_BLUE;
            case "Lime Sheep" -> DyeColor.LIME;
            case "Yellow Sheep" -> DyeColor.YELLOW;
            case "Black Sheep" -> DyeColor.BLACK;
            case "Cyan Sheep" -> DyeColor.CYAN;
            case "Magenta Sheep" -> DyeColor.MAGENTA;
            case "Light Gray Sheep" -> DyeColor.LIGHT_GRAY;
            case "Gray Sheep" -> DyeColor.GRAY;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid sheep name");
        };
        final Predicate<Sheep> sheepPredicate = sheep -> sheep.getColor() == dyeColor;
        head.predicate(sheepPredicate);
    }

    private static void frog(final MobHead head) {
        final Frog.Variant variant = switch (head.name()) {
            case "Cold Frog" -> Frog.Variant.COLD;
            case "Temperate Frog" -> Frog.Variant.TEMPERATE;
            case "Warm Frog" -> Frog.Variant.WARM;
            default -> throw new IllegalArgumentException(head.name() + " isn't a valid frog name");
        };
        final Predicate<Frog> frogPredicate = frog -> frog.getVariant() == variant;
        head.predicate(frogPredicate);
    }

    private static void wolf(final MobHead head) {
        final Predicate<Wolf> wolfPredicate = wolf -> {
            if (head.name().equals("Angry Wolf")) {
                return wolf.isAngry();
            } else {
                return true;
            }
        };
        head.predicate(wolfPredicate);
    }

}
