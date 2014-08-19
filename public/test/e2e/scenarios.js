'use strict';

//Other libraries
var request = require('request');

//Page Objects
var Navbar = require("./pages/navbar.js");
var PreferencesPage = require("./pages/preferences.js");

describe('my app', function () {
    var companyName = "Russell's Law Firm";
    var company;

    var jar = request.jar();
    var req = request.defaults({
        jar : jar
    });

    function createCompany() {
        req.post("http://localhost:8080/companies", {
                body: {
                    name: companyName
                },
                json: true
            },
            function(error, message, response) {
                company = response;
            });
    }

    function deleteCompanies() {
        req.get("http://localhost:8080/companies/search/deleteCompanies", {
                json: true
            },
            function(error, message, response) {
            });
    }

    beforeEach(function() {
        var flow = protractor.promise.controlFlow();
        flow.execute(deleteCompanies);
        flow.execute(createCompany);
    });

    var navbar = new Navbar();

    describe('basic behavior', function() {

        beforeEach(function() {
            browser.get("");
            browser.waitForAngular();

        });

        it('should automatically redirect to /preferences when location hash/fragment is empty', function () {
            expect(browser.getLocationAbsUrl()).toMatch("/preferences");
        });

        it('should show the company name in the navbar right dropdown', function() {
            expect(navbar.companyNameNavbarButton.getText()).toBe(companyName);
        });
    });

    describe('preferences', function () {

        beforeEach(function () {
            browser.get('#/preferences');
            browser.waitForAngular();
        });

        it('should have the preferences button active on the navbar', function() {
            navbar.assertActiveNavbarButtons("Preferences");
        });

        it('should render preferences page when user navigates to /preferences', function () {
            expect(element.all(by.css('[ng-view] h1')).first().getText()).
                toMatch(/Preferences/);
        });

        it('should have a QuickBooks h3', function() {
            expect(element.all(by.css('[ng-view] h3')).first().getText()).
                toMatch(/QuickBooks/);
        });


        describe('when not connected to QBO', function() {

            var preferencesPage;

            beforeEach(function() {
//                browser.wait(function() {
//                    return protractor.getInstance().isElementPresent(by.css('#connect-to-qbo-div a.intuitPlatformConnectButton'));
//                }, 4000);
            });

            it('should have a section to allow connecting to QBO', function() {
                preferencesPage = new PreferencesPage();
                expect(preferencesPage.connectToQBOText.getText()).toMatch(companyName + ' is not connected to QuickBooks Online\\.');

//                expect(preferencesPage.connectToQBOButton.isDisplayed()).toBeTruthy();
//                expect(preferencesPage.connectToQBOButton.getText()).toMatch("Connect With QuickBooks");
            });
        });
    });
});
