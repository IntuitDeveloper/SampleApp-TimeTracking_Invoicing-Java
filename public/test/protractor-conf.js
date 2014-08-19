exports.config = {
    allScriptsTimeout: 11000,

    specs: [
        'e2e/*.js'
    ],

    capabilities: {
        'browserName': 'chrome'
    },

    baseUrl: 'http://localhost:8000/app/',

    framework: 'jasmine',

    onPrepare: function () {
        // The require statement must be down here, since jasmine-reporters
        // needs jasmine to be in the global and protractor does not guarantee
        // this until inside the onPrepare function.
        require('jasmine-reporters');
        var jUnitXmlReporter = new jasmine.JUnitXmlReporter('', true, true, 'e2e-');
        jasmine.getEnv().addReporter(jUnitXmlReporter);

    },

    jasmineNodeOpts: {
        defaultTimeoutInterval: 30000
    }
};
