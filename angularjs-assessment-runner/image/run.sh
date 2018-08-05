#!/bin/bash
cd /project/target
echo "TECHIO> redirect-streams --pattern '^PhantomJS.*ERROR$' null"
echo "TECHIO> redirect-streams --pattern 'JSON file was written to result\.json' null"
echo "TECHIO> open -s /project/target test.html"
npm --silent test
node lib/run-serializer.js