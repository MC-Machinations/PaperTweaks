from json import dumps
from sys import argv, exit
from uuid import UUID

from nbtlib import parse_nbt
from nbtlib.tag import Compound


def array_to_uuid(arr):
    arr = [i.to_bytes(4, byteorder='big', signed=True) for i in arr]
    raw_uuid = b''.join(arr)
    return UUID(bytes=raw_uuid)


def run():
    if len(argv) < 2:
        print("Need to specify .mcfunction file containing the list of trades (usually add_trade.mcfunction)")
        exit(1)
        return
    with open(argv[1], "r") as file:
        lines = file.readlines()
        heads = []
        for line in lines:
            if line.strip().startswith("#") or len(line.strip()) == 0:
                continue
            nbt: Compound = parse_nbt(line.split(" ", maxsplit=15)[15])
            sell_tag: Compound = nbt["sell"]
            heads.append({
                "maxUses": int(nbt["maxUses"]),
                "secondaryCost": str(nbt["buyB"]["id"]),
                "headCount": int(sell_tag["Count"]),
                "name": str(sell_tag["tag"]["display"]["Name"]),
                "uuid": str(array_to_uuid(sell_tag["tag"]["SkullOwner"]["Id"])),
                "texture": sell_tag["tag"]["SkullOwner"]["Properties"]["textures"][0]["Value"]
            })
        with open("../vanilla-tweaks-bukkit/src/main/resources/data/wandering_trades.json", "w") as out:
            out.write(dumps(heads, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    run()
