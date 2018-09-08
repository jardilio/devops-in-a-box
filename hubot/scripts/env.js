// Commands:
//   hubot env set <name> <value> - sets a local override for environmental value
//   hubot env unset <name> - unsets a local override for environmental value
//   hubot env list - lists out all availabile environment variables
//   hubot env overrides - lists out all override environment variables

var overrideFile = '/home/hubot/scripts/config/overrides.json',
    fs = require('fs'),
    path = require('path');

if (!fs.existsSync(path.dirname(overrideFile))) {
    fs.mkdirSync(path.dirname(overrideFile));
}

if (!fs.existsSync(overrideFile)) {
    fs.writeFileSync(overrideFile, '{}');
}

var overrides = require(overrideFile);

Object.keys(overrides).forEach(function (key) {
    console.log('overriding hubot env value for ' + key);
    process.env[key] = overrides[key];
});

function isAllowed(msg) {
    //TODO: can we get the user role?
    if (msg.envelope.user.name === 'devops.admin') {
        return true;
    }
    else {
        msg.reply("Only admins may use the env command to change configurations");
    }
    return false;
}

function saveOverrides(msg, newValue) {
    if (isAllowed(msg)) {
        overrides = newValue;
        Object.keys(overrides).forEach(function (key) {
            process.env[key] = overrides[key];
        });
        fs.writeFileSync(overrideFile, JSON.stringify(overrides));
        msg.reply(`Environment has been updated, run 'reload' to take effect:\n${JSON.stringify(overrides, null, 2)}`);
    }
}

module.exports = function(robot) {

    robot.respond(/env set (.*) (.*)/, function (msg) {
        var env = msg.match[1],
            val = msg.match[2],
            newValue = Object.assign({}, overrides);

        newValue[env] = val;

        saveOverrides(msg, newValue);
    });

    robot.respond(/env unset (.*)/, function (msg) {
        var env = msg.match[1],
            newValue = Object.assign({}, overrides);

        delete newValue[env];

        saveOverrides(msg, newValue);
    });

    robot.respond(/env list/, function (msg) {
        if (isAllowed(msg)) {
            msg.reply(JSON.stringify(process.env, null, 2));
        }
    });

    robot.respond(/env overrides/, function (msg) {
        if (isAllowed(msg)) {
            msg.reply(JSON.stringify(overrides, null, 2));
        }
    });
};