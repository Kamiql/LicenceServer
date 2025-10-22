db = db.getSiblingDB('app');
db.createUser({
    user: 'appUser',
    pwd: 'DgHfgSIscze0IdJa',
    roles: [{ roles: ['dbOwner'], db: 'app' }]
});
