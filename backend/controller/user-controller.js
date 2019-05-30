const mongoose = require('../dbschema/dbconfig');
const UserSchema = mongoose.model('User');

var Controller = function () {
    // adding new new to the system
    this.addUser = function (data) {
        return new Promise(function (resolve, reject) {
            var User = UserSchema({
                name: data.name,
                username: data.username,
                password: data.password,
                phone: data.phone,
                usertype: data.usertype,
                associates: data.associates
            });
            User.save().then(function () {
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
            UserSchema.deleteOne({ username: id }).then(function () {
                resolve({ status: 200, message: "Deleted" });
            }).catch(function (reason) {
                reject({ status: 404, message: "ID not found: " + reason });
            })
        })
    };
    // validates the log in request and sends the response instead of a password
    this.login = function (data) {
        return new Promise(function (resolve, reject) {
            UserSchema.findOne({ username: data.username }).exec().then(function (value) {
                if (value.password == data.password) {
                    resolve({ status: 200, message: "Welcome " + value.name, logged: true });
                } else {
                    reject({ status: 401, message: "Incorrect Password !", logged: false });
                }
            }).catch(function (reason) {
                reject({ status: 401, message: "User not found ! ", logged: false });
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
};

module.exports = new Controller();
