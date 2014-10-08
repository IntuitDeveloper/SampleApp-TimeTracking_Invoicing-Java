module.exports = function(config){
  config.set({

    basePath : '../',

    files : [
        'https://js.appcenter.intuit.com/Content/IA/intuit.ipp.anywhere.js',
        'app/bower_components/angular/angular.js',
        'app/bower_components/angular-route/angular-route.js',
        'app/bower_components/angular-mocks/angular-mocks.js',
        'app/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
        'app/bower_components/angular-resource/angular-resource.js',
        'app/bower_components/angular-ui-utils/validate.js',
        'app/js/**/*.js',
        'test/unit/**/*.js'
    ],

    autoWatch : true,

    frameworks: ['jasmine'],

    browsers : ['PhantomJS'],

    plugins : [
            'karma-phantomjs-launcher',
            'karma-jasmine',
            'karma-junit-reporter',
            'karma-coverage'
            ],

    reporters : ['dots', 'junit', 'coverage'],
    junitReporter : {
        outputFile: 'unit-test-results.xml'
    },

    preprocessors: {
          'app/js/**/*.js': ['coverage']
    }


  });
};
