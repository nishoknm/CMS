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
router.post('/getDepartments', function (req, res) {
    var deptCollection = dbo.collection('departments');
    deptCollection.find().toArray(function (err, docs) {
        if (!err) {
            res.send(docs);
        } else {
            console.dir(err);
        }
    });
});

/* POST users listing. */
router.post('/getDepartment', function (req, res) {
    var deptCollection = dbo.collection('departments');
    deptCollection.findOne({name: req.body.department}, function (err, item) {
        res.send(item);
    });
});

/* POST users listing. */
router.post('/getAccounts', function (req, res) {
    var deptCollection = dbo.collection('accounts');
    deptCollection.find().toArray(function (err, docs) {
        if (!err) {
            res.send(docs);
        } else {
            console.dir(err);
        }
    });
});

/* POST users listing. */
router.post('/getUser', function (req, res, next) {
    var usersCollection = dbo.collection('users');
    usersCollection.findOne({
        email: req.body.email,
        password: req.body.password,
        account: req.body.account
    }, function (err, item) {
        res.send(item);
    });
});

/* POST users listing. */
router.post('/getUserWithEmail', function (req, res, next) {
    var usersCollection = dbo.collection('users');
    usersCollection.findOne({
        email: req.body.email
    }, function (err, item) {
        res.send(item);
    });
});

/* POST users listing. */
router.post('/addCourse', function (req, res, next) {
    var usersCollection = dbo.collection('users');
    usersCollection.update({
        email: req.body.email
    }, {$addToSet: {courses: {name: req.body.course, department: req.body.department}}});
    res.send("Success");
});

/* POST users listing. */
router.post('/updateDepartmentProf', function (req, res, next) {
    var deptsCollection = dbo.collection('departments');
    var profIndex = req.body.courseindex;
    var course = {};
    course[profIndex] = req.body.email;
    course = flattenObject(course);
    deptsCollection.update({
        name: req.body.department
    }, {$set: course});
    res.send("Success");
});

/* POST users listing. */
router.post('/deleteDepartmentProf', function (req, res, next) {
    var deptsCollection = dbo.collection('departments');
    var profIndex = req.body.courseindex;
    var course = {};
    course[profIndex] = req.body.email;
    course = flattenObject(course);
    console.log(course);
    deptsCollection.update({
        name: req.body.department
    }, {$set: course});
    res.send("Success");
});

/* POST users listing. */
router.post('/deleteCourse', function (req, res, next) {
    var usersCollection = dbo.collection('users');
    usersCollection.update({
        email: req.body.email
    }, {$pull: {courses: {name: req.body.course}}});
    res.send("Success");
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

var flattenObject = function (ob) {
    var toReturn = {};
    var flatObject;
    for (var i in ob) {
        if (!ob.hasOwnProperty(i)) {
            continue;
        }
        if ((typeof ob[i]) === 'object') {
            flatObject = flattenObject(ob[i]);
            for (var x in flatObject) {
                if (!flatObject.hasOwnProperty(x)) {
                    continue;
                }
                toReturn[i + (!!isNaN(x) ? '.' + x : '')] = flatObject[x];
            }
        } else {
            toReturn[i] = ob[i];
        }
    }
    return toReturn;
};

module.exports = router;
