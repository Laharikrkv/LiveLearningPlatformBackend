@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {
        "common", 
        "courses::entities", 
        "courses::repositories" 
    } )
package com.example.live_learning.bookings;