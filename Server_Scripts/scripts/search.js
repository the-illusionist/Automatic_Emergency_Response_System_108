// GOOGLE API Key : AIzaSyA0WtDBuWAa6QC1LA0xv73JWl7n4Vzap3s

//import the mongodb native drivers.
var mongodb = require('mongodb');
//We need to work with "MongoClient" interface in order to connect to a mongodb server.
var MongoClient = mongodb.MongoClient;
// Connection URL. This is where your mongodb server is running.
var url_mongo = 'mongodb://172.16.100.139:27017/test'	;
// Use connect method to connect to the Server
var url = require( "url" );
var queryString = require( "querystring" );
var http = require('http');
// var request = require('request');

var distance = require('google-distance');
distance.apiKey = 'AIzaSyBxGWZUSBKoVCPFh7SAtI6dFaJ1owWRhDk';


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
						var is_being_Served,ambu_id;
				 
						
				MongoClient.connect(url_mongo, function (err, db)
				{
				if (err) 
				{
					console.log('Unable to connect to the mongoDB server. Error:', err);
				}
				else 
				{		
					var coll_users = db.collection('users');
					var coll_ambulances = db.collection('ambulances');

					coll_users.count(function(err, count) 
					{
								// assert.equal(null, err);
								event_id = count;
								console.log("count ",event_id);
								// console.log("err ",err);
								// db.close();
						});
						//HURRAY!! Connected :)
						//console.log('Connection established to', url);
						
//User app sends Ambulance Id..So return position of that ambulance only by querying DB ###################################################################
						if(Object.keys(user_data).length==1)
						{
							// Get the documents collection
							var collection = db.collection('ambulances');
							collection.find({_id: user_data.id}).toArray(function (err, result) 
							{
								if (err)
								{
									console.log(err);
								} 
								else if (result.length) 
								{
									var alloted_ambulance = result[0];//only one element will be there
									console.log(alloted_ambulance);
									res.write(JSON.stringify(alloted_ambulance));
								}
								else 
								{
									console.log('\nNo document(s) found with defined "find" criteria!');
								}
								//Close connection
								res.end();
								db.close();
							});
						}
//User app sends user_data #####################################################################################################################################						
						else
						{
							coll_users.find({email: user_data.email, beingServed: true}).toArray(function (err, result)
							{
								if (err)
								{
									console.log(err);
								} 
								else if (result.length) 
								{
									is_being_Served=true;	
									var temp_user  = result[0];	
									ambu_id=temp_user.amb_id;
									allot();
								}
								else
								{
									is_being_Served=false;
									allot();
								}
							});

							function allot()
							{
//Already alloted ambulance ###########################################################################################################################################
							if(is_being_Served == true)
							{
								coll_ambulances.find({_id: ambu_id}).toArray(function (err, result)
								{
										if (err)
										{
											console.log(err);
										} 
										else if (result.length) 
										{
											res.write(JSON.stringify(result[0]));
											console.log("already alloted ambulance is: ",result[0]);
										}
										else 
									{
										console.log('\nNo ambulance(s) found with defined "find" criteria!');
									}
									res.end();
								});
							}
//Alot new ambulance ###########################################################################################################################################
							else
							{
//Send User_Data to Kibana Server START #########################################################################################
								//later we have to send some more data to kibana
								var request = new http.ClientRequest({
									hostname: "172.16.100.139",
									port: 9200,
									path: "/btp108/trial/",
									method: "POST",
									headers: 
									{
											"Content-Type": "application/json",
											"Content-Length": Buffer.byteLength(JSON.stringify(user_data))
									}
								})	
								request.end(JSON.stringify(user_data));
//Send User_Data to Kibana Server END ###########################################################################################									
								coll_ambulances.find().toArray(function (err, result)
								{
									if (err) 
									{
										console.log(err);
									} 
									else if (result.length)
									{	
										var mini_dist = Number.POSITIVE_INFINITY;
										var best_ambulance_id;
										var best_ambulance;
										var temp_ambulance;
										var flag = 0;
										var flag1 = 0;
										var dist;
										var val=25;
											
										find_dist();

										function find_dist()
										{
											
											flag1++;
											temp_ambulance = result[flag1-1];
											console.log(temp_ambulance);
// find best ambulance ###############################################################################################################################################
											distance.get(
											{
												index: temp_ambulance._id,
												origin: user_data.lati.toString() + "," + user_data.longi.toString(),
													destination: temp_ambulance.lati.toString() + "," + temp_ambulance.longi.toString()
											},function(err, data)//If distance is received
											{
												if (err) 
													return console.log(err);
												else
												{
													dist = data.distanceValue;
													val = data.index;
													console.log(data);
													for(idx=0;idx<result.length;idx++)
													{
														if(result[idx]._id==val)
														{
															break;
														}
													}
													if(dist<mini_dist && result[idx].isServing==false && result[idx].readytoServe==true)
													{
														mini_dist = dist;
														best_ambulance_id = val;
														best_ambulance = result[idx];
														console.log("Yeah!!! minimum dist is ",mini_dist);
													}
												}
												flag++;
												console.log("flag",flag);
												if(flag==result.length)
												{
													res.write(JSON.stringify(best_ambulance));
													console.log("best ambulance id is: ",best_ambulance_id);
													console.log("best ambulance is: ",best_ambulance);
													write_in_db();
												}
											});
											
											if(flag1<result.length)
											{
												find_dist();
											}
											
										}
// Write in User DB & update ambulance DB #############################################################################################################################################
										function write_in_db()
										{
											console.log("Event idd: ", event_id);

											var email = user_data.email;
											var name = user_data.name;
											var contact = user_data.contact;
											var lati = user_data.lati;
											var longi = user_data.longi;
											var time =  new Date();
											var amb_id = best_ambulance_id;
											var beingServed = true;

											var json_object = {
												eventId: event_id,
												email: email,
												name: name,
												contact: contact,
												lati: lati,
												longi: longi,
												time: time,
												amb_id: amb_id,
												beingServed: beingServed
											};

											coll_users.insert(json_object);

											coll_ambulances.update( { _id: best_ambulance_id },{ $set: {isServing: true, eventId: event_id} } );
// Send Push Notification ########################################################################################################################################

											var message_tuple = {
												name: name, contact:contact, lati:lati, longi:longi
											};
											
											var request = require('request');
											var send_data = {
											    message : message_tuple, regId: best_ambulance_id
											};

											request({url:"http://172.16.100.139/btp/send_message.php", qs:send_data}, function(err, response, body) {
											  if(err) { console.log(err); return; }
											  console.log("Get response: " + response.statusCode);
											});

											close_all();
										}
									} 
									else 
									{
										console.log('\nNo ambulance(s) found with defined "find" criteria!');
									}
									//Close connection
									function close_all()
									{
										res.end();
											db.close();
									}
								});
							}
							}
					}
				}
			});
				});
		}
}).listen(3000);
