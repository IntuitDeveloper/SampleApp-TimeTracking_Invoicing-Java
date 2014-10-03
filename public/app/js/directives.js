'use strict';

var directivesModule = angular.module('myApp.directives', []);

directivesModule.directive('invoiceList', function () {
    return {
        restrict: 'E',
        scope: {
            invoiceList: "=list",
            invoiceStatus: "@",
            actionCallback: "="

        },
        controller: ["$scope", function ($scope) {

            $scope.actionButtonIcon = function () {
                if ($scope.invoiceStatus === 'Invoiced') {
                    return "glyphicon glyphicon-eye-open"
                } else {
                    return "glyphicon glyphicon-send"
                }
            };

            $scope.actionButtonText = function () {
                if ($scope.invoiceStatus === 'Invoiced') {
                    return "View in QuickBooks"
                } else {
                    return "Generate"
                }
            };

            $scope.expandServiceItemSummary = function (invoice) {
                if (invoice.showServiceItemSummaries) {
                    invoice.showServiceItemSummaries = !invoice.showServiceItemSummaries;
                } else {
                    invoice.showServiceItemSummaries = true;
                }
            };

            $scope.expandButtonIcon = function (invoice) {
                if (invoice.showServiceItemSummaries) {
                    return "glyphicon glyphicon-minus-sign"
                } else {
                    return "glyphicon glyphicon-plus-sign"
                }
            };


        }],
        templateUrl: 'partials/invoiceList.html'
    }
});