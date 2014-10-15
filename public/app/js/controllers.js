'use strict';

/* Controllers */

var controllersModule = angular.module('myApp.controllers', ['ngRoute', 'ui.bootstrap', 'ui.validate', 'myApp.services']);

controllersModule.controller('NavCtrl', ['$scope', '$routeParams', '$location', 'ModelSvc',
    function ($scope, $routeParams, $location, ModelSvc) {
        $scope.navClass = function (page, secondaryPage) {
            var currentRoute = $location.path().substring(1);

            var result = page === currentRoute ? 'active' : '';
            if (secondaryPage && $scope.disableSecondaryPages()) {
                result = 'disabled';
            }

            return result;
        };

        $scope.disableSecondaryPages = function () {
            if (ModelSvc.model.company.connectedToQbo &&
                ModelSvc.model.company.employeesSynced &&
                ModelSvc.model.company.customersSynced &&
                ModelSvc.model.company.serviceItemsSynced) {
                return false;
            } else {
                return true;
            }
        };

        $scope.navChange = function (event) {
            if ($scope.disableSecondaryPages()) {
                event.preventDefault();
            }
        };

        $scope.model = ModelSvc.model;
    }]);


controllersModule.controller('SettingsCtrl',
    ['$scope', 'SyncRequestSvc', 'ModelSvc', 'CompanySvc', 'BusyModalSvc', 'DeepLinkSvc', '$window', 'TrackingSvc',
        function ($scope, SyncRequestSvc, ModelSvc, CompanySvc, BusyModalSvc, DeepLinkSvc, $window, TrackingSvc) {

        TrackingSvc.trackPage('setup');

        $scope.model = ModelSvc.model;

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

            var disableViewInQBOButton = function (entitySynced) {
                if (connectedToQBO()) {
                    //we can synced
                    if (entitySynced) {
                        //don't disable the view button
                        return false;
                    } else {
                        //do disable the view button
                        return true;
                    }
                } else {
                    //we can't view, disable the button
                    return true;
                }
            };

        var self = this;

        $scope.disableCustomersSyncButton = function () {
            return disableSyncButton($scope.model.company.customersSynced);
        };

        $scope.disableServiceItemsSyncButton = function () {
            return disableSyncButton($scope.model.company.serviceItemsSynced);
        };

        $scope.disableEmployeeSyncButton = function () {
            return disableSyncButton($scope.model.company.employeesSynced);
        };

            $scope.disableViewEmployeesInQBOButton = function () {
                return disableViewInQBOButton($scope.model.company.employeesSynced);
            };

            $scope.disableViewCustomersInQBOButton = function () {
                return disableViewInQBOButton($scope.model.company.customersSynced);
            };

            $scope.disableViewItemsInQBOButton = function () {
                return disableViewInQBOButton($scope.model.company.serviceItemsSynced);
            };

        $scope.syncCustomers = function () {
            SyncRequestSvc.sendCustomerSyncRequest(self.syncCompleted);
            $scope.busyModal = BusyModalSvc.openBusyModal();
        };

        $scope.syncServiceItems = function () {
            SyncRequestSvc.sendServiceItemsSyncRequest(self.syncCompleted);
            $scope.busyModal = BusyModalSvc.openBusyModal();
        };

        $scope.syncEmployees = function () {
            SyncRequestSvc.sendEmployeeSyncRequest(self.syncCompleted);
            $scope.busyModal = BusyModalSvc.openBusyModal();
        };

        this.syncCompleted = function (data, status, headers, config) {
            CompanySvc.initializeModel();
            BusyModalSvc.closeBusyModal($scope.busyModal);
        };

            $scope.openEmployeesScreenInQBO = function () {
                $window.open(DeepLinkSvc.getEmployeesLink());
            };

            $scope.openCustomersScreenInQBO = function () {
                $window.open(DeepLinkSvc.getCustomersLink());
            };

            $scope.openItemsScreenInQBO = function () {
                $window.open(DeepLinkSvc.getItemsLink());
            };
    }]);

controllersModule.controller('TimeEntryCtrl', ['$scope', '$filter', '$window',
    'ModelSvc', 'TimeActivitySvc', 'BusyModalSvc', 'DeepLinkSvc', 'TrackingSvc',
    function ($scope, $filter, $window, ModelSvc, TimeActivitySvc, BusyModalSvc, DeepLinkSvc, TrackingSvc) {

        TrackingSvc.trackPage('timeentry')

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
            BusyModalSvc.closeBusyModal($scope.busyModal);
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
            $scope.busyModal = BusyModalSvc.openBusyModal()
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

        $scope.openSalesScreenInQuickBooks = function (timeActivity) {
            $window.open(DeepLinkSvc.getSalesLink(timeActivity));
        };
    }]);

controllersModule.controller('InvoiceCtrl', ['$scope', "$window", 'ModelSvc', 'InvoiceSvc', 'BusyModalSvc', 'DeepLinkSvc', 'TrackingSvc',
    function ($scope, $window, ModelSvc, InvoiceSvc, BusyModalSvc, DeepLinkSvc, TrackingSvc) {
        TrackingSvc.trackPage('invoices');

        InvoiceSvc.getInvoices();


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
            $scope.busyModal = BusyModalSvc.openBusyModal();
        };

        $scope.openInvoiceInQBO = function (invoice) {
            $window.open(DeepLinkSvc.getInvoiceLink(invoice));
        };

        this.showSuccessfulAlert = function (result) {
            $scope.alertMessage = "Invoice successfully created and pushed to QBO (QBO ID = " + result.qboId + ")";
            TrackingSvc.trackPage('invoices_success');
            $scope.showAlert = true;
            BusyModalSvc.closeBusyModal($scope.busyModal);
        };
    }]);
