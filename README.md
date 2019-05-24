# TravelMem App for Android

### What problem does this app solve?
* It will allow the user to look for places to travel to.
* Can plan travel.
* Can add previous travel.
* Can record location, pictures and videos.
* Can view these records in a map.

### How will this app solve those problems?
* Uses a web API to search for places to travel to.
* When planning a travel. Routes such as airports to use and road directions can be added. In the middle of the routes, stops can also be added.
* Previous travels can be added through the list of travels or directly place it on the map.
* Pictures and videos can be added to the travel by clicking the marker on the map or opening the travel in the travel list.
* These travels are recorded in a list but the user can open the app's map and see the travels there.

### What is the minimum viable product of this app?
* A login activity for the user.
* A map activity to show traveled and planned places.
* A search activity that consumes an API to search for places to travel to.
* A fragment to edit a travel.
* A list of travels.
* A fragment to play media and view pictures.
* A navigation drawer to show user info.
* A settings fragment for utilities such as theme and notification.

### What features may be added to this app in the future?
* Notify the user when a certain location is reached, using service started in App
* Set custom sound when a certain location is reached
* A way to share travel records with other users.
    * Will also have to add travel visibility, only me, friends and anyone.
* A way to create a travel with other users.
* A way to split travel fees with other users.
* A way for companies to integrate their business with this app so users can view their establishment.
* The app may be put on auto-mode so it can record the travel path automatically.
* In-app camera

### Model

* Travel class.
    * Name
    * Fine/Coarse Location Name
    * LatLng location. The location of this marker
    * Video TravelMedia list
    * Picture TravelMedia list
    * Description
    * Start date
    * End date
    * Completed
    * origin point
    * destination point
    * Waypoint query
    * Route. For history purpose only. Tell user this. User should use google map when navigating
    * View
        * Create travel view
            * EditText
            * origin/destination read only by user
        * Created travel view
            * TextView with edit button/view
            * origin/destination read only by user
            * Copy this travel, copy waypoint query
            * Allow sending direction query to google map
        * A list view of Travel
        * Origin/destination can be set in the travel view, but will be overriden in the RouteEditorUI
            * Setting Origin/destination in travel view will override(delete) route.
        * open RouteEditorUI button
            * Origin/destination icon. opens google places autocomplete
            * Origin/destination can only be edited in here

* TravelMedia
    * type
    * media uri
    * Location LatLng
    * Description
    
* Route
    * Leg list
    * Overview polyline. LatLng list
    * Bounds
    * Copyrights

* Bounds
    * northeast LatLng
    * southeast LatLng

* Leg
    * Step list

* Step
    * instruction
    * distance
    * duration
    * start Point
    * end Point
    * travel mode
    * polyline. LatLng list

* Point
    * Custom name
    * LatLng

* User profile
    * email
    * first name
    * last name

### UI
* Nav drawer
    * Shows user profile
    * Menu items
        * Travels
        * Images
        * Videos
        * Logout

* Toolbar menus
    * Settings    

* TravelsMapUI. Activity
    * Uses google map
    * Shows Travels on the map
    * List of travel in a collapsable view
    * Can show selected Travel route
    * Can show the latest media with a location in the map
    * Can open a SearchLocationFragment to search for places to travel to

* RouteEditorUI. Fragment.
    * Uses google map
    * Shows detailed Travel route on the map
    * Add first opening warning that the user should not use this for navigation

* ImagesUI. Activity
    * Shows all images

* ImageViewerUI
    * Shows details of the image
    * Allow user to click a locate button and edit location button

* VideosUI. Activity
    * Shows all videos

* VideoViewerUI
    * Shows details of the video
    * Allow user to click a locate button and edit location button

### Libraries and API
* Google Maps, Direction, Places, Location.
* Google Maps Android API Utility Library
* Retrofit 2
* Gson
