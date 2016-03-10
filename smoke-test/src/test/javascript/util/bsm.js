'use strict';

var ADMIN_PAGE_URL = '/admin/bsm/adminpage.jsp';

function AdminPageEditWindow(adminPage) {
	var self = this;

	self.adminPage = adminPage;
	self.opennms = adminPage.opennms;
	self.casper = self.opennms.casper;
	self.doLog = function(ssname) {
		self.casper.then(function() {
			self.opennms.takeScreenshot('AdminPageEditWindow.' + ssname);
		});
	};

	return self;
}
AdminPageEditWindow.prototype.name = function(serviceName) {
	var self = this;
	self.serviceName = serviceName;
	self.doLog('name.start');

	self.casper.waitForSelector('#nameField');
	self.casper.then(function() {
		this.sendKeys('#nameField', serviceName, {reset:true});
	});

	self.doLog('name.finish');
	return self;
};
AdminPageEditWindow.prototype.save = function() {
	var self = this;
	self.doLog('save.start');

	self.casper.waitForSelector('#saveButton');
	self.casper.then(function() {
		this.click('#saveButton');
	});
	self.casper.waitForText(self.serviceName);

	self.doLog('save.finish');
	return new AdminPage(self.opennms).open();
};
AdminPageEditWindow.prototype.setReductionFunction = function(func) {
	var self = this;
	self.doLog('setReductionFunction.start');

	self.casper.waitForSelector('#reduceFunctionNativeSelect select');
	self.opennms.selectByValue('#reduceFunctionNativeSelect select', func);

	self.doLog('setReductionFunction.finish');
	return self;
};
AdminPageEditWindow.prototype.setThreshold = function(thresh) {
	var self = this;
	self.doLog('setThreshold.start');

	self.casper.waitForSelector('#thresholdTextField');
	self.casper.then(function() {
		this.sendKeys('#thresholdTextField', thresh, {reset:true});
	});

	self.doLog('setThreshold.finish');
	return self;
};
AdminPageEditWindow.prototype.newEdgeWindow = function() {
	var self = this;
	self.doLog('newEdgeWindow.start');

	self.casper.waitForSelector('#addEdgeButton');
	self.casper.then(function() {
		self.casper.click('#addEdgeButton');
	});
	self.casper.waitForText('Business Service Edge Edit');

	self.doLog('newEdgeWindow.finish');
	return new AdminPageEdgeEditWindow(self);
};

function AdminPageEdgeEditWindow(adminPageEditWindow) {
	var self = this;
	self.opennms = adminPageEditWindow.opennms;
	self.casper = adminPageEditWindow.casper;
	self.doLog = function(ssname) {
		self.casper.then(function() {
			self.opennms.takeScreenshot('AdminPageEdgeEditWindow.' + ssname);
		});
	};
	return self;
};
AdminPageEdgeEditWindow.prototype.selectEdgeType = function(type) {
	var self = this;
	self.doLog('selectEdgeType.start');

	self.casper.waitForSelector('#edgeTypeSelector select');
	self.opennms.selectByValue('#edgeTypeSelector select', type);

	self.doLog('selectEdgeType.finish');
	return self;
};

function AdminPage(opennms) {
	var self = this;
	self.opennms = opennms;
	self.casper = self.opennms.casper;
	self.doLog = function(ssname) {
		self.casper.then(function() {
			self.opennms.takeScreenshot('AdminPage.' + ssname);
		});
	};
	return self;
}
AdminPage.prototype.openNewDialog = function(serviceName) {
	var self = this;
	self.doLog('openNewDialog.start');

	self.casper.waitForSelector('#createButton');
	self.casper.then(function() {
		this.click('#createButton');
	});
	self.casper.waitForText('Business Service Edit');

	self.doLog('openNewDialog.finish');
	return new AdminPageEditWindow(self).name(serviceName);
};
AdminPage.prototype.openEditDialog = function(serviceName) {
	var self = this;
	self.doLog('openEditDialog.start');

	self.casper.waitForSelector('#editButton-' + serviceName);
	self.casper.then(function() {
		self.casper.click('#editButton-' + serviceName);
	});
	self.casper.waitForText('Business Service Edit');

	self.doLog('openEditDialog.finish');
	return new AdminPageEditWindow(self).name(serviceName);
};
AdminPage.prototype.open = function() {
	var self = this;
	self.doLog('open.start');

	self.casper.thenOpen(self.opennms.root() + ADMIN_PAGE_URL);
	self.casper.waitForSelector('div[id="content"] > iframe[src="osgi/bsm-admin-page"]');
	self.casper.then(function() {
		self.casper.page.switchToFrame(0);
	});

	self.doLog('open.finish');
	return self;
};
AdminPage.prototype.rename = function(oldName, newName) {
	var self = this;
	self.doLog('rename.start');

	self.openEditDialog(oldName).name(newName).save();

	self.doLog('rename.finish');
	return self;
};
AdminPage.prototype.delete = function(serviceName, withConfirmDialog) {
	var self = this;
	self.doLog('delete.start');

	var deleteButton = '#deleteButton-' + serviceName;
	self.casper.waitForSelector(deleteButton);
	self.casper.then(function() {
		this.click(deleteButton);
	});
	if (withConfirmDialog) {
		self.casper.waitForSelector('#confirmationDialog.button.ok');
		self.casper.then(function() {
			this.click('#confirmationDialog.button.ok');
		});
	}
	self.casper.waitWhileVisible(deleteButton);

	self.doLog('delete.finish');
	return self;
};

function BSM(opennms) {
	var self = this;
	self.opennms = opennms;
	self.casper = opennms.casper;
	self.adminPage = new AdminPage(opennms);
	self.doLog = function(ssname) {
		self.casper.then(function() {
			self.opennms.takeScreenshot('BSM.' + ssname);
		});
	};
}
BSM.prototype.createTestSetup = function() {
	var self = this;
	self.doLog('createTestSetup.start');

	self.opennms.setForeignSource('bsm', {
		name: 'bsm',
		'scan-interval': '1d'
	});
	self.opennms.createOrReplaceRequisition('bsm', {
		node: [
			{
				'foreign-id': 'NodeA',
				'node-label': 'NodeA',
				status: 1,
				interface: [{
					'ip-addr': '::1',
					'snmp-primary': 'N',
					'monitored-service': [{
						'service-name': 'AAA'
					},{
						'service-name': 'BBB'
					}]
				},{
					'ip-addr': '127.0.0.1',
					'snmp-primary': 'N',
					'monitored-service': [{
						'service-name': 'CCC'
					},{
						'service-name': 'DDD'
					}]
				}]
			}
		]
	});
	self.opennms.importRequisition('bsm');
	self.opennms.assertRequisitionImported('bsm');

	self.doLog('createTestSetup.finish');
};

module.exports = function(opennms) {
	return new BSM(opennms);
};