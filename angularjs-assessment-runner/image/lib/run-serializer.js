const fs = require('fs')

const fileContent = fs.readFileSync('result.json', 'utf8')
const parsedResult = JSON.parse(fileContent)
let exitCode = 0

if (Object.keys(parsedResult).length > 0) {
  for (let title in parsedResult) {
    const tests = parsedResult[title]
    if (tests != null) {
      console.log(title + ':')
      for (let test in tests) {
        if (tests[test] == 'PASSED') {
          console.log('  ¤GREEN¤' + test + ': SUCCESS§GREEN§')
        } else {
          console.log('  ¤RED¤' + test + ': FAILED§RED§')
          exitCode = 1
        }
      }
      console.log('\n')
    }
  }
} else {
  exitCode = 1
}

process.exit(exitCode)
