const express = require('express');
const app = express();
app.use(express.json());

var user = function() {
	this.name = 'john';
};

function handler(input) {
	console.log(input);
	user['anyVal'] = user[input[0]](input[1]);
	console.log(user);
}

app.post("/api/user", (req, res) => res.send(handler(req.body)));

app.listen(3000, '127.0.0.1');
