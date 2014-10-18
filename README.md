Time Tracking and Invoicing Java Sample App
=====================================

Welcome to the Intuit Developer's Time Tracking and Invoicing Java Sample App. 

This sample app is meant to provide working examples of how to integrate your app with the Intuit Small Business ecosystem.  Specifically, this sample application demonstrates the following:

1. Implementing OAuth to connect an application to a customer's QuickBooks Online (QBO) account.
2. Using the QBO v3 SDK to create TimeActivity and Invoice objects in QBO.

Please note that while these examples work, features not called out above are not intended to be taken and used in production business applications. In other words, this is not a seed project to be taken cart blanche and deployed to your production environment.  

For example, certain concerns are not addressed at all in our samples (e.g. security, privacy, scalability). In our sample apps, we strive to strike a balance between clarity, maintainability, and performance where we can. However, clarity is ultimately the most important quality in a sample app.

Therefore there are certain instances where we might forgo a more complicated implementation (e.g. caching a frequently used value, robust error handling, more generic domain model structure) in favor of code that is easier to read. In that light, we welcome any feedback that makes our samples apps easier to learn from.

## Table of Contents

* [Requirements](#requirements)
* [First Use Instructions](#first-use-instructions)
* [Running the code](#running-the-code)
* [High Level Workflow](#high-level-workflow)
* [Project Structure](#project-structure)
* [Importing into IntelliJ IDEA & Eclipse](#importing-into-intellij-idea--eclipse)
  * [IntelliJ IDEA](#intellij-idea)
  * [Eclipse](#eclipse)
* [How To Guides](#how-to-guides)
* [Cleaning up the database](#cleaning-up-the-database)
* [Testing the code](#testing-the-code)
  * [Java Junit Tests](#java-junit-tests)
  * [Javascript Karma tests](#javascript-karma-tests)


## Requirements

In order to successfully run this sample app you need a few things:

1. Java 1.7
2. A [developer.intuit.com](http://developer.intuit.com) account
3. An app on [developer.intuit.com](http://developer.intuit.com) and the associated app token, consumer key, and consumer secret.
 
## First Use Instructions

To most convenient way to start playing around with this sample app is to start at [http://developer.intuit.com/v2/sampleapps/details/timetracking_details](http://developer.intuit.com/v2/sampleapps/details/timetracking_details).

1. Clone the GitHub repo to your computer
2. Fill in your [`oauth.json`](oauth.json) file values (app token, consumer key, consumer secret) by copying over from the keys section for your app.

## Running the code

Once the sample app code is on your computer, you can do the following steps to run the app:


1. cd to the project directory
2. Run the command:`./gradlew bootRun` (Mac OS) or `gradlew.bat bootRun` (Windows)
3. Wait until the terminal output displays the **READY** message.
![App Ready](https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/App-Ready.png)
4. Open your browser and go to `http://localhost:9001/app/index.html`


If you happen to be behind an http proxy you will need to create a file called gradle.properties in the root of the project and follow instructions on this [page](http://www.gradle.org/docs/current/userguide/build_environment.html) for configuring gradle to use a proxy.

## High Level Workflow
1. Connect to a QuickBooks Online company.

<img src="https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/timetrackingstep1a.png" alt="Connect to Quickbooks" height="150" width="150">
2. Setup—sync the following from the local database to the QuickBooks Online company.
  - employees—so time can be recorded against a specific service
  - customers—so time can be recorded as billable to a specific customer, 
  - items—the list of billable services.
3. Create and push approved time activity objects to QuickBooks Online company for payroll and billing purposes.
4. Create and push invoice objects to QuickBooks Online company for billing purposes.

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

### IntelliJ IDEA
  * The project is maintained with IntelliJ IDEA and as such an .ipr file is checked into the GitHub repo.

### Eclipse
  * [Importing Project into Eclipse IDE](https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/Importing-Project-into-Eclipse-IDE)

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










    













