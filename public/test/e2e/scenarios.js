'use strict';

//Other libraries
var request = require('request');

//Page Objects
var Navbar = require("./pages/navbar.js");
var SettingsPage = require("./pages/settings.js");

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

        it('should automatically redirect to /settings when location hash/fragment is empty', function () {
            expect(browser.getLocationAbsUrl()).toMatch("/settings");
        });

//        it('should show the company name in the navbar right dropdown', function() {
//            expect(navbar.companyNameNavbarButton.getText()).toBe(companyName);
//        });
    });

    describe('settings', function () {

        beforeEach(function () {
            browser.get('#/settings');
            browser.waitForAngular();
        });

        it('should have the preferences button active on the navbar', function() {
            navbar.assertActiveNavbarButtons("Settings");
        });

        it('should have a QuickBooks h3', function() {
            expect(element.all(by.css('[ng-view] h3')).first().getText()).
                toMatch(/Step 1a: Connect To QuickBooks Online/);
        });


        describe('when not connected to QBO', function() {

            var settingsPage;

            beforeEach(function() {
                protractor.getInstance().driver.navigate().refresh();
                browser.wait(function() {
                    return protractor.getInstance().isElementPresent(by.css('#connect-to-qbo-div a.intuitPlatformConnectButton'));
                }, 10000);
            });

            it('should have a section to allow connecting to QBO', function() {
                settingsPage = new SettingsPage();
                expect(settingsPage.connectToQBOText.getText()).toMatch(companyName + ' is not connected to QuickBooks Online\\.');

                expect(settingsPage.connectToQBOButton.isDisplayed()).toBeTruthy();
//                expect(settingsPage.connectToQBOButton.getText()).toMatch("Connect With QuickBooks");
            });
        });
    });
});
