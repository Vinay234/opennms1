'use strict';

var opennms = require('../../util/opennms')(casper),
	utils = require('utils'),
	uuid = require('node-uuid');

var bsm = require('../../util/bsm')(opennms);

casper.test.begin('Business Services: Create And Delete Business Service', 2, {
	setUp: function() {
		opennms.initialize();
		opennms.login();
		opennms.ensureNoRequisitions();
		bsm.adminPage.open();
	},

	test: function(test) {
		var serviceName = uuid.v4();
		bsm.adminPage.openNewDialog(serviceName).save();
		casper.then(function() {
			casper.test.assertExists('#deleteButton-' + serviceName, 'Delete button for service ' + serviceName + ' should exist.');
		});
		bsm.adminPage.delete(serviceName);
		casper.then(function() {
			casper.test.assertDoesntExist('#deleteButton-' + serviceName, 'Delete button for service ' + serviceName + ' should not exist.');
		});

		opennms.finished(test);
	}
});