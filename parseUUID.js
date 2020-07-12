const uuidBytes = new Uint8Array(16)
const uuid = new DataView(uuidBytes.buffer)

const groupSizes = [8, 4, 4, 4, 12]

const intArray = process.argv.slice(2)
for (let i = 0; i < intArray.length; i++) {
    uuid.setInt32(i * 4, Number(intArray[i]), false)
}

let hexText =
    uuid.getBigUint64(0, false).toString(16).padStart(16, '0') +
    uuid.getBigUint64(8, false).toString(16).padStart(16, '0')
const groups = []
let groupStart = 0
for (const groupSize of groupSizes) {
    groups.push(hexText.substring(groupStart, groupStart + groupSize))
    groupStart += groupSize
}
hexText = groups.join('-')
console.log(hexText)
