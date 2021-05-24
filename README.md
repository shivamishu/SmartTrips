# CMPE277 Project
## Smart Trips
 
Author: Shivam Shrivastav, Praveen Nayak  
Smart Trips: An Android mobile application to build and save smart trips.

## Overview:
Smart Trips is a smart travel planner app built on an android platform which lets you plan your trip by selecting interesting destinations along the route.  

The app provides optimized routes to popular places  and allows you to choose gas stations, charging points and restaurants on the way towards the destination.  

App offers to avoid deviation/long detours to reach places of interest and other essential places.  

## Problem Statement
Nearby places displayed by Google Maps consider only the current location suggestions.  
While searching along the route,  Google Maps shows only the places which are close to the current location.  
The search radius is large which results in a huge list to be sorted by the user manually.  
This is prone to errors and involves unnecessary detours.  

## Solution Proposed
Nearby places displayed by our app considers the entire route by optimally selecting the waypoints.  
Even if the search radius is large, the search results of the places will be shown only if it falls under the custom polygon(red bound polyline).  
This will avoid unnecessary long detours and provide an optimized route.   

### Unique Selling Points:

Narrow down the place search nearest to the route.  
Display Time taken to reach each of the popular places so that users can plan activities.	  
(ex : Deciding restaurant for Lunch/Dinner based on the time displayed by our app). 

Our app shows popular places in close proximity along the route, unlike Google Maps.  
Our app shows optimized routes for multiple stops to avoid unnecessary detours, which is a manual tedious task, by hit and trial, in Google Maps.  

### Logic Flow and Functionality

Screen 1. Users can sign in or use the guest login option to start using the app.  
Screen 2. User selects the commute mode, source and destination and clicks Next button.  
Screen 3. Users can view the distance and time taken between source and destination for the mode chosen.  
	Users then have the choice to select the places of interest such as popular places , restaurants, gas stations and charging points along the route.  
Screen 4: Users based on the waypoint type selected such as Popular, Restaurants, Charging Points and Gas Stations, will be displayed with a list of options.  
Based on the details for each option, such as distance and time taken to reach the waypoint, the user can then decide to add all the selected options to his original route.  
Screen 5: Users will be navigated to Screen 2 with all the options added to the route and the app will show the optimized route (shortest).  
Screen 6:Users can save their favorite trips for future use and can be viewed in the trip history section.This option is only available if logged in via google account.  


## Setup Required
Clone/Download the codebase and build the project in Android Studio:  
https://github.com/shivamishu/SmartTrips.git  
Create a project on Google Cloud.  
Enable and generate API Keys for Google Maps SDK, Directions and Places API.  
Locate/Create file 'google_maps_api.xml' under values directory and enter your own above API Keys in this file.  
Also, Locate/Create file 'local.properties' in root directory and add your own above Google API Key in this file.  
Create Firebase Account and connect to Firebase Console.  
Add project with name “smart trips” and create project.  
Follow steps to generate a google-services.json file (app/google-service.json):   
https://firebase.google.com/docs/android/setup  
In the project under Firebase console, go to Authentication and make sure that Email/Password and Google is enabled.  
Save the above file in directory: app/google-service.json  
Open the codebase, build and run the app.  


## Demo
[Demo URL](https://drive.google.com/file/d/1EMNWDa_UVcH9VySVWNya_wVXKE0JKYlV/view?usp=sharing)
