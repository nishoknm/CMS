var express = require('express');
var mongoClient = require('mongodb').MongoClient;
var router = express.Router();
var dbo;

//Mongodb connection using mongodb client : the mangodb service has to be started on the server
mongoClient.connect("mongodb://127.0.0.1:27017/cms", function (err, db) {
    if (err) {
        return console.dir(err);
    }
    dbo = db;
});

/* POST users listing. */
router.post('/getUser', function (req, res, next) {
    var usersCollection = dbo.collection('users');
    usersCollection.findOne({
        username: req.body.username,
        password:req.body.password
    }, function(err, item) {
        res.send(item);
    });
});

/* POST users listing. */
router.post("/addUser", function (req, res) {
    var usersCollection = dbo.collection('users');
    usersCollection.insert(req.body, {
        w: 1
    }, function (err, result) {
        res.send(req.body);
    });
});

module.exports = router;
