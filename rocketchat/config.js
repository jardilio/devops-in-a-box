Meteor.startup(() => {
    Accounts.onLogin((data) => {
        var user = data && data.user;
        if (!user) return;
        //TODO: need to get memberOf in LDAP data to map to rocketchat roles
        // should be user.customFields.roles, but its not pulling back
        console.log(user);
        switch(user.name) {
            case 'devops-admin':
                console.log('set role admin for devops-admin');
                RocketChat.authz.addUserRoles(user._id, 'admin');
                RocketChat.authz.removeUserFromRoles(user._id, ['user', 'bot']);
                break;
            case 'devops-system':
                console.log('set role bot for devops-system');
                RocketChat.authz.addUserRoles(user._id, 'bot');
                RocketChat.authz.removeUserFromRoles(user._id, ['user', 'admin']);
                break;
            default:
                console.log('set role user for', user.name);
                RocketChat.authz.addUserRoles(user._id, 'user');
                RocketChat.authz.removeUserFromRoles(user._id, ['bot', 'admin']);
                break;
        }
    });
});