const express = require('express');
const router = express.Router();
const Controller = require('../controller/user-monitor-controller');

// stats from the visually impaired
router.post('/', function (req, res) {
    Controller.addNewStats(req.body).then(function (data) {
        res.status(data.status).send({ message: data.message });
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get all stats
router.get('/', function (req, res) {
    Controller.getAllStatistics().then(function (data) {
        res.status(data.status).send(data.userdata);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get all stats for username
router.get('/:id', function (req, res) {
    Controller.getUserStats(req.params.id).then(function (data) {
        res.status(data.status).send(data.user);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});
// get latest stats for username
router.get('/latest/:id', function (req, res) {
    Controller.getLatestUserStats(req.params.id).then(function (data) {
        res.status(data.status).send(data.user);
    }).catch(function (reason) {
        res.status(reason.status).send({ message: reason.message });
    })
});

module.exports = router;
