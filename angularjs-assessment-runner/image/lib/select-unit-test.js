const fs = require('fs')
const { execFile } = require('child_process');

const fileContent = fs.readFileSync("test.js", 'utf8');
let newContent = fileContent;

const input = process.argv[2];
const startIdx = fileContent.indexOf(input);
if (startIdx >= 0) {
    let lineStartIdx = -1;
    for(let i = startIdx; i >= 0 && lineStartIdx < 0; i--) {
        if (fileContent.charAt(i) == '\n') {
            lineStartIdx = i + 1;
        }
    }

    if (lineStartIdx < startIdx) {
        const extracted = fileContent.substring(lineStartIdx, startIdx).replace(/it.*\(/, 'it.only(');
        newContent = fileContent.substr(0, lineStartIdx) + extracted + fileContent.substr(startIdx);
    }
}

fs.writeFileSync("test.js", newContent);
