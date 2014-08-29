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
