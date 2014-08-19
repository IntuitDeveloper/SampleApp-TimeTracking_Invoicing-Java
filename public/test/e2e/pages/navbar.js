var Navbar = (function () {
    function Navbar() {
        this.activeNavbarButtons = element.all(by.css('#navbar-page-buttons li.active'));
        this.companyNameNavbarButton = element(by.css('#navbar-dropdown-button li.dropdown a.dropdown-toggle'));
    }

    Navbar.prototype.assertActiveNavbarButtons = function(expectedButtonText) {
        expect(this.activeNavbarButtons.count()).toEqual(1);
        expect(this.activeNavbarButtons.first().getText()).toBe(expectedButtonText);
    };

    return Navbar;

})();

module.exports = Navbar;