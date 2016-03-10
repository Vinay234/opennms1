'use strict';

var opennms = require('../../util/opennms')(casper),
	utils = require('utils'),
	uuid = require('node-uuid');

var bsm = require('../../util/bsm')(opennms);

casper.test.begin('Business Services: Add IP Service Edge', 4, {
	setUp: function() {
		opennms.initialize();
		opennms.login();
		opennms.ensureNoRequisitions();
		bsm.createTestSetup();
		bsm.adminPage.open();
	},

	test: function(test) {
		var serviceName = uuid.v4();
		var renamed = 'renamed-' + uuid.v4();

		var editWindow = bsm.adminPage.openNewDialog(serviceName);
		var edgeEditWindow = editWindow.newEdgeWindow();

		edgeEditWindow.selectEdgeType('IP Service');

		var IP_SERVICE_1 = "NodeA /0:0:0:0:0:0:0:1 AAA";
		var IP_SERVICE_2 = "NodeA /0:0:0:0:0:0:0:1 BBB";
		var IP_SERVICE_3 = "NodeA /127.0.0.1 CCC";
		var IP_SERVICE_4 = "NodeA /127.0.0.1 DDD";

		casper.waitForText(IP_SERVICE_1);
		casper.waitForText(IP_SERVICE_2);
		casper.waitForText(IP_SERVICE_3);
		casper.waitForText(IP_SERVICE_4);


		/*
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
		*/

		opennms.finished(test);
	}
});