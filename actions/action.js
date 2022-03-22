"use strict";
let datafire = require('datafire');

let linkedin = require('@datafire/linkedin').actions;
module.exports = new datafire.Action({
  handler: async (input, context) => {
    let result = await Promise.all([].map(item => linkedin.companies.get({}, context)));
    return result;
  },
});
