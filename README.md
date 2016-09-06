Time Tracking and Invoicing Java Sample App
=====================================

<p>Welcome to the Intuit Developer's Time Tracking and Invoicing Java Sample App.</p>
<p>This sample app is meant to provide working examples of how to integrate your app with the Intuit Small Business ecosystem.  Specifically, this sample application demonstrates the following:</p>

<ul>
	<li>Implementing OAuth to connect an application to a customer's QuickBooks Online company.</li>
	<li>Syncing employee, customer, and service item data from the app's local database to the QuickBooks Online company.</li>
	<li>Using the QuickBooks Online SDK to create TimeActivity and Invoice objects in the QuickBooks Online company.</li>
</ul>

<p>Please note that while these examples work, features not called out above are not intended to be taken and used in production business applications. In other words, this is not a seed project to be taken cart blanche and deployed to your production environment.</p>  

<p>For example, certain concerns are not addressed at all in our samples (e.g. security, privacy, scalability). In our sample apps, we strive to strike a balance between clarity, maintainability, and performance where we can. However, clarity is ultimately the most important quality in a sample app.</p>

<p>Therefore there are certain instances where we might forgo a more complicated implementation (e.g. caching a frequently used value, robust error handling, more generic domain model structure) in favor of code that is easier to read. In that light, we welcome any feedback that makes our samples apps easier to learn from.</p>

## Table of Contents

* [Requirements](#requirements)
* [First Use Instructions](#first-use-instructions)
* [Running the code](#running-the-code)
* [High Level Workflow](#high-level-workflow)
* [Importing into IntelliJ IDEA & Eclipse](#importing-into-intellij-idea--eclipse)
  * [IntelliJ IDEA](#intellij-idea)
  * [Eclipse](#eclipse)
* [Project Structure](#project-structure)
* [How To Guides](#how-to-guides)
* [Testing the code](#testing-the-code)
  * [Java Junit Tests](#java-junit-tests)
  * [Javascript Karma tests](#javascript-karma-tests)
* [Reset the App](#reset-the-app)
* [Watch & Learn](#watch--learn)
* [More Information](#more-information)


## Requirements

In order to successfully run this sample app you need a few things:

1. Java 1.7
2. A [developer.intuit.com](http://developer.intuit.com) account
3. An app on [developer.intuit.com](http://developer.intuit.com) and the associated app token, consumer key, and consumer secret.
4. QuickBooks Java SDK (already included in the [`libs`](libs) folder) 
 
## First Use Instructions

1. Clone the GitHub repo to your computer
2. Fill in your [`oauth.json`](oauth.json) file values (app token, consumer key, consumer secret) by copying over from the keys section for your app.

## Running the code

Once the sample app code is on your computer, you can do the following steps to run the app:

1. cd to the project directory</li>
2. Run the command:`./gradlew bootRun` (Mac OS) or `gradlew.bat bootRun` (Windows)</li>
3. Wait until the terminal output displays the **READY** message.
<p align="center"><img src="https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/App-Ready.png" alt="App Ready" height="200" width="1000"/>
4. Open your browser and go to `http://localhost:9001/app/index.html`</li>

If you happen to be behind an http proxy you will need to create a file called gradle.properties in the root of the project and follow instructions on this [page](http://www.gradle.org/docs/current/userguide/build_environment.html) for configuring gradle to use a proxy.

## High Level Workflow
<ol>

<li>Connect to a QuickBooks Online company.
<p align="center"><img src="https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/timetrackingstep1a.png" alt="Connect to Quickbooks" height="250" width="250"/></p>
</li>

<li>Setup—sync the following from the local database to the QuickBooks Online company.
<ul>
  <li>employees—so time can be recorded against a specific service,</li>
  <li>customers—so time can be recorded as billable to a specific customer, </li>
  <li>items—the list of billable services.</li>
</ul>
<p align="center"><img src="https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/timetrackingstep1b.png" alt="Sync Entities" height="168" width="250"></p>
</li>

<li>Create and push approved time activity objects to QuickBooks Online company for payroll and billing purposes.
	<p align="center"><img src="https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/timetrackingstep2.png" alt="Sync Entities" height="243" width="250"></p>
</li>

<li>Create and push invoice objects to QuickBooks Online company for billing purposes.
<p align="center"><img src="https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/images/timetrackingstep3.png" alt="Sync Entities" height="84" width="500"></p>
</li>
</ol>

## Importing into IntelliJ IDEA & Eclipse

To edit the code you will need to open it in an IDE. Currently we support both IntelliJ IDEA and Eclipse.

### IntelliJ IDEA
  * The project is maintained with IntelliJ IDEA and as such an .ipr file is checked into the GitHub repo.

### Eclipse
  * [Importing Project into Eclipse IDE](https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/Importing-Project-into-Eclipse-IDE)

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

## How To Guides

The following How-To guides related to implementation tasks necessary to produce a production-ready Intuit Partner Platform app (e.g. OAuth, OpenId, etc) are available:

* [OAuth How To Guide (Java)](https://github.com/IntuitDeveloper/SampleApp-TimeTracking_Invoicing-Java/wiki/OAuth-How-To-Guide-(Java))

## Testing the code
The two types of tests in the project (and how to run them) are listed below.

### Java JUnit tests
1. _./gradlew test_

### Javascript Karma tests
To run the Javascript Karma tests you must have NodeJs v0.10.28 or later (http://nodejs.org/) installed and have npm on the path.

1. _cd public_
2. _npm test_

## Reset the App

This app uses a file-based HSQL database that is stored in the _database_ folder in the root of the project. Deleting
this folder will delete all data persisted in the database. The next time you start your app a clean database will be created
with no data.

## Watch & Learn

<a href="http://www.youtube.com/watch?feature=player_embedded&v=I_KgAmtMtLk
" target="_blank"><img src="http://img.youtube.com/vi/I_KgAmtMtLk/0.jpg" 
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>

## More Information

More detailed information for this sample app can be found here [here](https://developer.intuit.com/sampleapps/timetracking).











    













