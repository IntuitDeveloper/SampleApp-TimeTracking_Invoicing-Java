Time Tracking and Invoicing Java Sample App
=====================================

Welcome to the Intuit Partner Platform's Time Tracking and Invoicing Java Sample App. 

This sample app is meant to provide working examples of how to integrate your app with the Intuit Small Business ecosystem.  Specifically, this sample application demonstrates the following:

1. Implementing OAuth to connect an application to a customer's QuickBooks Online (QBO) account.
2. Using the QBO v3 SDK to create TimeActivity and Invoice objects in QBO.

Please note that while these examples work, features not called out above are not intended to be taken and used in production business applications. In other words, this is not a seed project to be taken cart blanche and deployed to your production environment.  

For example, certain concerns are not addressed at all in our samples (e.g. security, privacy, scalability). In our sample apps, we strive to strike a balance between clarity, maintainability, and performance where we can. However, clarity is ultimately the most important quality in a sample app.

Therefore there are certain instances where we might forgo a more complicated implementation (e.g. caching a frequently used value, robust error handling, more generic domain model structure) in favor of code that is easier to read. In that light, we welcome any feedback that makes our samples apps easier to learn from.

## Requirements

In order to successfully run this sample app you need a few things:

1. A [developer.intuit.com](http://developer.intuit.com) account
2. An app on [developer.intuit.com](http://developer.intuit.com) and the associated app token, consumer key, and consumer secret.
 
## First Use Instructions

To most convenient way to start playing around with this sample app is to start at [http://developer.intuit.com/v2/sampleapps/details/timetracking_details](http://developer.intuit.com/v2/sampleapps/details/timetracking_details).

1. Clone the GitHub repo to your computer
2. Fill in your oauth.json file values (app token, consumer key, consumer secret) by copying over from the keys section for your app.

## Running the code

Once the sample app code is on your computer, you can do the following steps to run the app:

<ol>
<li>cd to the project directory</li>

<li>Run the command:
./gradlew bootRun
</li>
<li>Open your browser and go to 
```
http://localhost:9001/app/index.html
```
</li>
</ol>

If you happen to be behind an http proxy you will need to create a file called gradle.properties in the root of the project and follow instructions on this [page](http://www.gradle.org/docs/current/userguide/build_environment.html) for configuring gradle to use a proxy.

## Project Structure
* **The Java code for integrating with the QuickBooks Online Accounting is located in the [`src`](src) directory.**
    *  For OAuth implementation see:
        - [`OAuthController.java`](src/main/java/com/intuit/developer/sampleapp/timetracking/oauth/controllers/OAuthController.java)
        - [`OAuthInfoProvider.java`](src/main/java/com/intuit/developer/sampleapp/timetracking/oauth/OAuthInfoProvider.java)
        - [`OAuthInfoProviderImpl.java`](src/main/java/com/intuit/developer/sampleapp/timetracking/oauth/controllers/OAuthInfoProviderImpl.java)
    *  For QBO V3 Java SDK usage see:
        - [`QBOGateway.java`](src/main/java/com/intuit/developer/sampleapp/timetracking/qbo/QBOGateway.java)
        - [`DataServiceFactory.java`](src/main/java/com/intuit/developer/sampleapp/timetracking/qbo/DataServiceFactory.java)
* The Java code for the rest of the application is located in the [`src-general`](src-general) directory
* The HTML, CSS and JavaScript code for the web-based client are is located in the [`public`](public) directory

## Importing into IntelliJ IDEA & Eclipse

To edit the code you will need to open it in an IDE. Currently we support both IntelliJ IDEA and Eclipse.

The project is maintained with IntelliJ IDEA and as such an .ipr file is checked into the GitHub repo. 

To create Eclipse .project and .classpath files for the project execute the following: _./gradlew eclipse_

## How To Guides

How-To guides related to implementation tasks necessary to produce a production-ready Intuit Partner Platform app (e.g. OAuth, OpenId, etc) can be found on the Time Tracking Sample Apps Wiki page:

[Time Tracking Sample App Wiki](https://github.com/IntuitPartnerPlatform/SampleApp-TimeTracking_Invoicing-Java/wiki)

## Cleaning up the database

This app uses a file-based HSQL database that is stored in the _database_ folder in the root of the project. Deleting
this folder will delete all data persisted in the database. The next time you start your app a clean database will be created
with no data.

## Testing the code
The two types of tests in the project (and how to run them) are listed below.

### Java JUnit tests
1. _./gradlew test_

### Javascript Karma tests
To run the Javascript Karma tests you must have NodeJs v0.10.28 or later (http://nodejs.org/) installed and have npm on the path.

1. _cd public_
2. _npm test_










    













