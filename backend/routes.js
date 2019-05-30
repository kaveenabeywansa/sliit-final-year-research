const express = require('express');
var Routes = express.Router();

const UserRoutes = require('./route/user-route');

Routes.use('/users/', UserRoutes);
Routes.use('/', function(req, res) {
    res.sendFile(__dirname+'/welcome.html');
});

module.exports = Routes;