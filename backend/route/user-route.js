const express = require('express');
const router = express.Router();
const Controller = require('../controller/user-controller');

// create new user
router.post('/', function (req, res) {
    Controller.addUser(req.body).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get all users
router.get('/', function (req, res) {
    Controller.getUsers().then(function (data) {
        res.status(data.status).send(data.userdata);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get a user from username
router.get('/:id', function (req, res) {
    Controller.getOneUser(req.params.id).then(function (data) {
        res.status(data.status).send(data.user);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// update user from username
router.put('/:id', function (req, res) {
    Controller.editProfile(req.params.id, req.body).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// delete user from username
router.delete('/:id', function (req, res) {
    Controller.deleteUser(req.params.id).then(function (data) {
        res.status(data.status).send({ data: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// user log in attempt
router.post('/login', function (req, res) {
    Controller.login(req.body).then(function (data) {
        res.status(data.status).send({ message: data.message, logged: data.logged });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message, logged: reason.logged });
    })
});
// user password change
router.post('/pwd/:id', function (req, res) {
    Controller.changePassword(req.params.id, req.body).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get a user from username
router.get('/checkuname/:id', function (req, res) {
    Controller.checkUsername(req.params.id).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get a list of blind users for the associate
router.get('/blindlist/:id', function (req, res) {
    Controller.getBlindListForAssociate(req.params.id).then(function (data) {
        res.status(data.status).send(data.userdata);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get a list of all from the selected user type
router.get('/usertype/:id', function (req, res) {
    Controller.getAllOfUserType(req.params.id).then(function (data) {
        res.status(data.status).send(data.userdata);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get associate list for given keyword
router.get('/searchassociate/:id', function (req, res) {
    Controller.getAssociatesForKeyword(req.params.id).then(function (data) {
        res.status(data.status).send(data.userdata);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});

module.exports = router;
