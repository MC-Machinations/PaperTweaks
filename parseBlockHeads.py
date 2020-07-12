import re
import subprocess


def getUUID(array):
    out = subprocess.Popen(['node',
                            './parseUUID.js',
                            ] + array,
                           stdout=subprocess.PIPE,
                           stderr=subprocess.STDOUT)
    stdout, stderr = out.communicate()
    return str(stdout).split('\\')[0].split("'")[1]


with open("./blockHeads.txt", "r") as file:
    lines = file.readlines()

for line in lines:
    line = line.replace('Â', '')
    name = re.search(r'(?<=§r§e)(\w+\s?)+', line)
    name = name.group(0)
    uuid = re.search(r'(?<=I;)(-?\d+,?){4}', line)
    uuid = uuid.group(0).split(',')
    uuid = getUUID(uuid)
    texture = re.search(r'(?<=Value:")\w+=*', line)
    texture = texture.group(0)
    ingredient = re.search(r'(?<=buyB:{id:"minecraft:)\w*', line)
    ingredient = "Material." + ingredient.group(0).upper()
    print(
        f"public static BlockHead {name.replace(' ', '_')} = create(\"{name}\", \"{uuid}\", \"{texture}\", {ingredient});")
