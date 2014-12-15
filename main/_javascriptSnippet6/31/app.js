(function () {
	var app = angular.module('projectsModule', ['ui.router']);
	app.config(function ($stateProvider, $urlRouterProvider) {

		$urlRouterProvider.otherwise('/home');

		$stateProvider

		// Home state
		.state('home', {
			url : '/home',
			templateUrl : 'home.html'
		})

		// Projects Abstract state
		.state('projects', {
			url : '/projects',
			abstract : true,
			template : '<div ui-view="" ng-controller="ProjectsListCtrl"></div>'
		})

		//Projects List state
		.state('projects.list', {
			url : '/list',
			templateUrl : 'projects-list.html'
		})

		//New Project state
		.state('projects.new', {
			url : '/new',
			templateUrl : 'project-form.html',
			controller : 'ProjectCtrl'
		})

		//Project Details state
		.state('projects.project', {
			url : '/:ProjectNO',
			templateUrl : 'project-details.html',
			controller : 'ProjectCtrl'
		});

	});

	app.controller('ProjectsListCtrl', function ($http, $scope, $state, $stateParams) {

		$scope.printProjectsButton = 'Print'; //Initializer for print projects

		//vars for data
		$scope.selectedProject = {};
		$scope.selectedProjectIndex = 0;
		$scope.maxProjectNO = 0;
		$scope.projects = [];
		$scope.gridColumns = [];
		$scope.customers = [];
		$scope.salespeople = [];
		$scope.labmembers = [];
		$scope.employees = [];
		$scope.loading = true;
		$scope.error = false;

		//Initial Sort
		$scope.sortColumn = 'ProjectNO';
		$scope.reverse = true;

		//Load data from Projects JSON, will fill the rows of each table
		$http.get('/data/api/Projects').success(function (data) {
			//data = eval("(" + data + ")");
			$scope.projects = data;
		});

		//Load data from gridColumns JSON to be used for <table> headers
		$http.get('/data/Content/gridcolumns.json').success(function (data) {
			$scope.gridColumns = data;
		});

		//load customers
		$http.get('/data/api/Customers/').success(function (data) {
			$scope.customers = data;
		});

		//load lab and salespeople
		$http.get('/data/api/Employees/').success(function (data) {
			$.each(data, function (index, e) {
				if (e.status) {
					if (e.department_name == 'Sales') {
						$scope.salespeople.push(e);
					} else if (e.department_name == 'Lab') {
						$scope.labmembers.push(e);
					}
				}
				$scope.employees.push(e);
			$scope.loading = false;
			});
		});
		

		//Print Projects List
		$scope.printProjects = function () {

			//Hide toolbar and styling of some columns
			$('#toolbar').css({
				'display' : 'none'
			});
			$('.btnProj').removeClass('btn btn-info');
			$('.unshipped').removeClass('btn btn-success glyphicon glyphicon-send');

			//HTML from div stored in a variable
			var DocumentContainer = $('#divProjects').html();
			var html = '<html><head>' +
				'<link href="bootstrap.min.css" rel="stylesheet" type="text/css" />' +
				'</head><body style="background:#ffffff;">' +
				DocumentContainer +
				'</body></html>';

			//Print Window
			var WindowObject = window.open("", "PrintWindow",
					"width=1366,height=768,top=50,left=50,toolbars=no,scrollbars=yes,status=no,resizable=yes");
			WindowObject.document.writeln(html);
			WindowObject.document.close();
			WindowObject.focus();
			WindowObject.print();
			WindowObject.close();

			//Restore toolbar and styling of some columns
			$('#toolbar').css({
				'display' : 'table-row'
			});
			$('.btnProj').addClass('btn btn-info');
			$('.unshipped').removeClass('btn btn-success glyphicon glyphicon-send');

		}

		//Excel Export function
		$scope.projectsXLS = function () {

			//Hide toolbar and styling of some columns
			$('#toolbar').css({
				'display' : 'none'
			});
			$('.btnProj').removeClass('btn btn-info');
			$('.unshipped').removeClass('btn btn-success glyphicon glyphicon-send');

			var DocumentContainer = $('#divProjects').html();
			var html = '<html><head>' +
				'</head><body style="background:#ffffff;">' +
				DocumentContainer +
				'</body></html>';

			//Check if using Internet Explorer, use ActiveX method
			if (navigator.userAgent.indexOf('MSIE') !== -1 || navigator.appVersion.indexOf('Trident/') > 0) {
				window.clipboardData.setData("Text", html);
				var objExcel = new ActiveXObject("Excel.Application");
				objExcel.visible = true;
				var objWorkbook = objExcel.Workbooks.Add;
				var objWorksheet = objWorkbook.Worksheets(1);
				objWorksheet.Paste;
			}

			//Check if using any other browser and use DataURI method
			else {
				window.open('data:application/vnd.ms-excel,' + encodeURIComponent(html));
			}

			//Restore toolbar and styling of some columns
			$('#toolbar').css({
				'display' : 'table-row'
			});
			$('.btnProj').addClass('btn btn-info');
			$('.unshipped').removeClass('btn btn-success glyphicon glyphicon-send');
		}
	});

	//Project Controller
	app.controller('ProjectCtrl', function ($scope, $stateParams, $http) {

		//Initialize some variables for project details use
		$scope.qcInit = false;
		$scope.customerName = null;
		$scope.employeeName = null;
		$scope.assignedLabMember = null;
		$scope.shipments = null;
		$scope.ShipDate = null;
		$scope.error = false;
		//Checks if parameters contains anything useful before loading Project Details page
		//Otherwise ignored to save API requests
		if ($stateParams.ProjectNO) {
			$http.get('/data/api/Projects/' + $stateParams.ProjectNO).success(function (data) {
				$scope.selectedProject = data;
				$scope.ShipDate = null;
				//Retrieve Customer Name
				$.each($scope.customers, function (index, c) {
					if (c.customerID == $scope.selectedProject.CustomerID) {
						$scope.customerName = c.name;
					}
				});

				//Retrieve Employee full name who requested project
				$.each($scope.employees, function (index, c) {
					//Requestor
					if ($.trim(c.username) == $.trim($scope.selectedProject.RequestBy)) {
						$scope.employeeName = $.trim(c.first_name) + ' ' + $.trim(c.last_name);
					}
					//Assigned To
					if ($.trim(c.username) == $.trim($scope.selectedProject.AssignedTo)) {
						$scope.assignedLabMember = $.trim(c.first_name) + ' ' + $.trim(c.last_name);
					}
				});

				$http.get('/data/api/ProjectShipments', function (data) {
					$.each(data, function (index, c) {
						if (c.projectNO == $stateParams.ProjectNO) {
							$scope.ShipDate = c.ShipDate;
						}
					});
				});

			});
		}

		//Get Index of Project from Array
		var index = $scope.projects.indexOf($scope.selectedProject.ProjectNO);
		//Start Production of Project
		$scope.startProduction = function () {
			$scope.selectedProject.Status = 'In Production';
		};

		//Start QC for Project function
		$scope.startQC = function () {
			//Set as QC processing
			if ($scope.selectedProject.Status === 'QC in Progress') {
				$scope.qcInit = false;
				$scope.selectedProject.Status = 'Ready to Ship';
			} else {
				$scope.qcInit = true;
				$scope.selectedProject.Status = 'QC in Progress';
			}
			
		};

		//Add Project
		$scope.project = {};
		$scope.addProject = function ($state) {

			//load projects array from parent scope
			$scope.$parent.$projects = $scope.projects;

			//additional info to be added to project
			$scope.project.RequestDate = (new Date()).toJSON();
			$scope.project.ShipDate = null;
			$scope.project.Status = 'New';

			$http.post('/data/api/Projects/', $scope.project).success(function (data, status, headers, config) {
				$http.get('/data/api/Projects/GetMaxKey').success(function(data){
					$scope.project.ProjectNO = data + 1;
				});
				//push project in projects array
				$scope.projects.push($scope.project);
				//Empty out the project object
				$scope.project = {};
				$state.go('projects.list');
				window.location = "/#/projects/list";
				
			}).error(function (data, status, headers, config){
				$scope.error = true; //display error div
			});
		};

		//Delete Project function
		$scope.deleteProject = function () {
			$scope.projects.splice(index, 1); //delete project from array
			$scope.selectedProject = {}; //re-initialize variable
			window.location = "/#/projects/list";
		};

		//scope watcher if AssignedTo value changes
		$scope.$watch('selectedProject.AssignedTo',
			function (newValue, oldValue) {
			if (!newValue)
				return;
			if ($stateParams.ProjectNO) {
				$.each($scope.labmembers, function (index, c) {
					if (newValue == c.username) {
						alert('invoke');
						$scope.assignedLabMember = $.trim(c.first_name) + ' ' + $.trim(c.last_name);
					}
				});
			}
		}, true);

	});

	//Controller for project logs
	app.controller('LogCtrl', function ($scope, $stateParams, $http) {
		$scope.logs = [];
		if ($stateParams.ProjectNO) {
			$http.get('/data/api/ProjectOperations').success(function (data) {
				$.each(data, function (index, c) {
					if (c.ProjectNO == $stateParams.ProjectNO) {
						$scope.logs.push(c);
					}
				});
			});
		}
	});

	app.directive('projectLogs', function () {
		return {
			restrict : 'E',
			templateUrl : 'project-logs.html'
		};
	});
})();
