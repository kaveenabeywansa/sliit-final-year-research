const express = require('express');
var Routes = express.Router();

Routes.use('/', function(req, res) {
    res.sendFile(__dirname+'/welcome.html');
});

module.exports = Routes;