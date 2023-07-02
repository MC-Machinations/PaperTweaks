from glob import iglob
from json import load, dumps
from os.path import abspath
from sys import argv, exit
from uuid import UUID

from nbtlib import parse_nbt


def array_to_uuid(arr):
    arr = [i.to_bytes(4, byteorder='big', signed=True) for i in arr]
    raw_uuid = b''.join(arr)
    return UUID(bytes=raw_uuid)


def run():
    if len(argv) < 2:
        print("Need to specify the root data directory for the treasure gems datapack")
        exit(1)
    print(f"Root directory for loot tables: {abspath(argv[1])}")

    heads = {}
    root = abspath(argv[1]) + "/treasure_gems/loot_tables/gem"
    files = sorted(iglob(root + "/*.json"))
    for filename in files:
        with open(filename, "r") as file:
            json = load(file)
            gem_name = filename.split("/")[-1].split(".")[0]
            loot_table_name = "gem/" + gem_name
            if len(json["pools"]) != 1 or json["pools"][0]["rolls"] != 1 or len(json["pools"][0]["entries"]) != 1:
                print(f"Error reading {loot_table_name} loot table")
                exit(1)
            entry = json["pools"][0]["entries"][0]
            name = None
            uuid = None
            texture = None

            for func in entry["functions"]:
                if "set_name" in func["function"]:
                    name = dumps(func["name"], ensure_ascii=False, indent=None)
                elif "set_nbt" in func["function"]:
                    tag = parse_nbt(func["tag"])
                    uuid = str(array_to_uuid(tag["SkullOwner"]["Id"]))
                    texture = str(tag["SkullOwner"]["Properties"]["textures"][0]["Value"])
                else:
                    print(f"Unrecognized loot function {func['function']} in {loot_table_name}")
                    exit(1)

            if name is None or uuid is None or texture is None:
                print(f"{loot_table_name} did not meet the check for a treasure gem loot table")
            heads[gem_name] = {
                "name": name,
                "uuid": uuid,
                "texture": texture
            }

    tables = []
    root = abspath(argv[1]) + "/minecraft/loot_tables/chests"
    files = sorted(iglob(root + "/**/*.json", recursive=True))
    for filename in files:
        with open(filename, "r") as file:
            json = load(file)
            loot_table_name = filename.split("loot_tables/")[-1].split(".")[0]
            mc_name = "minecraft:" + loot_table_name
            if "entity" not in json["type"] or len(json["pools"]) != 2:
                print(f"{mc_name} is of an unknown specification")
                exit(1)
            for pool in json["pools"]:
                if pool["rolls"] != 1 or len(pool["entries"]) != 1:
                    print(f"{mc_name} is of an unknown specification")
                    exit(1)
                entry = pool["entries"][0]
                if "loot_table" not in entry["type"] \
                        or (entry["name"] != "vanilla:" + loot_table_name and entry["name"] != "treasure_gems:random"):
                    print(f"{mc_name} is of an unknown specification")
                    exit(1)
            tables.append(mc_name)

    with open("../src/main/resources/data/treasure_gems/heads.json", "w") as out:
        out.write(dumps(heads, ensure_ascii=False, indent=2) + "\n")

    with open("../src/main/resources/data/treasure_gems/loot_tables.json", "w") as out:
        out.write(dumps(tables, ensure_ascii=True, indent=2) + "\n")


if __name__ == "__main__":
    run()
