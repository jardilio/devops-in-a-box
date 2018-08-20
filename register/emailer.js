const config = require('./config');
const sendmail = require('sendmail')({
    devHost: config.smtpHost,
    devPort: config.smtpPort,
    smtpHost: config.smtpHost,
    smtpPort: config.smtpPort
});
const escape = require('escape-html');

module.exports = (user, host) => new Promise((resolve, reject) => {
    console.log(JSON.stringify(user, null, 2));
    sendmail({
        from: `no-reply@${host.hostname}`,
        to: user.mail,
        subject: 'New User Request',
        html: `<p>A new user account was generated for ${user.mail} at <a href="${host.href}">${host.href}</a>.</p>
        <div>Domain Name: ${user.dn}</div>
        <div>Username: ${user.cn}</div>
        <div>Password: ${escape(user.password)}</div>
        <p>You can go to <a href="${host.href}openldap">${host.href}openldap</a> 
        to change your password using your Domain Name. For all other services, you may use 
        your standard username.</p>`
    }, (err) => {
        if (err) {
            console.error(err);
            reject({code: 500, message: `Sorry, but we were unable to send email to ${user.mail} at this time.`});
        }
        else {
            resolve();
        }
    });
});