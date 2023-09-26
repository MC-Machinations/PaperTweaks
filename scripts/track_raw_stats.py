from sys import argv, exit


def run():
    if len(argv) < 2:
        print("Need to specify .mcfunction file containing all the objectives")
        exit(1)
    with open(argv[1], "r") as file:
        lines = file.readlines()
        for line in lines:
            if line.strip().startswith("#") or len(line.strip()) == 0:
                continue
            words = [word.strip(" \n\"") for word in line.split(" ", maxsplit=5)[3:]]
            # special cases
            if words[1] == "deathCount":
                words[1] = "minecraft.custom:minecraft.deaths"
            if words[1] == "playerKillCount":
                words[1] = "minecraft.custom:minecraft.player_kills"

            if ":" in words[1]:
                print(f"new Tracked.StatisticType(\"{words[0]}\", \"{words[1]}\", \"{words[2]}\"),")
            else:
                print(f"new Tracked.CriteriaType(\"{words[0]}\", \"{words[1]}\", \"{words[2]}\", ),")


if __name__ == "__main__":
    run()
