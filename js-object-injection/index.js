const express = require('express');
const app = express();
app.use(express.json());

const user = function() {
  this.name = 'john'; // eslint-disable-line no-invalid-this
};

/**
 * Function vulnerable to object injection
 * @param {any_string} input Dangerous value can be provided here
 */
function handler(input) {
  console.log(input);
  const v1 = input[0];
  const v2 = user[v1];
  user['anyVal'] = v2(input[1]);
  console.log(user);
}

app.post('/api/user', (req, res) => res.send(handler(req.body)));

app.listen(3000, '127.0.0.1');
