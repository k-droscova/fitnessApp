# TJV

This is a repository for the semestral work for B231 BI-TJV.


## Theme

![Fitness Classes Diagram](modeling/ER/ER_diagram.png)

This semestral work is about a fitness centre, which provides fitness classes for trainees that are signed up in their system.
Fitness classes are an instance of a class type occurring at particular date and time in a particular Room. Each Room can hold only one class at a given time, and is suitable for specific class types. It also has set maximum capacity of trainees it can hold. 
Class types are for example yoga, aerobik, etc.
Fitness classes can be attended by any number of people up until its capacity. Trainees can attend multiple classes, of course they can only sign up for one class once, and the class must have a free capacity.
Fitness classes are lead by exactly one instructor (an employee of the fitness centre), each instructor can lead multiple fitness classes. 

## Client server

The application is meant for the fitness centre to keep track of their classes and to be able to add new class available for trainees to sign up for. 
Hence the client server will be some sort of web interface where I can see the list of classes, I can filter the ones that have free capacity, I can filter by date (before specific date, after specific date or between), by type of class, by instructor etc. 
More importantly, I can also add new class available for trainees to sign up for (the siging up of trainees for classes is done via other appliaction and is not part of the project). When creating the class, the application needs to keep track of available (suitable) instructors and available (suitable) rooms for the desired date and time. I will also be able to add new instructor, as well as new trainee to the system.
On the other hand, the application will not be able to add class that would occur regularly (e.g. every week). It will also not enable trainees to sign up for classes. 
 
## More complex query

All different types of filters for classes, as listed above (e.g. filter classes led by this instructor between 1st of september 2021 and 30th of september 2021). Moreover, there will be complex queries involved when creating the class, because specific class type can be held in specific rooms and led by specific instructors, which might not be available at that specific time.
