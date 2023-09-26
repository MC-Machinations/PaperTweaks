from glob import iglob
from json import dumps
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
        print("Need to specify a directory containing gem villager mcfunction files")
        exit(1)
    print(f"Root directory for gem villager mcfunction files: {abspath(argv[1])}")

    villagers = {}
    files = sorted(iglob(abspath(argv[1]) + "/*.mcfunction"))
    for filename in files:
        with open(filename, "r") as file:
            villager_name = filename.split("/")[-1].split(".")[0]
            nbt = parse_nbt(file.readline().split(" ", maxsplit=5)[-1])
            head_data_nbt = nbt["ArmorItems"][-1]["tag"]
            villager = {
                "head": {
                    "name": nbt["CustomName"],
                    "uuid": str(array_to_uuid(head_data_nbt["SkullOwner"]["Id"])),
                    "texture": head_data_nbt["SkullOwner"]["Properties"]["textures"][0]["Value"]
                },
                "offers": []
            }
            offers_nbt = nbt["Offers"]["Recipes"]
            for offer in offers_nbt:
                new_offer = {
                    "input": None,
                    "output": None
                }

                if offer["buy"]["id"] == "minecraft:player_head":
                    if "tag" in offer["buy"]:
                        buy = offer["buy"]["tag"]
                        new_offer["input"] = {
                            "uuid": str(array_to_uuid(buy["SkullOwner"]["Id"])),
                            "texture": buy["SkullOwner"]["Properties"]["textures"][0]["Value"],
                            "count": offer["buy"]["Count"],
                            "name": buy["display"]["Name"]
                        }
                    else:
                        new_offer["input"] = {
                            "count": offer["buy"]["Count"]
                        }
                else:
                    new_offer["input"] = {
                        "id": offer["buy"]["id"],
                        "count": offer["buy"]["Count"]
                    }

                if "tag" in offer["sell"]:
                    sell = offer["sell"]["tag"]
                    new_offer["output"] = {
                        "uuid": str(array_to_uuid(sell["SkullOwner"]["Id"])),
                        "texture": sell["SkullOwner"]["Properties"]["textures"][0]["Value"],
                        "count": offer["sell"]["Count"],
                        "name": sell["display"]["Name"]
                    }
                else:
                    new_offer["output"] = {
                        "count": offer["sell"]["Count"]
                    }
                villager["offers"].append(new_offer)

            villagers[villager_name] = villager

    with open("../src/main/resources/data/gem_villagers.json", "w") as out:
        out.write(dumps(villagers, ensure_ascii=False, indent=2) + "\n")


if __name__ == "__main__":
    run()
