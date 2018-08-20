module.exports = {
    smtpHost: process.env.SMTP_HOST || 'smtp',
    smtpPort: process.env.SMTP_PORT || 25,
    ldapUri: process.env.LDAP_URI || 'ldap://openldap:389',
    ldapBaseDN: process.env.LDAP_BASE_DN || 'dc=devops',
    ldapUser: process.env.LDAP_USER || 'admin',
    ldapPassword: process.env.LDAP_PASSWORD || null,
    restrictEmailDomain: process.env.RESTRICT_EMAIL_DOMAIN || null
};