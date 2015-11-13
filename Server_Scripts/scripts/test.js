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
var Geohash = require('latlon-geohash');


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
          var amb_data = JSON.parse(jsonString);
          if(Object.keys(amb_data).length==7)//Push New Ambulance
          {
              console.log(amb_data);
              var reg_id = amb_data.reg_id;
              var reg_name = amb_data.reg_name;
              var reg_email = amb_data.reg_email;
              var reg_contact = amb_data.reg_contact;
              var reg_veh_no = amb_data.reg_veh_no;
              var lati = amb_data.lati;
              var longi = amb_data.longi;

              var json_object = 
              {
                  _id: reg_id,
                  email: reg_email,
                  name: reg_name,
                  contact: reg_contact,
                  vehicle: reg_veh_no,
                  lati: lati,
                  longi: longi,
                  eventId: 0,
                  isServing: false,
                  readytoServe: true,
              };
              MongoClient.connect(url_mongo, function (err, db)
              {
                if (err) 
                {
                  console.log('Unable to connect to the mongoDB server. Error:', err);
                }
                else 
                {   
                    var coll_ambulances = db.collection('ambulances');
                    coll_ambulances.insert(json_object);
                    console.log('Connection established to', url);
                }
              });
              res.write("Successfully Registered");
              res.end();
              var geohash = Geohash.encode(lati,longi,7);
              console.log("up");
              var kibana_data = 
              {
                  regid: reg_id,
                  email: reg_email,
                  name: reg_name,
                  contact: reg_contact,
                  vehicle: reg_veh_no,
                  geohash: geohash, 
                  eventId: 0,
                  isServing: false,
                  readytoServe: true,
              };
              console.log("up1");
              var request = new http.ClientRequest({
                    hostname: "172.16.100.139",
                    port: 9200,
                    path: "/ambulance/ambulance/",
                    method: "POST",
                    headers: 
                    {
                        "Content-Type": "application/json",
                        "Content-Length": Buffer.byteLength(JSON.stringify(kibana_data))
                    }
                })  
              console.log("up2");
                request.end(JSON.stringify(kibana_data));
                console.log("up3");
          }
          else if (Object.keys(amb_data).length==3)//Update location of Ambulance in the database
          {
              console.log(amb_data);
              var reg_id = amb_data.reg_id;
              var lati = amb_data.lati;
              var longi = amb_data.longi;

              var geohash = Geohash.encode(lati,longi,8);
              var kibana_data = 
              {
                "doc":{
                  "geohash": geohash,
                }
              };
              var temp_path="/ambulance/ambulance"+reg_id+"/_update"
              var request = new http.ClientRequest({
                    hostname: "172.16.100.139",
                    port: 9200,
                    path: temp_path,
                    method: "POST",
                    headers: 
                    {
                        "Content-Type": "application/json",
                        "Content-Length": Buffer.byteLength(JSON.stringify(kibana_data))
                    }
                })  
                request.end(JSON.stringify(kibana_data));


              MongoClient.connect(url_mongo, function (err, db)
              {
                if (err) 
                {
                  console.log('Unable to connect to the mongoDB server. Error:', err);
                }
                else 
                {   
                    var coll_ambulances = db.collection('ambulances');
                    coll_ambulances.update( { _id: reg_id },{ $set: {lati:lati, longi:longi} } );
                }
                var coll_users = db.collection('users');

                  coll_users.find({amb_id:reg_id , beingServed:true}).toArray(function (err, result) 
                  {
                    if (err)
                    {
                      console.log(err);
                    } 
                    else if (result.length) 
                    {
                    	console.log("hi_elseif");
                      var alloted_user = result[0];//only one element will be there
                      res.write(JSON.stringify(alloted_user));
                       res.end();
                       db.close();
                    }
                    else 
                    {
                    	console.log("hi_else");
                      res.write("Successfully updated");
                       res.end();
                       db.close();
                    }
                  });

              });
          }
           else if (Object.keys(amb_data).length==2)//Ambulance Not serviceable
          {
              console.log(amb_data);
              var reg_id = amb_data.reg_id;
              var item = amb_data.curr_status;
              console.log("item: "+item)
              MongoClient.connect(url_mongo, function (err, db)
              {
                if (err) 
                {
                  console.log('Unable to connect to the mongoDB server. Error:', err);
                }
                else 
                {   
                    var coll_ambulances = db.collection('ambulances');
                    if(item=="Serviceable")
                    {
                      coll_ambulances.update( { _id: reg_id },{ $set: {readytoServe: true} } ); 
                      res.write("Status updated");
                      res.end();
                      db.close();
                    }
                    else
                    {
                      coll_ambulances.update( { _id: reg_id },{ $set: {readytoServe: false} } );
                      res.write("Status updated");
                      res.end();
                      db.close(); 
                    }
                }
                
              });
          }
           else if (Object.keys(amb_data).length==1)//End trip
          {
              console.log(amb_data);
              var reg_id = amb_data.reg_id;
              
              MongoClient.connect(url_mongo, function (err, db)
              {
                if (err) 
                {
                  console.log('Unable to connect to the mongoDB server. Error:', err);
                }
                else 
                {   
                    var coll_ambulances = db.collection('ambulances');
                    var coll_users = db.collection('users');
                    coll_ambulances.update( { _id: reg_id },{ $set: {isServing: false} } );
                    coll_ambulances.find({ _id: reg_id}).toArray(function (err, result)
                    {
                      if (err)
                        {
                          console.log(err);
                        } 
                        else if (result.length) 
                        {
                            var temp_event_id = result.eventId;
                            coll_users.update( { eventId: temp_event_id },{ $set: {beingServed: false} },{ multi: true } ); 
                            res.write("Successfully ended");
                            res.end();
                            db.close();
                        }
                    });
                }
                
              });
          }
          else
          {
            console.log('amb_data is not of desired length');
          }  
        });
    }
}).listen(3001);