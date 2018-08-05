const fs = require('fs')

const input = process.argv[2]
const fileContent = fs.readFileSync('result.json', 'utf8')
const parsedResult = JSON.parse(fileContent)
let exitCode = 1

for (let title in parsedResult) {
  const tests = parsedResult[title]
  if (tests != null && tests[input] == 'PASSED') {
    exitCode = 0
    break
  }
}

process.exit(exitCode)
