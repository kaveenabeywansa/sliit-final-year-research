const mongoose = require('../dbschema/dbconfig');
// const UserSchema = mongoose.model('User');
const StatsSchema = mongoose.model('RealTimeStats');

var Controller = function () {
    // adding new new to the system
    this.addNewStats = function (data) {
        return new Promise(async function (resolve, reject) {
            // defines the new user
            StatsSchema.findOne({ username: data.username }).exec().then(function (value) {
                if (value) {
                    // console.log(value.statistics);
                    value.statistics.push({
                        date: data.date,
                        time: data.time,
                        bpm: data.bpm,
                        stress: data.stress,
                        location: {
                            latitude: data.latitude,
                            longitude: data.longitude
                        }
                    });
                    value.save().then(function () {
                        resolve({ status: 200, message: "Successfully Added !" });
                    }).catch(function (reason) {
                        reject({ status: 404, message: "Error: " + reason });
                    });
                } else {
                    reject({ status: 404, message: "User Not Found !" });
                }
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        });
    };
    // get a list of all the registered users
    this.getAllStatistics = function () {
        return new Promise(function (resolve, reject) {
            StatsSchema.find().exec().then(function (value) {
                resolve({ status: 200, userdata: value });
            }).catch(function (reason) {
                reject({ status: 404, message: "Not Found: " + reason });
            })
        })
    };
    // get all stats for a username
    this.getUserStats = function (id) {
        return new Promise(function (resolve, reject) {
            StatsSchema.findOne({ username: id }).exec().then(function (value) {
                resolve({ status: 200, user: value });
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        })
    };
    // get all stats for a username
    this.getLatestUserStats = function (id) {
        return new Promise(function (resolve, reject) {
            StatsSchema.findOne({ username: id }).exec().then(function (value) {
                resolve({ status: 200, user: value.statistics[value.statistics.length-1] });
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        })
    };
};

module.exports = new Controller();
