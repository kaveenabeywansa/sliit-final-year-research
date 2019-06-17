const mongoose = require('../dbschema/dbconfig');
const UserSchema = mongoose.model('User');
const StatsSchema = mongoose.model('RealTimeStats');

var Controller = function () {
    // adding new new to the system
    this.addUser = function (data) {
        return new Promise(async function (resolve, reject) {
            // defines the new user
            var User = UserSchema({
                name: data.name,
                username: data.username,
                password: data.password,
                phone: data.phone,
                usertype: data.usertype,
                associates: data.associates,
            });

            var Stat = StatsSchema({
                username: data.username,
                statistics: {
                    date: null,
                    time: null,
                    bpm: null,
                    location: {
                        latitude: null,
                        longitude: null
                    }
                }
            });

            // check if the selected username already exist
            var value = await UserSchema.findOne({ username: data.username });
            if (value) {
                // username already exists... cancel user creation
                reject({ status: 409, message: "Username already exists !" });
                return;
            }

            // continue user creation if no issues
            User.save().then(function () {
                Stat.save();
                resolve({ status: 200, message: "Successfully Added !" });
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        });
    };
    // get a list of all the registered users
    this.getUsers = function () {
        return new Promise(function (resolve, reject) {
            UserSchema.find().exec().then(function (value) {
                resolve({ status: 200, userdata: value });
            }).catch(function (reason) {
                reject({ status: 404, message: "Not Found: " + reason });
            })
        })
    };
    // get a user from the username
    this.getOneUser = function (id) {
        return new Promise(function (resolve, reject) {
            UserSchema.findOne({ username: id }).exec().then(function (value) {
                resolve({ status: 200, user: value });
            }).catch(function (reason) {
                reject({ status: 404, message: "ID not found: " + reason });
            })
        })
    };
    // edit a user from the username
    this.editProfile = function (id, data) {
        return new Promise(function (resolve, reject) {
            UserSchema.findOne({ username: id }).exec().then(function (value) {
                value.name = data.name;
                value.phone = data.phone;
                value.save();
                resolve({ status: 200, message: "Profile Updated Successfully !" });
            }).catch(function (reason) {
                reject({ status: 401, message: "User not found ! " });
            })
        })
    };
    // delete a user from the username
    this.deleteUser = function (id) {
        return new Promise(function (resolve, reject) {
            UserSchema.deleteOne({ username: id }).then(function (data) {
                if (data.deletedCount > 0) {
                    resolve({ status: 200, message: "Deleted !" });
                } else {
                    reject({ status: 404, message: "No record found to delete" })
                }
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        })
    };
    // validates the log in request and sends the response instead of a password
    this.login = function (data) {
        return new Promise(function (resolve, reject) {
            UserSchema.findOne({ username: data.username }).exec().then(function (value) {
                if (value.password == data.password) {
                    var logObj = { status: true, userType: value.usertype, userName: value.name };
                    resolve({ status: 200, message: "Welcome " + value.name, logged: logObj });
                } else {
                    reject({ status: 401, message: "Incorrect Password !", logged: { status: false } });
                }
            }).catch(function (reason) {
                reject({ status: 401, message: "User not found ! ", logged: { status: false } });
            })
        })
    };
    // user password change
    this.changePassword = function (id, data) {
        return new Promise(function (resolve, reject) {
            UserSchema.findOne({ username: id }).exec().then(function (value) {
                value.password = data.password;
                value.save();
                resolve({ status: 200, message: "Password Successfully Changed !" });
            }).catch(function (reason) {
                reject({ status: 401, message: "User not found ! " });
            })
        })
    };
    // checks if username already exist while creating user from app
    this.checkUsername = function (id) {
        return new Promise(function (resolve, reject) {
            UserSchema.findOne({ username: id }).exec().then(function (value) {
                if (value) {
                    reject({ status: 409, message: "Username taken !" });
                } else {
                    resolve({ status: 200, message: "Username available !" });
                }
            }).catch(function (reason) {
                reject({ status: 404, message: "ID not found: " + reason });
            })
        })
    };
    // gets a list of blind users connected with the given associate
    this.getBlindListForAssociate = function (id) {
        return new Promise(function (resolve, reject) {
            UserSchema.find({ associates: { $all: [id] } }).exec().then(function (value) {
                if (value.length > 0) {
                    resolve({ status: 200, userdata: value });
                } else {
                    reject({ status: 404, message: "No users found" });
                }
            }).catch(function (reason) {
                reject({ status: 404, message: "Error: " + reason });
            })
        })
    };
};

module.exports = new Controller();
