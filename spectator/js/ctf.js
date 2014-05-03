var map;
var json;
var markers = [];
var id = $.url().param('id')
var bluePlayerIconDead = "images/blue_marker_dead.png";
var bluePlayerIconAlive = "images/blue_marker_alive.png";
var redPlayerIconDead = "images/red_marker_dead.png";
var redPlayerIconAlive = "images/red_marker_alive.png";
var blueFlagIcon = "images/ctf_logo_blue.png";
var redFlagIcon = "images/ctf_logo_red.png";
var radiusInMeters = '';

var ctf = {
  jsonFile: "../lobbies/" + id + ".json",
  accuracy: '',
  epicenter: '35.12113674,-89.93842138',
  status: '',
  north: '',
  south: '',
  east: '',
  west: '',
  blueScore: '0',
  redScore: '0',
  players: [],
  flags: [],
  bases: []
}

function loadJson()
{
  json = $.getJSON( ctf.jsonFile + "?t=" + new Date().getTime(), function() {
    console.log(ctf.jsonFile + " refreshed.");
  });
  json.done(function () {
    parseJson();
  });
  setTimeout(loadJson, 500);
}

function parseJson()
{
  ctf.accuracy = json.responseJSON[0].ACCURACY;
  ctf.epicenter = json.responseJSON[0].EPICENTER;
  ctf.status = json.responseJSON[0].STATUS;
  ctf.north = json.responseJSON[0].NORTH;
  ctf.south = json.responseJSON[0].SOUTH;
  ctf.east = json.responseJSON[0].EAST;
  ctf.west = json.responseJSON[0].WEST;
  ctf.redScore = json.responseJSON[0].RED_SCORE;
  ctf.blueScore = json.responseJSON[0].BLUE_SCORE;
  ctf.players = json.responseJSON[0].PLAYERS;
  ctf.flags = json.responseJSON[1].FLAGS;
  ctf.bases = json.responseJSON[2].BASES;
  updateMarkers();
  updateScore();
}

function updateScore()
{
  $('#redPoints').html(ctf.redScore);
  $('#bluePoints').html(ctf.blueScore);
}

function updateMarkers()
{
  var count = 0;
  console.log(markers);
  // For each player, base and flag add markers
  for(var i = 0; i < ctf.players.length; i++)
  {
    var stringLatLng = ctf.players[i].LOCATION.split(",");
    markers[count].setPosition(new google.maps.LatLng(stringLatLng[0], stringLatLng[1]));
    if(ctf.players[i].TEAM == 1)
    {
      if(ctf.players[i].STATUS == 0)
      {
        // player is dead
        markers[count].setIcon(bluePlayerIconDead);
      } else if(ctf.players[i].STATUS == 1) {
        markers[count].setIcon(bluePlayerIconAlive);
      }
      
    } else if(ctf.players[i].TEAM == 2) {
      if(ctf.players[i].STATUS == 0)
      {
        // player is dead
        markers[count].setIcon(redPlayerIconDead);
      } else if(ctf.players[i].STATUS == 1) {
        markers[count].setIcon(redPlayerIconAlive);
      }
    }
    count++;
  }
  for(var i = 0; i < ctf.flags.length; i++)
  {
    var stringLatLng = ctf.flags[i].LOCATION.split(",");
    markers[count].setPosition(new google.maps.LatLng(stringLatLng[0], stringLatLng[1]));
    count++;
  }
}

// Add a marker to the map and push to the array.
function addMarker(entity, type) {
  // Get the lat and long as doubles from location
  var stringLatLng = entity.LOCATION.split(",");
  var image;
  var marker_title;
  if(type == "player")
  {
    marker_title = entity.USERNAME;
    if(entity.TEAM == 1)
    {
      if(entity.STATUS == 0)
      {
        // player is dead
        image = bluePlayerIconDead;
      } else if(entity.STATUS == 1) {
        image = bluePlayerIconAlive;
      }
      
    } else if(entity.TEAM == 2) {
      if(entity.STATUS == 0)
      {
        // player is dead
        image = redPlayerIconDead;
      } else if(entity.STATUS == 1) {
        image = redPlayerIconAlive;
      }
    }
  } else if(type == "flag") {
    if(entity.TEAM == 1)
    {
      marker_title = "Blue Flag";
      image = blueFlagIcon;
    } else if(entity.TEAM == 2) {
      marker_title = "Red Flag";
      image = redFlagIcon;
    } 
  } else if(type == "base") {
    if(entity.TEAM == 1)
    {
      marker_title = "Blue Base";
      image = blueBaseIcon;
    } else if(entity.TEAM == 2) {
      marker_title = "Red Base";
      image = redBaseIcon;
    } 
  }

  var markerIcon = {
    size: new google.maps.Size(64, 64),
    url: image
};

  var markerPosition = new google.maps.LatLng(stringLatLng[0],stringLatLng[1]);
  var markerIcon = new google.maps.MarkerImage(image, null, null, null, new google.maps.Size(24,24));
  var marker = new google.maps.Marker({
    position: markerPosition,
    map: map,
    icon: markerIcon,
    title: marker_title
  });
  markers.push(marker);
}

// Sets the map on all markers in the array.
function setAllMap(map) {
  for (var i = 0; i < markers.length; i++) {
    markers[i].setMap(map);
  }
}

// Removes the markers from the map, but keeps them in the array.
function clearMarkers() {
  setAllMap(null);
}

// Shows any markers currently in the array.
function showMarkers() {
  setAllMap(map);
}

// Deletes all markers in the array by removing references to them.
function deleteMarkers() {
  clearMarkers();
  markers = [];
}

function initialize() {
  var stringLatLng = ctf.epicenter.split(",");
  var mapOptions = {
    zoom: 18,
    center: new google.maps.LatLng(stringLatLng[0],stringLatLng[1])
  };

  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

  var arena;

  // Define the LatLng coordinates for the polygon's path.
  var arenaCoords = [
    new google.maps.LatLng(ctf.north, ctf.west),
    new google.maps.LatLng(ctf.north, ctf.east),
    new google.maps.LatLng(ctf.south, ctf.east),
    new google.maps.LatLng(ctf.south, ctf.west),
    new google.maps.LatLng(ctf.north, ctf.west),
  ];

  var worldCoords = [
    new google.maps.LatLng(ctf.north+10,ctf.west-10),
    new google.maps.LatLng(ctf.south-10, ctf.west-10),
    new google.maps.LatLng(ctf.south-10, ctf.east+10),
    new google.maps.LatLng(ctf.north+10, ctf.east+10),
    new google.maps.LatLng(ctf.north+10,ctf.west-10)
  ];

  // Construct the polygon.
  arena = new google.maps.Polygon({
    paths: [worldCoords, arenaCoords],
    strokeColor: '#EDEDED',
    strokeOpacity: 0.8,
    strokeWeight: 2,
    fillColor: '#222222',
    fillOpacity: 0.35
  });

  var redBaseLatLng = ctf.bases[1].LOCATION.split(",");
  var redBase = {
      strokeColor: '#FF0000',
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor: '#FF0000',
      fillOpacity: 0.35,
      map: map,
      center: new google.maps.LatLng(redBaseLatLng[0], redBaseLatLng[1]),
      radius: radiusInMeters
    };
    redBase = new google.maps.Circle(redBase);

  var blueBaseLatLng = ctf.bases[0].LOCATION.split(",");
  var blueBase = {
      strokeColor: '#0000FF',
      strokeOpacity: 0.8,
      strokeWeight: 2,
      fillColor: '#0000F0',
      fillOpacity: 0.35,
      map: map,
      center: new google.maps.LatLng(blueBaseLatLng[0], blueBaseLatLng[1]),
      radius: radiusInMeters
    };
    blueBase = new google.maps.Circle(blueBase);


  arena.setMap(map);
  initializeMarkers();
  setAllMap(map);
  loadJson();
}

//setTimeout(loadJson, 1);

function initialJsonLoad()
{
  json = $.getJSON( ctf.jsonFile + "?t=" + new Date().getTime(), function() {
    console.log(ctf.jsonFile + " loaded.");
  });
  json.done(function () {
    initialParseJson();
  });
}

function initialParseJson()
{
  ctf.accuracy = json.responseJSON[0].ACCURACY;
  ctf.epicenter = json.responseJSON[0].EPICENTER;
  ctf.status = json.responseJSON[0].STATUS;
  ctf.north = json.responseJSON[0].NORTH;
  ctf.south = json.responseJSON[0].SOUTH;
  ctf.east = json.responseJSON[0].EAST;
  ctf.west = json.responseJSON[0].WEST;
  ctf.players = json.responseJSON[0].PLAYERS;
  ctf.flags = json.responseJSON[1].FLAGS;
  ctf.bases = json.responseJSON[2].BASES;
  console.log("4: setRadiusInMeters");
  setRadiusInMeters();
  console.log("6: initialize");
  initialize();
}

function initializeMarkers()
{
  // For each player, base and flag add markers
  for(var i = 0; i < ctf.players.length; i++)
  {
    addMarker(ctf.players[i], "player");
  }
  for(var i = 0; i < ctf.flags.length; i++)
  {
    addMarker(ctf.flags[i], "flag");
  }
}

function setRadiusInMeters()
{
  var stringLatLng = ctf.bases[0].LOCATION.split(",");
  console.log("5a: stringLatLng:" + stringLatLng);
  var base_lat = stringLatLng[0];
  var base_long = stringLatLng[1];
  console.log("5b: base_lat " + base_lat);
  console.log("5c: base_long " + base_long);
  var lat2 = parseFloat(base_lat, 10) + ctf.accuracy;
  console.log("5d: lat2 " + lat2);
  radiusInMeters = measure(base_lat, base_long, lat2, base_long);
  console.log("5e: Radius in meters: " + radiusInMeters);
}

function measure(lat1, lon1, lat2, lon2){  // generally used geo measurement function
    var R = 6378.137; // Radius of earth in KM
    var dLat = (lat2 - lat1) * Math.PI / 180;
    var dLon = (lon2 - lon1) * Math.PI / 180;
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLon/2) * Math.sin(dLon/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;
    console.log("5f: measured meters " + d);
    return d * 1000; // meters
}

$( document ).ready(function() {
    // First load the json file for the first time. 
    initialJsonLoad();
    // All the initial values should be parsed by now.
});


