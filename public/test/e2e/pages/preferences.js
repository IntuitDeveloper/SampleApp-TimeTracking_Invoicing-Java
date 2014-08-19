var PreferencesPage = (function () {
    function PreferencesPage() {
        this.connectToQBOText = element(by.css('#connect-to-qbo-div p'));
        this.connectToQBOButton = element(by.css('#connect-to-qbo-div a.intuitPlatformConnectButton'));
    }

    return PreferencesPage;

})();

module.exports = PreferencesPage;