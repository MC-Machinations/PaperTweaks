from glob import iglob
from json import load, dumps
from os.path import abspath
from pathlib import Path
from sys import argv, exit
from uuid import UUID

from nbtlib import parse_nbt


def array_to_uuid(arr):
    arr = [i.to_bytes(4, byteorder='big', signed=True) for i in arr]
    raw_uuid = b''.join(arr)
    return UUID(bytes=raw_uuid)


def run():
    if len(argv) < 2:
        print("Need to specify a directory containing treasure gem loot tables")
        exit(1)
        return
    print(f"Root directory for loot tables: {abspath(argv[1])}")

    pools = {}
    for filename in iglob(abspath(argv[1]) + "/**/loot_tables/**/*.json", recursive=True):
        with open(filename, "r") as file:
            json = load(file)
            loot_table_name = filename.split("/")[-1].split(".")[0]
            actual_name = "minecraft:chests/" + ("village/village_toolsmith" if loot_table_name == "village_blacksmith" else loot_table_name)
            if len(json["pools"][-1]["entries"]) == 6:
                current_pool = json["pools"][-1]
                pool = {
                    "rolls": current_pool["rolls"],
                    "entries": []
                }
                for entry in current_pool["entries"]:
                    weight = entry["weight"]
                    pool["entries"].append({
                        "weight": weight,
                        "head": None
                    })
                    if "functions" in entry:
                        tag = parse_nbt(entry["functions"][0]["tag"])
                        pool["entries"][-1]["count"] = entry["functions"][-1]["count"]
                        pool["entries"][-1]["head"] = {
                            "uuid": str(array_to_uuid(tag["SkullOwner"]["Id"])),
                            "texture": str(tag["SkullOwner"]["Properties"]["textures"][0]["Value"]),
                            "name": str(tag["display"]["Name"])
                        }
                pools[actual_name] = pool
            else:
                print(f"{loot_table_name} did not meet the check for a treasure gem loot table")

    heads = {}
    for filename in iglob(abspath(argv[1]) + "/**/functions/**/*.mcfunction", recursive=True):
        with open(filename, "r") as file:
            head_name = filename.split('/')[-1].split('.')[0]
            nbt = parse_nbt(file.readline().split("player_head")[1])
            heads[head_name] = {
                "name": nbt["display"]["Name"],
                "uuid": str(array_to_uuid(nbt["SkullOwner"]["Id"])),
                "texture": nbt["SkullOwner"]["Properties"]["textures"][0]["Value"]
            }

    print(f"Read from {len(pools)} loot tables, and parsed {len(heads)} heads")

    Path("../vanilla-tweaks-bukkit/src/main/resources/data/treasure_gems").mkdir(parents=True, exist_ok=True)
    with open("../vanilla-tweaks-bukkit/src/main/resources/data/treasure_gems/loot_pools.json", "w") as out:
        out.write(dumps(pools, ensure_ascii=False, indent=2))

    with open("../vanilla-tweaks-bukkit/src/main/resources/data/treasure_gems/heads.json", "w") as out:
        out.write(dumps(heads, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    run()
