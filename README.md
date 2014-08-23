Time Tracking and Invoicing Java Sample App
=====================================

Welcome to the Intuit Partner Platform's Time Tracking and Invoicing Java Sample App. 

This sample app is meant to provide working examples of how to integrate your app with the Intuit Small Business ecosystem.  Specifically, this sample application demonstrates the following:

1. Implementing OAuth to connect an application to a customer's QuickBooks Online (QBO) account.
2. Using the QBO v3 SDK to create TimeActivity and Invoice objects in QBO.

Please note that while these examples work, features not called out above are not intended to be taken and used in production business applications. In other words, this is not a seed project to be taken cart blanche and deployed to your production environment.  

For example, certain concerns are not addressed at all in our samples (e.g. security, privacy, scalability). In our sample apps, we strive to strike a balance between clarity, maintainability, and performance where we can. However, clarity is ultimately the most important quality in a sample app.

Therefore there are certain instances where we might forego a more complicated implementation (e.g. cacheing a frequently used value, robust error handling, more generic domain model structure) in favor of code that is easier to read. In that light, we welcome any feedback that makes our samples apps easier to learn from. 

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

1. _./gradlew bootRun_

On **_another_** command prompt window:

1. _cd public_
2. _npm run protractor_









    













