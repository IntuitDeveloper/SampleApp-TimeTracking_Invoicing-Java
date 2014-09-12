'use strict';

/* Controllers */

var controllersModule = angular.module('myApp.controllers', ['ngRoute', 'ui.bootstrap', 'ui.validate', 'myApp.services']);

controllersModule.controller('NavCtrl', ['$scope', '$routeParams', '$location', 'ModelSvc',
    function ($scope, $routeParams, $location, ModelSvc) {
        $scope.navClass = function (page) {
            var currentRoute = $location.path().substring(1);
            return page === currentRoute ? 'active' : '';
        };

        $scope.model = ModelSvc.model;
    }]);


controllersModule.controller('SettingsCtrl', ['$scope', 'SyncRequestSvc', 'ModelSvc', 'CompanySvc',
    function ($scope, SyncRequestSvc, ModelSvc, CompanySvc) {

        $scope.model = ModelSvc.model;
        $scope.syncCustomersMessage = '';
        $scope.syncServiceItemsMessage = '';
        $scope.syncEmployeesMessage = '';

        $scope.showConnectButton = function () {
            return $scope.model.company.connectedToQbo === false;
        }

        var connectedToQBO = function () {
            return $scope.model.company.connectedToQbo === true;
        }

        var disableSyncButton = function (entitySynced) {
            if (connectedToQBO()) {
                //we can synced
                if (entitySynced) {
                    //we have synced, disable the button
                    return true;
                } else {
                    //we have not synced, don't disable the button
                    return false;
                }
            } else {
                //we can't sync, disable the button
                return true;
            }
        };

        var self = this;

        $scope.disableCustomersSyncButton = function () {
            return disableSyncButton($scope.model.company.customersSynced);
        }

        $scope.disableServiceItemsSyncButton = function () {
            return disableSyncButton($scope.model.company.serviceItemsSynced);
        }

        $scope.disableEmployeeSyncButton = function () {
            return disableSyncButton($scope.model.company.employeesSynced);
        }

        $scope.syncCustomers = function () {
            SyncRequestSvc.sendCustomerSyncRequest(self.syncCompleted);
        }

        $scope.syncServiceItems = function () {
            SyncRequestSvc.sendServiceItemsSyncRequest(self.syncCompleted);
        }

        $scope.syncEmployees = function () {
            SyncRequestSvc.sendEmployeeSyncRequest(self.syncCompleted);
        }

        this.syncCompleted = function (data, status, headers, config) {
            var message = data.successful ? data.message : 'Error: ' + data.message;
            if (data.type === 'Customer') {
                $scope.syncCustomersMessage = message;
            } else if (data.type === 'ServiceItem') {
                $scope.syncServiceItemsMessage = message;
            } else if (data.type === 'Employee') {
                $scope.syncEmployeesMessage = message;
            }
            CompanySvc.initializeModel();
        };
    }]);

controllersModule.controller('TimeEntryCtrl', ['$scope', '$filter', 'ModelSvc', 'TimeActivitySvc',
    function ($scope, $filter, ModelSvc, TimeActivitySvc) {

        $scope.model = ModelSvc.model;
        $scope.selectedEmployee = null;
        $scope.selectedCustomer = null;
        $scope.selectedServiceItem = null;
        $scope.description = null;
        $scope.selectedDate = null;
        $scope.selectedDuration = new Date();
        $scope.selectedDuration.setMinutes(15);
        $scope.selectedDuration.setHours(0);

        $scope.datePickerOpened = false;
        $scope.showAlert = false;
        $scope.alertMessage = "";

        var self = this;


        this.resetSelectedDuration = function () {
            var d = new Date()
            d.setMinutes(15);
            d.setHours(0);
            $scope.selectedDuration = d;
        };

        this.showSuccessfulAlert = function (result) {
            $scope.alertMessage = "Time Activity successfully created and pushed to QBO (QBO ID = " + result.qboId + ")";
            $scope.showAlert = true;
        };


        $scope.saveTimeActivity = function () {

            TimeActivitySvc.createTimeActivity({
                minutes: ($scope.selectedDuration.getHours() * 60)
                    + $scope.selectedDuration.getMinutes(),
                date: $filter('date')($scope.selectedDate, 'yyyy-MM-dd'),
                description: $scope.description,
                serviceItem: $scope.selectedServiceItem._links.self.href,
                employee: $scope.selectedEmployee._links.self.href,
                customer: $scope.selectedCustomer._links.self.href,
                company: ModelSvc.model.company._links.self.href
            }, self.showSuccessfulAlert);
            $scope.clearTimeActivity();
        };

        $scope.clearTimeActivity = function () {
            $scope.selectedDate = null;
            self.resetSelectedDuration();
            $scope.selectedEmployee = null;
            $scope.selectedCustomer = null;
            $scope.selectedServiceItem = null;
            $scope.description = null;
        };

        //date picker stuff
        $scope.openDatePicker = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();
            $scope.datePickerOpened = true;
        };

        $scope.timeChanged = function () {
            //dont let the user select 0:00, making 15 minutes the minimum amount of time to be recorded
            if ($scope.selectedDuration.getHours() === 0 && $scope.selectedDuration.getMinutes() === 0) {
                self.resetSelectedDuration();
            }
        };
    }]);

controllersModule.controller('InvoiceCtrl', ['$scope', 'ModelSvc', 'InvoiceSvc',
    function ($scope, ModelSvc, InvoiceSvc) {
        InvoiceSvc.refreshPendingInvoices();


        $scope.model = ModelSvc.model;
        $scope.alertMessage = "";
        $scope.showAlert = false;

        var self = this;

        $scope.expandServiceItemSummary = function (invoice) {
            if (invoice.showServiceItemSummaries) {
                invoice.showServiceItemSummaries = !invoice.showServiceItemSummaries;
            } else {
                invoice.showServiceItemSummaries = true;
            }

        };

        $scope.generateInvoice = function (invoice) {
            InvoiceSvc.submitInvoiceForBilling(invoice, self.showSuccessfulAlert);
        };

        this.showSuccessfulAlert = function (result) {
            $scope.alertMessage = "Invoice successfully created and pushed to QBO (QBO ID = " + result.qboId + ")";
            $scope.showAlert = true;
        };
    }]);

