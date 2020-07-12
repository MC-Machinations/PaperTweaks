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


with open("./hermitHeads.txt", "r") as file:
    lines = file.readlines()

for line in lines:
    line = line.replace('Â', '')
    name = re.search(r'(?<=§r§e)\w+', line)
    name = name.group(0)
    uuid = re.search(r'(?<=I;)(-?\d+,?){4}', line)
    uuid = uuid.group(0).split(',')
    uuid = getUUID(uuid)
    texture = re.search(r'(?<=Value:")\w+=*', line)
    texture = texture.group(0)
    print(
        f"public static BlockHead {name} = create(\"{name}\", \"{uuid}\", \"{texture}\");")
