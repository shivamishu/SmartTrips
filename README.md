# CMPE277 Project
## Smart Trips
 
Author: Shivam Shrivastav, Praveen Nayak  
Smart Trips: An Android mobile application to build and save smart trips.

### Application Demo
[Demo Video](https://d3fu3214bbcc0g.cloudfront.net/smarttrips.html)

## Overview:
Smart Trips is a smart travel planner app built on an android platform which lets you plan your trip by selecting interesting destinations along the route.  

The app provides optimized routes to popular places  and allows you to choose gas stations, charging points and restaurants on the way towards the destination.  

App offers to avoid deviation/long detours to reach places of interest and other essential places.  

## Problem Statement
![Picture1](https://user-images.githubusercontent.com/24988178/119447169-5a63fe00-bce4-11eb-8fda-a8cb5bc16172.png)
Nearby places displayed by Google Maps consider only the current location suggestions.  
While searching along the route,  Google Maps shows only the places which are close to the current location.  
The search radius is large which results in a huge list to be sorted by the user manually.  
This is prone to errors and involves unnecessary detours.  

## Solution Proposed
![Picture2](https://user-images.githubusercontent.com/24988178/119447166-59cb6780-bce4-11eb-8c49-d00c9a016d56.png)
Nearby places displayed by our app considers the entire route by optimally selecting the waypoints.  
Even if the search radius is large, the search results of the places will be shown only if it falls under the custom polygon(red bound polyline).  
This will avoid unnecessary long detours and provide an optimized route.   

### Unique Selling Points:

Narrow down the place search nearest to the route.  
Display Time taken to reach each of the popular places so that users can plan activities.	  
(ex : Deciding restaurant for Lunch/Dinner based on the time displayed by our app). 

Our app shows popular places in close proximity along the route, unlike Google Maps.  
Our app shows optimized routes for multiple stops to avoid unnecessary detours, which is a manual tedious task, by hit and trial, in Google Maps.  

## Architecture Diagram
![Picture3](https://user-images.githubusercontent.com/24988178/119447165-5932d100-bce4-11eb-8926-6a9e2ff5cedf.png)

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


### Tech Details:
Google Firebase for Authentication.  
Custom Algorithm to Calculate the Optimized Waypoints, to consider apt radius for places search, and to minimize the detours.   
Jackson Parser and annotations.  
RecyclerView, CardView, Android MaterialUI Floating Buttons, NestedScrollView, Menu Drawer, Fragments, Displaying Home Up Button(Customized the back arrow button to reversed app icon with back arrow).  
Custom Control : Collapsable Panel with Animation.  
Custom Reusable Class for Calling Async REST APIs.  
Google Maps with custom markers.  
WhatsApp Overlay on Maps.  
Navigation using Google Maps.  
Google Firebase Realtime database for saving the trip information.  

## Screenshots

![Picture4](https://user-images.githubusercontent.com/24988178/119447161-5932d100-bce4-11eb-9e92-61df86233024.png)
![Picture5](https://user-images.githubusercontent.com/24988178/119447159-589a3a80-bce4-11eb-8279-36e197d1e870.png)
![Picture6](https://user-images.githubusercontent.com/24988178/119447157-5801a400-bce4-11eb-8a2e-4496075eb989.png)
![Picture7](https://user-images.githubusercontent.com/24988178/119447155-5801a400-bce4-11eb-9771-df9747c70877.png)
![Picture8](https://user-images.githubusercontent.com/24988178/119447153-57690d80-bce4-11eb-91c3-863bc1e08d4f.png)
![Picture9](https://user-images.githubusercontent.com/24988178/119447150-56d07700-bce4-11eb-8bfd-f37d99f42ab0.png)
