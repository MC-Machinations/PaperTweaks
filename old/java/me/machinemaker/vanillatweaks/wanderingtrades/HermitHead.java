package me.machinemaker.vanillatweaks.wanderingtrades;

import com.google.common.collect.Sets;
import me.machinemaker.vanillatweaks.utils.VTUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.Collection;
import java.util.function.Function;

public class HermitHead {

    public static Collection<HermitHead> hermitHeads = Sets.newHashSet();
    public static HermitHead PythonGB = create("PythonGB", "2ce1367d-48f2-4e89-b79a-f85325c85063", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA5M2YwNzc3NThjZjVmZTkyNGYxZWM1YWY5YzZiZGJiNzgwYTVjY2I2ZjEzNmFiMWFmNDc3NDIxZTcyM2Y0ZCJ9fX0=");
    public static HermitHead Xisuma = create("Xisuma", "7f9a1502-c82f-4db0-8249-ac11892910fe", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU5NjQwMTg2YTcwOTM3Nzg4MTAwOTg0OGVjN2ViZGE4MTllYjE4YzRlNTk2N2FlMWQwYWM3MTQ2YWZiNGI0ZSJ9fX0=");
    public static HermitHead Docm77 = create("Docm77", "0b6386ac-4d32-4e96-a792-24fa763ed21d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFmNmNhZGMzZTljYWQ5NWZiZjZjMTFmYTU4ZTgwZWE0ZGI1MTllM2I0MjUwZGU5NjEzMGRjZjhjMWE3NDNlZSJ9fX0=");
    public static HermitHead Jessassin = create("Jessassin", "cc4880d9-81b7-4319-97e6-c44eb5f9f059", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTM0MzdiMGY0NDRlYTQzYmJjNDc3ZWMyMTI3YzlkNjY3NDdlM2U3M2M2YTdkN2UxMjBhZmQxYTA0M2E4YWMxZCJ9fX0=");
    public static HermitHead xBCrafted = create("xBCrafted", "1f3e2672-566e-4611-ab8f-52c8afe9ab7e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y1M2Q4YjQwMGZlZjAyOGRlYzk1MjEzMGQ0MDY1NDRhZGVjMTY0ZDFkYjRmMGQwYjExNDZjNjVhMWRmYzI2MyJ9fX0=");
    public static HermitHead Etho = create("Etho", "6209adf8-4dcf-4411-b69a-7456b1314b57", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY3NzQ3ZWEzNDA5ODliOThhODIyMjk0OGFmNDU0NTRiZWU5MDliOWFkODMwYjJhMDFkY2Y5MTE3YzgzYTMzYSJ9fX0=");
    public static HermitHead Mumbo = create("Mumbo", "217e8fc4-b203-49b9-9623-09b16a01561e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzRhOGQ4NzllZjI5NzMxZTJiNDQ4ZjZlZTYwMDQ4NTViOGZmYWZlZjNiZmQ3OGQ2NTQ2ODY0MjczNmE4MWI0MiJ9fX0=");
    public static HermitHead iJevin = create("iJevin", "db4b6fd5-486f-4b0f-9b4c-2c32cad5a54e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWUwNDE5ZjBjODBkNmFkNTljMWMzMmI0NmM3Nzc1NjJmZGM4M2EwMGUxOGJhNDg0ZTI3M2U3NGYyNDZkZGE4OSJ9fX0=");
    public static HermitHead impulseSV = create("impulseSV", "9e548022-eba1-4fd3-840a-5cf10b678e09", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzcyNTFjODYwNTU4YTU2YzA0ZjYwZTEwNzA1MGI3NzkxMzgwYTgwZDRlYTBhYjZhMjdiOTc5NzljYjhmYmM2ZCJ9fX0=");
    public static HermitHead Renthedog = create("Renthedog", "b075cecd-acfc-4aad-a6fc-1784dd8a0084", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYTY0YTkyNWRjZjUxNTRlNjYzYzU4NGQ0OGNlODBhNDBmZDEyOWVlOTRlNzA0NDZmNzJlOGEwMjRhOWM5In19fQ==");
    public static HermitHead Tinfoilchef = create("Tinfoilchef", "933cc6e5-6208-4d39-8ddc-12ad5007189c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWEzYjcwNzA2ODliOGQ4OGIxNjQ5MDhmZGM2YzAwZDAzNWY3YWY0NjJhNzQxMTE0NGQ3NjkyZDI0NzhmYWZmYiJ9fX0=");
    public static HermitHead Biffa2001 = create("Biffa2001", "28b1d659-18b6-463a-a988-475a526da4ba", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzkwMjRjMTE4ZTc5YjM5NTBmNGIxNmZlN2NkMDk2NGRiNTA2NTg4ZGUxZjRmYzI5NDQwMTQyMmZjYWVlZmFlMCJ9fX0=");
    public static HermitHead Stressmonster101 = create("Stressmonster101", "9cfb2306-5f6f-4e30-903b-7f2a875c3a94", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU1ZDQwYWM5MGJkMTgxZDI0OWE2ZmNhMjhhODIwNWQzOWJhYzQzYjczZTBjZTU2Y2UzMmZlYTRkYjFkODA1NyJ9fX0=");
    public static HermitHead GoodTimesWithScar = create("GoodTimesWithScar", "6545fed0-99ed-466d-b6f3-6f0d8aa7a0a4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODFiNjRiYWU5ODc0Mzc4YWNkM2Y5YjdiNjRjODRkNWZmYmUzYzA2MjcwZDM4NGUyZmViYTQ1NWVlZGIyYTkxNiJ9fX0=");
    public static HermitHead Zedaph = create("Zedaph", "5bd21b35-314a-4fae-80aa-584fa6db9adb", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDMzYjFkYTUwMmQ2MmEzODM2NDU3YmNkMmI2M2UzMjQzNmIyODU4MjljZjM5Y2FhNGQwN2Y3YWYwZmQ5MzJkMCJ9fX0=");
    public static HermitHead joehillssays = create("joehillssays", "9cd808d0-0027-4801-bb3a-a92f701c124f", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTIzNmJkNzgwZjkzNTM3NDI1ZWEwYWY1ZDRhMmE1MzQyZmI4MGRlMDY5MWViMTFkOTkzYjU1NmEzY2EyNDQ2OSJ9fX0=");
    public static HermitHead cubfan135 = create("cubfan135", "03f5b923-d869-414a-9d8b-edce53a24f2a", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTgxNTBlOTVkMjY5OTQ2NjQ2ZTFjN2FiOTJlZDUxOWViYWQ5NTc4MzY5NjhjMDA0N2YzM2ZhN2JhZTAyZTRhYyJ9fX0=");
    public static HermitHead Welsknight = create("Welsknight", "699a78f8-c165-4673-8b0c-cedb3ceb6791", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI1NTg5NGE5Njk2NTUzY2U5N2I1OWJmYTMxZGY4ZTVlOGI4YWVhZDFkZGUzYTZkYWE3NjZhMGNjNGIxZTc1MyJ9fX0=");
    public static HermitHead Keralis = create("Keralis", "20fe9cd0-c349-4c54-a973-22b42ef029b3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjg3ODc2MDFkNGU5ZDFhOGM0MmY5MjdmN2YxZjdiNzVjZWMxYmRkZTM0MmQ0MDVlYzA2MzlkOGY4YTk2MmQyMSJ9fX0=");
    public static HermitHead falsesymmetry = create("falsesymmetry", "bcf5b3a2-af14-4b96-84ec-bd2f44d8c95b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI3MzI3NmFlMzI0ZGFiYzdkMTcxZDVmNDZiZGM3NmI3NDVlODM5YTk1YTFkODhkMDhlNDBlMTY2ZDExMDMwOSJ9fX0=");
    public static HermitHead hypnotizd = create("hypnotizd", "eca8787a-670b-493b-93cf-52e63c2ada86", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODE0OTRkYzljOTI3NTRlYjY0Zjc1N2E4ZTRhMmMxZjJhZGNjYTIxMTVkMTE5NzJkNmQzOWRjOWZkYjE2ZjU2NCJ9fX0=");
    public static HermitHead VintageBeef = create("VintageBeef", "4d39bdae-475e-4bef-935d-8b47d8de11fd", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGVmNWZiMzE4Zjg1NWZiNjNiYTFlZmRjYzEyMDJlZTIzOGNiNDAzYTIxY2Q2YmI0ZjMyNDM3NGYxZTE0ZGRkOSJ9fX0=");
    public static HermitHead BdoubleO100 = create("BdoubleO100", "ed78596f-a590-44fb-8933-562c99970be4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzVlODA1ZDA0YjM3OGVkNzNkNDQ5ZWViOTUzZTBkNTdhMmIyZGZiZjNiNzk1MTY5OTRhMjNhMWRhMDcwNTFkZSJ9fX0=");
    public static HermitHead Tango = create("Tango", "528079e8-ed63-45b9-bc31-7184ebfbc8f1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzE4NjAxMjU5ZDM3NTVkNmFlYTMyMmNiYjMzM2M0ZTM2OWNmM2IyMTY4MTA4ZjUxOWI1NGI1NDAzZjZiZjMzNiJ9fX0=");
    public static HermitHead Grian = create("Grian", "da10973e-e0ed-4b64-88b8-b2d24ddd9803", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI3YWI2ZGQ3MzE4ZTk5NDMyZjI0MzFiZTBlYjZiMGYxYzcwOTQ5YTRiOTJmNDUzYzYwZWE1YjhkMzNiNzEyYyJ9fX0=");
    public static HermitHead iskall85 = create("iskall85", "52ca908b-f437-491a-9359-80eedd0cc6ee", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBiZmE4YTExOGI3ZjdkNzRiZGU4NDdjMWEyNDgzMWZiY2E2ODJhZDNjYzVkNzk4MzljYmVmYjlkZTQ5MDczNiJ9fX0=");
    public static HermitHead ZombieCleo = create("ZombieCleo", "0e90e178-b5aa-4f56-8c7d-bd34e0df94cf", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODlmZWZmODM4YWI1NmU0MDI1ZWVmODdlOTNjNDE4ODc4MTEwZDhjOGQ5MDVkOGZkNGI0OTM4ODljMDFhNWYxNyJ9fX0=");

    private final ItemStack skull;
    private final Function<ItemStack, MerchantRecipe> recipeFunction;

    private HermitHead(ItemStack skull, Function<ItemStack, MerchantRecipe> recipeFunction) {
        this.skull = skull;
        this.recipeFunction = recipeFunction;
    }

    public MerchantRecipe getTrade() {
        return recipeFunction.apply(this.skull.clone());
    }

    private static HermitHead create(String playerName, String uuid, String texture) {
        ItemStack skull = VTUtils.getSkull(ChatColor.RESET.toString() + ChatColor.YELLOW + playerName, uuid, texture, 1);
        HermitHead head = new HermitHead(skull, (sellItem) -> {
            MerchantRecipe recipe = new MerchantRecipe(sellItem, 3);
            recipe.addIngredient(new ItemStack(Material.EMERALD, 1));
            return recipe;
        });
        hermitHeads.add(head);
        return head;
    }
}
