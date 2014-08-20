Time Tracking and Invoicing Java Sample App
=====================================

Welcome to Intuit Partner Platform's Time Tracking and Invoicing Java Sample App.

## First use instructions

The most conveinent way to start playing around with this sample app is to start at [http://developer.intuit.com/v2/sampleapps/details/timetracking_details](http://developer.intuit.com/v2/sampleapps/details/timetracking_details). 

There you can quickly:

1. Create a developer account
2. Create your first app and get development OAuth keys created
3. Get a sandbox QuickBooks Online company provisioned
4. Download this sample app's source code with an additional file containing your OAuth keys pre-installed in the project

## Running the code

Once the sample app code is on your computer, you can do the following steps to run the app:

1. Unzip the zip file
2. cd to the unzipped folder
3. Run the command: _./gradlew bootRun_
4. Open your browser to http://localhost:8080/app/index.html

## How To Guides

How-To guides related to implementation tasks necessary to produce a production-ready Intuit Partner Platform app (e.g. OAuth, OpenId, etc) can be found on the Time Tracking Sample Apps Wiki page:

[Time Tracking Sample App Wiki](https://github.com/IntuitPartnerPlatform/SampleApp-TimeTracking_Invoicing-Java/wiki)



## Editing the code
To edit the code you will need to open it in an IDE. Currently we support both IntelliJ IDEA and Eclipse.

The project is maintained with IntelliJ IDEA and as such an .ipr file is checked into the GitHub repo. 

To create Eclipse .project and .classpath files for the project execute the following: _./gradlew eclipse_

## Testing the code
The three types of tests in the project (and how to run them) are listed below.

### Java JUnit tests
1. _./gradlew test_

### Javascript Karma tests
To run the Javascript Karma tests you must have NodeJs v0.10.28 or later (http://nodejs.org/) installed and have npm on the path.

1. _cd public_
2. _npm test_

### Javascript Protractor tests
To run the Javascript Protractor tests you must have NodeJs v0.10.28 or later (http://nodejs.org/) installed and have npm on the path.

On one command prompt window:

1. _./gradlew bootRun _

On another command prompt window:

1. _cd public_
2. _npm protractor_ (on **_another_** command prompt window)









    













