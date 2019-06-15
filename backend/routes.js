const express = require('express');
var Routes = express.Router();

const UserRoutes = require('./route/user-route');
const StatsRoutes = require('./route/user-monitor-route');

Routes.use('/users/', UserRoutes);
Routes.use('/stats/', StatsRoutes);
Routes.use('/', function(req, res) {
    res.sendFile(__dirname+'/welcome.html');
});

module.exports = Routes;