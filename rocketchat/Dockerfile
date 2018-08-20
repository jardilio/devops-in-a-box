FROM rocketchat/rocket.chat:0.66.3

USER root

# inject our own scripts to configure the system on initialization
COPY config.js /app/bundle/programs/server/app/config.js
RUN chown rocketchat:rocketchat /app/bundle/programs/server/app/config.js
RUN cat /app/bundle/programs/server/app/config.js >> /app/bundle/programs/server/app/app.js

USER rocketchat

ENV MONGO_URL=mongodb://rocketchat-database:27017/rocketchat

#ENV INITIAL_USER='{"email":"devops-admin@local", "name": "admin user", "password": "foobar", "username": "devops-admin", "_id": "devops-admin", "customFields": {"roles": ""}}'

# Properties names found in tooltips of fields on http://localhost/rocketchat/admin/
# Use prefix "OVERWRITE_SETTING_" prefix per https://github.com/RocketChat/Rocket.Chat.Docs/issues/261

#ENV OVERWRITE_SETTING_LDAP_BaseDN=${LDAP_BASE_DN}
#ENV OVERWRITE_SETTING_LDAP_User_Search_Filter=(memberOf=cn=devops-user,ou=roles,${LDAP_BASE_DN})
#ENV OVERWRITE_SETTING_LDAP_Authentication_Password=$DOS_LDAP_SYSTEM_PASSWORD
#ENV OVERWRITE_SETTING_LDAP_Authentication_UserDN=cn=readonly,${LDAP_BASE_DN}
ENV OVERWRITE_SETTING_LDAP_Enable=true
ENV OVERWRITE_SETTING_LDAP_Login_Fallback=true
ENV OVERWRITE_SETTING_LDAP_Host=openldap
ENV OVERWRITE_SETTING_LDAP_Authentication=true
ENV OVERWRITE_SETTING_LDAP_Username_Field=cn
ENV OVERWRITE_SETTING_LDAP_Merge_Existing_Users=true
ENV OVERWRITE_SETTING_LDAP_Sync_User_Data=true
ENV OVERWRITE_SETTING_LDAP_Sync_User_Data_FieldMap='{"cn":"name", "mail":"email", "memberOf":"customFields.roles"}'
ENV OVERWRITE_SETTING_LDAP_Sync_User_Avatar=false
ENV OVERWRITE_SETTING_LDAP_User_Search_Field=uid
ENV OVERWRITE_SETTING_LDAP_User_Search_Scope=''
ENV OVERWRITE_SETTING_LDAP_Unique_Identifier_Field=objectGUID,ibm-entryUUID,GUID,dominoUNID,nsuniqueId,uidNumber

ENV OVERWRITE_SETTING_Accounts_RegistrationForm=Disabled
ENV OVERWRITE_SETTING_Accounts_AllowAnonymousRead=false
ENV OVERWRITE_SETTING_Accounts_AllowAnonymousWrite=false
ENV OVERWRITE_SETTING_Accounts_CustomFieldsToShowInUserInfo=ldapRoles
ENV OVERWRITE_SETTING_Accounts_CustomFields='{"roles":{"type":"text"}}'
ENV OVERWRITE_SETTING_Accounts_AllowUserProfileChange=false
ENV OVERWRITE_SETTING_Accounts_AllowUsernameChange=false
ENV OVERWRITE_SETTING_Accounts_AllowEmailChange=false
ENV OVERWRITE_SETTING_Accounts_AllowPasswordChange=false

ENV OVERWRITE_SETTING_Show_Setup_Wizard=completed

# TODO: activate external hubot server
ENV OVERWRITE_SETTING_InternalHubot_Enabled=false
#ENV OVERWRITE_SETTING_InternalHubot_Username=devops-system
#ENV OVERWRITE_SETTING_InternalHubot_ScriptsToLoad=hubot-jenkins
#ENV OVERWRITE_SETTING_InternalHubot_PathToLoadCustomScripts=/hubot_scripts/