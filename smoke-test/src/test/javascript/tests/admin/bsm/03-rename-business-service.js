'use strict';

var opennms = require('../../util/opennms')(casper),
	utils = require('utils'),
	uuid = require('node-uuid');

var bsm = require('../../util/bsm')(opennms);

casper.test.begin('Business Services: Rename Business Service', 4, {
	setUp: function() {
		opennms.initialize();
		opennms.login();
		opennms.ensureNoRequisitions();
		bsm.adminPage.open();
	},

	test: function(test) {
		var serviceName = uuid.v4();
		var renamed = 'renamed-' + uuid.v4();

		// create the service
		bsm.adminPage.openNewDialog(serviceName).save();
		casper.then(function() {
			casper.test.assertExists('#deleteButton-' + serviceName, 'Delete button for service ' + serviceName + ' should exist.');
		});

		// rename the service
		bsm.adminPage.rename(serviceName, renamed);
		casper.then(function() {
			casper.test.assertDoesntExist('#deleteButton-' + serviceName, 'Delete button for service ' + serviceName + ' should not exist.');
			casper.test.assertExists('#deleteButton-' + renamed, 'Delete button for service ' + renamed + ' should exist.');
		});

		bsm.adminPage.delete(renamed);
		casper.then(function() {
			casper.test.assertDoesntExist('#deleteButton-' + renamed, 'Delete button for service ' + renamed + ' should not exist.');
		});

		opennms.finished(test);
	}
});