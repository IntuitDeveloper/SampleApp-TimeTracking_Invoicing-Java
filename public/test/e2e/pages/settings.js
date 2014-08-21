var SettingsPage = (function () {
    function SettingsPage() {
        this.connectToQBOText = element(by.css('#connect-to-qbo-div p'));
        this.connectToQBOButton = element(by.css('#connect-to-qbo-div a.intuitPlatformConnectButton'));
    }

    return SettingsPage;

})();

module.exports = SettingsPage;