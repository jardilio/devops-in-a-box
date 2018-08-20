const express = require('express');
const app = express();
const register = require('./register');
const emailer = require('./emailer');
const url = require('url');
const bodyParser = require('body-parser');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.use('/register', express.static('public'))

app.post('/register', async (req, res) => {
    let email = (req.body && req.body.email),
        host = url.parse(`${req.protocol}://${req.get('host')}`);

    console.log(`Registration requestion from ${req.socket.remoteAddress} for ${email}`);
    
    try {
        let user = await register(email, req.body.fname, req.body.lname);
        res.write(`User account has been created for ${user.cn}. `);
        await emailer(user, host);
        res.write(`Please check your email for login information.`);
    }
    catch(err) {
        console.error(err);
        res.status(err.code || 500);
        res.write(err.message || err);
    }
    finally {
        res.end();
    }
});

app.listen(3000, () => console.log('Register service ready!'));