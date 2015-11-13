//import the mongodb native drivers.
var mongodb = require('mongodb');
//We need to work with "MongoClient" interface in order to connect to a mongodb server.
var MongoClient = mongodb.MongoClient;
// Connection URL. This is where your mongodb server is running.
var url_mongo = 'mongodb://172.16.100.139:27017/test' ;
// Use connect method to connect to the Server
//read data from DB

var url = require( "url" );
var queryString = require( "querystring" );
var http = require('http');


http.createServer(function(req, res)
{
    if (req.method == 'POST') 
    {
        var jsonString = '';
        req.on('data', function (data) 
        {
            jsonString += data;
        });
        req.on('end', function ()
        {
          console.log(jsonString);
          var user_data = JSON.parse(jsonString);
          if(Object.keys(user_data).length)//Push New Ambulance
          {
              console.log(user_data);
              // var reg_id = amb_data.reg_id;
              // var reg_name = amb_data.reg_name;
              // var reg_email = amb_data.reg_email;
              // var reg_contact = amb_data.reg_contact;
              // var reg_veh_no = amb_data.reg_veh_no;
              // var lati = amb_data.lati;
              // var longi = amb_data.longi;

              // MongoClient.connect(url_mongo, function (err, db)
              // {
              //   if (err) 
              //   {
              //     console.log('Unable to connect to the mongoDB server. Error:', err);
              //   }
              //   else 
              //   {   
              //       var coll_ambulances = db.collection('ambulances');
              //       coll_ambulances.insert(json_object);
              //       console.log('Connection established to', url);
              //   }
              // });
              res.write("Suitable Hospital is Abc");
              res.end();
          }
          else
          {
            console.log('Invalid user data received');
          }  
        });
    }
}).listen(3002);