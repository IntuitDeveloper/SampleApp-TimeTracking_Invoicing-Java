"use strict";
let datafire = require('datafire');

let hubspot = require('@datafire/hubspot').actions;
module.exports = new datafire.Action({
  handler: async (input, context) => {
    let result = await Promise.all([].map(item => hubspot.getCompanyByDomain({
      domain: "",
    }, context)));
    return result;
  },
});
