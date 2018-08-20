const config = require('./config');
const validator = require('email-validator');
const generator = require('generate-password');
const LDAP = require('ldap-client');

module.exports = (email, fname = '', lname = email) => new Promise(async (resolve, reject) => {
    if (!validator.validate(email)) {
        return reject({code: 406, message: 'You must provide a valid email address'});
    }
    else if (config.restrictEmailDomain && !email.endsWith(config.restrictEmailDomain)) {
        return reject({code: 406, message: `${email} is not an acceptable email address`});
    }
    else {
        try {
            let ldap = await connect();
            let user = await addUser(ldap, email, fname, lname);
            await addRole(ldap, user);
            resolve(user);
        }
        catch(err) {
            reject(err);
        }
    }
});

function connect() {
    return new Promise((resolve, reject) => {
        const ldap = new LDAP({uri: config.ldapUri, base: config.ldapBaseDN}, (err) => {
            if (err) {
                console.error(err);
                reject({code: 500, message: `Sorry, but we were unable to connect to LDAP.`});
            }
            else {
                ldap.simplebind({binddn: `cn=${config.ldapUser},${config.ldapBaseDN}`, password: config.ldapPassword}, (err) => {
                    if (err) {
                        console.error(err);
                        reject({code: 500, message: `Sorry, but we were unable to bind to LDAP.`});
                    }
                    else {
                        resolve(ldap);
                    }
                });
            }
        });
    });
}

function addUser(ldap, email, fname, lname) {
    return new Promise((resolve, reject) => {

        let password = generator.generate({length: 12, numbers: true, symbols: true, uppercase: true, strict: true}),
            dn = `cn=${email},ou=users,${config.ldapBaseDN}`,
            attrs = [
                { attr: 'cn', vals: [ email ] },
                { attr: 'uid', vals: [ email ] },
                { attr: 'objectClass', vals: [ 'inetOrgPerson' ] },
                { attr: 'sn', vals: [ lname ] },
                { attr: 'displayName', vals: [ `${fname} ${lname}`.trim() ] },
                { attr: 'mail', vals: [ email ] },
                { attr: 'givenName', vals: [ fname ] },
                //TODO: MD5 or SHA userpassword value, but leave password clear for email
                { attr: 'userpassword', vals: [ password ] }
            ];

        ldap.add(dn, attrs, (err) => {
            if (err) {
                console.log(Object.keys(err));
                console.error(err);
                if (err.toString().endsWith('Already exists')) {
                    reject({code: 500, message: `An account for ${email} already exists.`});
                }
                else {
                    reject({code: 500, message: `Sorry, but we were unable to create an account for ${email}.`});
                }
            }
            else {
                let user = {dn, password};
                attrs.forEach(attr => user[attr.attr] = attr.vals.join());
                resolve(user);
            }
        });

    });
}

function addRole(ldap, user) {
    return new Promise((resolve, reject) => {
        changes = [{
            op: 'add',
            attr: 'uniqueMember',
            vals: [ user.dn ]
        }];
        ldap.modify(`cn=devops-user,ou=roles,${config.ldapBaseDN}`, changes, (err) => {
            if (err) {
                console.err(err);
                reject({code: 500, message: `Sorry, but we were unable to add user ${email} to the devops-user role.`});
            }
            else {
                resolve(user);
            }
        });
    });
}