angular.module('formControls',['ngLocalizer', 'ngSanitize',])
	.value('realm', 'ABS') //needed for some controls


	//============================================================
	//
	//
	//============================================================
	//NOTE: Requires a user!
	.directive('kmNotes',  function ($http, $filter) {
	    return {
	        restrict: 'EAC',
	        templateUrl: '/afc_template/km-notes.html',
	        replace: true,
	        transclude: false,
	        require: "?ngModel",
	        scope: {
	            placeholder: "@",
	            binding: "=ngModel",
	            rows: '=',
	            required: "@"
	        },
	        link: function ($scope, $element, attrs, ngModelController) {
	            $scope.timestamp = Date.now();
	            $scope.skipLoad = false;
	            $scope.texts = [];
	            $scope.$watch('binding', $scope.load);
	            $scope.$watch('binding', function () {
	                ngModelController.$setViewValue($scope.binding);
	            });

	        },
            controller: ["$scope", function ($scope) {
	                //==============================
	                //
	                //==============================
	                $scope.load = function () {
	                    if ($scope.skipLoad) {
	                        $scope.skipLoad = false;
	                        return;
	                    }

	                    $http.get("/api/v2013/authentication/user/", { cache: true }).success(function (data) {
	                        $scope.user = data;
	                    });

	                    var oBinding = $scope.binding || [];

	                    $scope.texts = [];

	                    angular.forEach(oBinding, function (text, i) {
	                        $scope.texts.push({ value: text });
	                    });
	                };

	                //==============================
	                //
	                //==============================
	                $scope.remove = function (index) {
	                    $scope.texts.splice(index, 1);

	                    $scope.save();
	                };

	                //==============================
	                //
	                //==============================
	                $scope.save = function () {
	                    var oNewBinding = [];
	                    var oText = $scope.texts;

	                    angular.forEach(oText, function (text, i) {
	                        if ($.trim(text.value) != "")
	                            oNewBinding.push($.trim(text.value));
	                    });

	                    if ($scope.newtext) {
	                        if ($.trim($scope.newtext) != "") {
	                            var timestamp = $filter('date')(Date.now(), 'medium');
	                            oNewBinding.push("[ " + $scope.user.name + " | " + timestamp + " ] - " + $.trim($scope.newtext));
	                        }
	                    }

	                    $scope.binding = !$.isEmptyObject(oNewBinding) ? oNewBinding : undefined;
	                    $scope.skipLoad = true;
	                };

	                //==============================
	                //
	                //==============================
	                $scope.isRequired = function () {
	                    return $scope.required != undefined
                            && $.isEmptyObject($scope.binding);
	                }
	            }]
	        }
	    })




	//============================================================
	//
	//
	//============================================================
	.directive('kmTextboxMl', function ($http)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-inputtext-ml.html',
			replace: true,
			require : "?ngModel",
			scope: {
				placeholder: '@',
				ngDisabledFn : '&ngDisabled',
				binding    : '=ngModel',
				locales    : '=',
				rows       : '=',
				required   : "@",
				ngChange   : "&"
			},
			link: function ($scope, element, attrs, ngModelController)
			{
				$scope.text = {}
				$scope.$watch('locales', $scope.watchLocales);
				$scope.$watch('binding', $scope.watchBinding);
				$scope.$watch('binding', function() {
					try { ngModelController.$setViewValue($scope.binding); } catch(e) {}
				});

			},
			controller : ["$scope", function ($scope)
			{
				//==============================
				//Remove value of not selected languages/empty languages
				//==============================
				$scope.watchLocales = function()
				{
					var oLocales = $scope.locales || [];
					var oBinding = $scope.binding || {};
					var oText    = $scope.text;

					angular.forEach(oLocales, function(locale, i) {
						oText[locale] = oBinding[locale] || oText[locale]; });
				}

				//==============================
				//Remove value of not selected languages/empty languages
				//==============================
				$scope.watchBinding = function()
				{
					var oLocales = $scope.locales || [];
					var oBinding = $scope.binding || {};
					var oText    = $scope.text;

					angular.forEach(oLocales, function(locale, i) {
						oText[locale] = oBinding[locale]; });
				}

				//==============================
				//Remove value of not selected languages/empty languages
				//==============================
				$scope.onchange = function()
				{
					var oLocales    = $scope.locales || [];
					var oText       = $scope.text    || {};
					var oNewBinding = {};

					angular.forEach(oLocales, function(locale, i)
					{
						if($.trim(oText[locale])!="")
							oNewBinding[locale] = oText[locale];
					});

					$scope.binding = !$.isEmptyObject(oNewBinding) ? oNewBinding : undefined;
					$scope.ngChange();
				}

				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}

				//==============================
				//
				//==============================
				$scope.isShowLocale = function()
				{
					return $scope.locales && $scope.locales.length>1
				}
			}]
		};
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmRichTextbox', function ($http)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-rich-textbox.html',
			replace: true,
			require : "?ngModel",
			scope: {
				placeholder: '@',
				ngDisabledFn : '&ngDisabled',
				binding    : '=ngModel',
				locales    : '=',
				rows       : '=',
				required   : "@",
				ngChange   : "&"
			},
			link: function ($scope, element, attrs, ngModelController)
			{
				$scope.text = {}
				$scope.$watch('locales', $scope.watchLocales);
				$scope.$watch('binding', $scope.watchBinding);
				$scope.$watch('binding', function() {
					try { ngModelController.$setViewValue($scope.binding); } catch(e) {}
				});

			},
			controller : ["$scope", function ($scope)
			{
				//==============================
				//Remove value of not selected languages/empty languages
				//==============================
				$scope.watchLocales = function()
				{
					var oLocales = $scope.locales || [];
					var oBinding = $scope.binding || {};
					var oText    = $scope.text;

					angular.forEach(oLocales, function(locale, i) {
						oText[locale] = oBinding[locale] || oText[locale]; });
				}

				//==============================
				//Remove value of not selected languages/empty languages
				//==============================
				$scope.watchBinding = function()
				{
					var oLocales = $scope.locales || [];
					var oBinding = $scope.binding || {};
					var oText    = $scope.text;

					angular.forEach(oLocales, function(locale, i) {
						oText[locale] = oBinding[locale]; });
				}

				//==============================
				//Remove value of not selected languages/empty languages
				//==============================
				$scope.onchange = function()
				{
					var oLocales    = $scope.locales || [];
					var oText       = $scope.text    || {};
					var oNewBinding = {};

					angular.forEach(oLocales, function(locale, i)
					{
						if($("<i>").html(oText[locale]).text().trim()!="")
							oNewBinding[locale] = oText[locale];
					});

					$scope.binding = !$.isEmptyObject(oNewBinding) ? oNewBinding : undefined;
					$scope.ngChange();
				}

				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}

				//==============================
				//
				//==============================
				$scope.isShowLocale = function()
				{
					return $scope.locales && $scope.locales.length>1
				}
			}]
		};
	})


	//============================================================
	//
	//
	//============================================================
	.directive('kmInputtextList', function ($http)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-inputtext-list.html',
			replace: true,
			transclude: false,
			require : "?ngModel",
			scope: {
				placeholder : "@",
				binding     : "=ngModel",
				type        : "@type",
				required    : "@"
			},
			link: function ($scope, $element, attrs, ngModelController)
			{
				$scope.skipLoad = false;
				$scope.texts    = [];
				$scope.$watch('binding', $scope.load);
				$scope.$watch('binding', function() {
					ngModelController.$setViewValue($scope.binding);
				});
			},
			controller: ["$scope", function ($scope)
			{
				//==============================
				//
				//==============================
				$scope.load = function ()
				{
					if($scope.skipLoad)
					{
						$scope.skipLoad = false;
						return;
					}

					var oBinding = $scope.binding || [];

					$scope.texts = [];

					angular.forEach(oBinding, function(text, i)
					{
						$scope.texts.push({value : text});
					});
				};

				//==============================
				//
				//==============================
				$scope.remove = function (index)
				{
					$scope.texts.splice(index, 1);

					$scope.save();
				};

				//==============================
				//
				//==============================
				$scope.save = function ()
				{
					var oNewBinding = [];
					var oText       = $scope.texts;

					angular.forEach(oText, function(text, i)
					{
						if($.trim(text.value)!="")
							oNewBinding.push($.trim(text.value));
					});

					$scope.binding  = !$.isEmptyObject(oNewBinding) ? oNewBinding : undefined;
					$scope.skipLoad = true;
				};

				//==============================
				//
				//==============================
				$scope.getTexts = function ()
				{
					if($scope.texts.length==0)
						$scope.texts.push({value : ""});

					var sLastValue = $scope.texts[$scope.texts.length-1].value;

					//NOTE: IE can set value to 'undefined' for a moment
					if(sLastValue && sLastValue!="")
						$scope.texts.push({value : ""});

					return $scope.texts;
				};

				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}
			}]
		};
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmTerms', function ($http)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-terms.html',
			replace: true,
			scope: {
				binding : '=ngModel',
			},
			link: function ($scope, element, attrs, controller)
			{
				$scope.termsX = [];
				$scope.terms = [];
				$scope.$watch('binding', $scope.load);
			},
			controller: ["$scope", "$q", function ($scope, $q)
			{
				//==============================
				//
				//==============================
				$scope.load = function()
				{
					$scope.terms = [];
					var oBinding = null;

						  if($scope.binding && angular.isArray ($scope.binding)) oBinding =  $scope.binding;
					else if($scope.binding && angular.isObject($scope.binding)) oBinding = [$scope.binding];
					else if($scope.binding && angular.isString($scope.binding)) oBinding = [$scope.binding];

					$scope.termsX = oBinding;
					console.log('oBinding: ', $scope.terms);
					return;


					if(oBinding) {
						var qTerms = [];

						angular.forEach(oBinding, function(value, key) {
							if(value.name)
								qTerms.push(value);
							else {

								var identifier = null;

								if(angular.isString(value))
									identifier = value;
								else
									identifier = value.identifier;

								qTerms.push($http.get("/api/v2013/thesaurus/terms/"+encodeURI(identifier),  {cache:true}).then(function(o) {
									return _.extend(_.clone(o.data),  _.omit(value, "identifier", "title"));
								}));
							}
						});

						$q.all(qTerms).then(function(terms) {
							$scope.terms = terms;
						});
					}
				}
			}]
		}
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmTermCheck', function ($http) {
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-terms-check.html',
			replace: true,
			transclude: false,
			require : "?ngModel",
			scope: {
				binding     : '=ngModel',
				bindingType : '@',
				termsFn     : '&terms',
				required    : "@",
				layout      : "@"
			},
			link: function ($scope, $element, $attr, ngModelController)
			{
				$scope.identifiers = null;
				$scope.terms       = null;
				$scope.rootTerms   = [];

				$scope.$watch('terms',      $scope.onTerms);
				$scope.$watch('identifier', $scope.save);
				$scope.$watch('binding',    $scope.load);
				$scope.$watch('binding', function() {
					ngModelController.$setViewValue($scope.binding);
				});

				$scope.init();

				if(!$attr["class"])
					$element.addClass("list-unstyled");

			},
			controller: ["$scope", "$q", "Thesaurus", '$timeout', function ($scope, $q, thesaurus, $timeout)
			{
				//==============================
				//
				//==============================
				$scope.init = function() {
					$scope.setError(null);
					$scope.__loading = true;

					var qData = $scope.termsFn();

					if(qData==undefined)
						$timeout($scope.init, 250); // MEGA UGLY PATCH

					$q.when(qData,
						function(data) { // on success
							$scope.__loading = false;
							$scope.terms     = data;
						}, function(error) { // on error
							$scope.__loading = false;
							$scope.setError(error);
						});
				}

				//==============================
				//
				//==============================
				$scope.load = function()
				{
					if (!$scope.terms) // Not initialized
						return;

					var oNewIdentifiers = {};

					if(!$.isArray($scope.terms))
						throw "Type must be array";

					if($scope.binding) {

						if(!$.isArray($scope.binding))
							throw "Type must be array";

						for(var i=0; i<$scope.binding.length; ++i)
						{
								  if($scope.bindingType=="string[]") oNewIdentifiers[$scope.binding[i]           ] = true;
							else if($scope.bindingType=="term[]")   oNewIdentifiers[$scope.binding[i].identifier] = true;
							else throw "bindingType not supported";
						}
					}

					if(!angular.equals(oNewIdentifiers, $scope.identifiers))
						$scope.identifiers = oNewIdentifiers;
				}

				//==============================
				//
				//==============================
				$scope.save = function()
				{
					if(!$scope.identifiers)
						return;

					var oNewBinding = [];

					angular.forEach($scope.terms, function(term, i)
					{
						if(term==undefined) return //IE8 BUG

						if($scope.identifiers[term.identifier])
						{
								  if($scope.bindingType=="string[]") oNewBinding.push(             term.identifier   );
							else if($scope.bindingType=="term[]"  ) oNewBinding.push({ identifier:term.identifier } );
							else throw "bindingType not supported";
						}
					});

					if($.isEmptyObject(oNewBinding))
						oNewBinding = undefined;

					if(!angular.equals(oNewBinding, $scope.binding))
						$scope.binding = oNewBinding;
				}

				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}

				//==============================
				//
				//==============================
				$scope.onTerms = function(refTerms) {

					$scope.rootTerms = [];

					if(refTerms)
					{
						if (($scope.layout||"tree") == "tree") //Default layout
							$scope.rootTerms = thesaurus.buildTree(refTerms);
						else
							$scope.rootTerms = Enumerable.from(refTerms).select("o=>{identifier : o.identifier, name : o.name, title : o.title}").toArray();
					}

					$scope.load();
				}

				//==============================
				//
				//==============================
				$scope.setError = function(error) {
					if (!error) {
						$scope.error = null;
						return;
					}

					if (error.status == 404) $scope.error = "Terms not found";
					else                     $scope.error = error.data || "unkown error";
				}
			}]
		}
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmTermRadio', function ($http) {
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-terms-radio.html',
			replace: true,
			transclude: false,
			require : "?ngModel",
			scope: {
				binding     : '=ngModel',
				//bindingName : '@ngModel',
				bindingType : '@',
				termsFn     : '&terms',
				description : "=",
				layout      : "@",
				required    : "@"
			},
			link: function ($scope, $element, $attr, ngModelController)
			{

				$scope.description = true;
				$scope.selection = null;
				$scope.terms     = null;
				$scope.rootTerms   = [];

				$scope.$watch('terms',     $scope.onTerms);
				$scope.$watch('selection', $scope.save);
				$scope.$watch('binding',   $scope.load);
				$scope.$watch('binding', function() {
					ngModelController.$setViewValue($scope.binding);
				});

				$scope.init();

				if(!$attr["class"])
					$element.addClass("list-unstyled");
			},
			controller: ["$scope", "$q", "Thesaurus", function ($scope, $q, thesaurus)
			{
				//==============================
				//
				//==============================
				$scope.init = function() {
					$scope.setError(null);
					$scope.__loading = true;

					$q.when($scope.termsFn(),
						function(data) { // on success
							$scope.__loading = false;
							$scope.terms     = data;
						}, function(error) { // on error
							$scope.__loading = false;
							$scope.setError(error);
						});
				}

				//==============================
				//
				//==============================
				$scope.load = function()
				{
					if (!$scope.terms) // Not initialized
						return;

					var oNewSelection = {};

					if(!$.isArray($scope.terms))
						throw "Type must be array";

					if($scope.binding) {

						if($.isArray($scope.binding))
							throw "Type cannot be an array";

							 if($scope.bindingType=="string") oNewSelection = { identifier : $scope.binding };
						else if($scope.bindingType=="term")   oNewSelection = { identifier : $scope.binding.identifier };
						else throw "bindingType not supported";
					}

					if(!angular.equals(oNewSelection, $scope.selection))
						$scope.selection = oNewSelection;
				}

				//==============================
				//
				//==============================
				$scope.save = function()
				{
					//debugger;

					if(!$scope.selection)
						return;

					var oNewBinding = {};

					if($scope.selection && $scope.selection.identifier)
					{
							 if($scope.bindingType=="string") oNewBinding = $scope.selection.identifier;
						else if($scope.bindingType=="term"  ) oNewBinding = { identifier : $scope.selection.identifier };
						else throw "bindingType not supported";
					}

					if(!angular.equals(oNewBinding, $scope.binding))
						$scope.binding = oNewBinding;

					if($.isEmptyObject($scope.binding))
						$scope.binding = undefined;
				}

				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}

				//==============================
				//
				//==============================
				$scope.onTerms = function(refTerms) {

					$scope.rootTerms = [];

					if(refTerms)
					{
						if (($scope.layout||"tree") == "tree") //Default layout
							$scope.rootTerms = thesaurus.buildTree(refTerms);
						else
							$scope.rootTerms = Enumerable.from(refTerms).select("o=>{identifier : o.identifier, name : o.name}").toArray();
					}

					$scope.load();
				}

				//==============================
				//
				//==============================
				$scope.setError = function(error) {
					if (!error) {
						$scope.error = null;
						return;
					}

					if (error.status == 404) $scope.error = "Terms not found";
					else                     $scope.error = error.data || "unkown error";
				}
			}]
		}
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmLink', function ($http, $q)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-link.html',
			replace: true,
			transclude: false,
			require : "?ngModel",
			scope: {
				binding    : '=ngModel',
				required   : "@",
				allowLink  : '@',
				allowFile  : '@',
				identifier : '=',
				mimeTypes  : "@",
				extensions : "="
			},
			link: function ($scope, $element, $attr, ngModelController)
			{
				// init
				$scope.links = [];
				$.extend($scope.editor, {
					link     : null,
					url      : null,
					name     : null,
					progress : null,
					error    : null,
					type     : null,
					uploadPlaceholder : $element.find("#uploadPlaceholder"),
					mimeTypes : [//	"application/octet-stream",
									"application/json",
									"application/ogg",
									"application/pdf",
									"application/xml",
									"application/zip",
									"audio/mpeg",
									"audio/x-ms-wma",
									"audio/x-wav",
									"image/gif",
									"image/jpeg",
									"image/png",
									"image/tiff",
									"text/csv",
									"text/html",
									"text/plain",
									"text/xml",
									"video/mpeg",
									"video/mp4",
									"video/quicktime",
									"video/x-ms-wmv",
									"video/x-msvideo",
									"video/x-flv",
									"application/vnd.oasis.opendocument.text",
									"application/vnd.oasis.opendocument.spreadsheet",
									"application/vnd.oasis.opendocument.presentation",
									"application/vnd.oasis.opendocument.graphics",
									"application/vnd.ms-excel",
									"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
									"application/vnd.ms-powerpoint",
									"application/msword",
									"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
								]
				});

				if ($attr.mimeTypes)
					$scope.editor.mimeTypes = $attr.mimeTypes.split(";");

				//Watchers
				$scope.$watch("binding", $scope.load);
				$scope.$watch('binding', function() {
					ngModelController.$setViewValue($scope.binding);
				});


				$scope.editor.show = function(visibility)
				{
					if (visibility) {
						$element.find($scope.editor.type == "file" ? "#editLink" : "#editFile").modal("hide");
						$element.find($scope.editor.type == "file" ? "#editFile" : "#editLink").modal("show");
					}
					else {
						$element.find("#editFile,#editLink").modal("hide");
					}

				}
			},
			controller: ["$scope", "IStorage", "$filter", function ($scope, storage, $filter)
			{
				$scope.editor = {};

				//==============================
				//
				//==============================
				$scope.isAllowLink   = function() { return $scope.allowLink!="false"; }
				$scope.isAllowFile   = function() { return $scope.allowFile!="false"; }

				//==============================
				//
				//==============================
				$scope.load = function()
				{
					var oNewLinks = [];

					angular.forEach($scope.binding || [], function(link, i) {

						oNewLinks.push(clone(link));
					});

					$scope.links = oNewLinks;
				}

				$scope.showModel = function(model, type){

					if(type=='file'){
						return $filter("term")(model);
					}
					else
						return model;
				}

				//==============================
				//
				//==============================
				$scope.save = function()
				{
					var oNewBinding = [];

					angular.forEach($scope.links, function(link, i)
					{
						var oNewLink = clone(link);

						oNewLink.url = $.trim(link.url);

						if(link.name && $.trim(link.name)!="")
							oNewLink.name = $.trim(link.name);

						//
						_.each(oNewLink, function(val, key) {

							if (!val)
								oNewLink[key] = undefined;
						});
						oNewBinding.push(oNewLink);
					});

					oNewBinding = _.compact(oNewBinding);

					$scope.binding = !$.isEmptyObject(oNewBinding) ? oNewBinding : undefined;
				}

				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}

				//==============================
				//
				//==============================
				$scope.addLink = function()
				{
					if(!this.isAllowLink())
						return;

					$scope.editor.editLink(null);
				}

				//==============================
				//
				//==============================
				$scope.addFile = function()
				{
					if(!$scope.isAllowFile())
						return;

					if(!$scope.identifier)
						throw "identifier not specified";

					$scope.editor.editFile(null);
				}

				//==============================
				//
				//==============================
				$scope.remove = function(link)
				{
					$scope.links.splice($scope.links.indexOf(link), 1);
					$scope.save();
				}

				//==============================
				//
				//==============================
				$scope.editor.editLink = function(link)
				{
					link = link || {url:"", name:""};

					$scope.editor.close();

					$scope.editor.link    = link;
					$scope.editor.url     = link.url;
					$scope.editor.name    = link.name;
					$scope.editor.extensions = clone(_.omit(link, "url", "name"))||{}
					$scope.editor.type    = "link";
					$scope.editor.show(true);
				}
				//==============================
				//
				//==============================
				$scope.editor.editFile = function(link)
				{
					if(link!=null)
						throw "Only new file is allowed"

					link = link || {url:"", name:""};

					$scope.editor.close();

					$scope.editor.link = link;
					$scope.editor.url  = link.url;
					$scope.editor.name = link.name;
					$scope.editor.extensions = clone(_.omit(link, "url", "name"))
					$scope.editor.type = "file";

					$scope.editor.startUploadProcess(function() {
						$scope.editor.show(true);
					})
				}

				//==============================
				//
				//==============================
				$scope.editor.close = function()
				{
					$scope.editor.link    = null;
					$scope.editor.url     = null;
					$scope.editor.name    = null;
					$scope.editor.error   = null;
					$scope.editor.extensions = null;
					$scope.editor.type    = null;
					$scope.editor.show(false);
				}

				//==============================
				//
				//==============================
				$scope.editor.save = function(type)
				{
					var oLink = { url:  $scope.editor.url };

					if($.trim($scope.editor.name||"")!="")
						oLink.name = $scope.editor.name;

					oLink = _.extend(oLink, clone($scope.editor.extensions));
					oLink.type= type;
					var iIndex = $scope.links.indexOf($scope.editor.link);

					if(iIndex>=0) $scope.links.splice(iIndex, 1, oLink);
					else          $scope.links.push  (oLink);

					$scope.editor.close();
					$scope.save();
				}

				//==============================
				//
				//==============================
				$scope.editor.startUploadProcess = function(onStartCallback)
				{
					//Clear old <input[file] />;
					$scope.editor.progress = null;
					$scope.editor.uploadPlaceholder.children('input[type=file]').remove();
					$scope.editor.uploadPlaceholder.prepend("<input type='file' style='display:none' />");

					var qHtmlInputFile = $scope.editor.uploadPlaceholder.children("input[type=file]:first");

					qHtmlInputFile.change(function()
					{
						var file = this.files[0];
						var type = storage.attachments.getMimeType(file);
						var link = {
							url: null,
							name: file.name
						};

						$scope.safeApply(function() {
							if (onStartCallback)
								onStartCallback();

							$scope.editor.link = link;

							if ($scope.editor.name == "" && file.name != "")
								$scope.editor.name = file.name;

							if ($scope.editor.mimeTypes.indexOf(type) < 0) {
								$scope.editor.onUploadError(link, "File type not supported: " + type);
								return;
							};

							$scope.editor.progress = {
								style: "active",
								position: 0,
								percent:100,
								size: file.size
							}

							if((file.size/(1024*1024)).toFixed(2)>20){

								$scope.editor.onUploadError(link, {'status' : 413});
								return;
							}
							storage.attachments.put($scope.identifier, file).then(
								function(result) { //success
									link.url = result.url;
									$scope.editor.onUploadSuccess(link, result.data);
								},
								function(result) { //error
									link.url = result.data.url;
									$scope.editor.onUploadError(link, result);
								},
								function(progress) { //progress
									$scope.editor.onUploadProgress(link, progress);
								});
						});
					});

					qHtmlInputFile.click();
				}


				//==============================
				//
				//==============================
				$scope.editor.onUploadProgress = function(link, progress)
				{
					if(!$scope.editor.progress)                 return;
					if( $scope.editor.progress.style!="active") return;
					if( $scope.editor.link !=link)              return;

					console.log('xhr.upload progress: ' + (progress*100) + "%")

					$scope.editor.progress.position = position;
					$scope.editor.progress.percent  = Math.round(progress*100);
				}

				//==============================
				//
				//==============================
				$scope.editor.onUploadSuccess = function(link, message)
				{
					if($scope.editor.link!=link)
						return;

					$scope.editor.url              = link.url;
					$scope.editor.progress.percent = 100;
					$scope.editor.progress.style   = "complete";

					if(link.name && $scope.editor.name!="")
						$scope.editor.name = link.name;

					if (!$scope.extensions || !$scope.extensions.length)
						$scope.editor.save();
				}

				//==============================
				//
				//==============================
				$scope.editor.onUploadError = function(link, message)
				{
					if($scope.editor.link!=link)
						return;

					console.log('xhr.upload error: ' , message)
					if(message.status == 401)
						$scope.editor.error = 'Unauthorised access, please make sure you are logged in.'
					else if(message.status == 413)
						$scope.editor.error = 'File too big, maxium file size allowed is 20 MB.'
					else
						$scope.editor.error = 'There was a error uploading your file, please try again.'
						//message.data;

					if($scope.editor.progress)
						$scope.editor.progress.style   = "error";
				}

				//====================
				//
				//====================
				$scope.safeApply = function(fn)
				{
					var phase = this.$root.$$phase;

					if (phase == '$apply' || phase == '$digest') {
						if (fn && (typeof (fn) === 'function')) {
							fn();
						}
					} else {
						this.$apply(fn);
					}
				};

				//====================
				//
				//====================
				function clone(obj) {
					if (obj === undefined) return undefined;
					if (obj === null) return null;

					return _.clone(obj);
				}

			}]
		}
	})


	//============================================================
	//
	//
	//============================================================
	.directive('kmReference', function ($http)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-reference.html',
			replace: false,
			transclude: true,
			require : "?ngModel",
			scope: {
				binding   : '=ngModel',
				loaderFn  : "&loader",
				orderByFn : "&orderBy"
			},
	/*
			compile: function compile(tElement, tAttrs, tTransclude) {
				//grab all transcluded html
				var children = tElement.children();

				//look for the place where we want to insert the html
				//ISSUE: tElement doesn't contain my template... I need my template somehow...
				var template = tElement.find('.reference-summary-cell').append(children);

				//append the new template to our compile element
				tElement.html('');
				tElement.append(template);
			},
	*/
			link: function ($scope, $element, $attr, ngModelController)
			{
				//Init
				$scope.references = [];
				$scope.multiple = $attr.multiple!==undefined;
				$.extend($scope.editor, {
					references  : null,
					visible     : false
				});

				//Watchers

				$scope.$watch("binding", $scope.load);
				$scope.$watch('binding', function() {
					ngModelController.$setViewValue($scope.binding);
				});

				$scope.$watch("editor.visible", function(_new, _old)
				{
					if(_new!=_old &&  _new) {
						var editor = $element.find("#editReference");
						editor.modal("show");
						editor.on('shown.bs.modal', function() {
							editor.find('.km-reference-search').focus();
						});
					}
					if(_new!=_old && !_new) $element.find("#editReference").modal("hide");
				});
			},
			controller: ["$scope", "authHttp", '$element', '$timeout', '$transclude', function ($scope, $http, $element, $timeout, $transclude)
			{
				$scope.editor = {};
				$scope.selected = -1;

				$scope.keydown = function($event) {
					if($event.which == 38) {
						if($scope.selected > 0)
							--$scope.selected;
					}
					else if($event.which == 40) {
						if($scope.selected < ($scope.filteredReferences.length - 1))
							++$scope.selected;
					}
					else if($event.which == 13) {
						if($scope.selected !== -1 && $scope.selected < $scope.filteredReferences.length)
							$scope.filteredReferences[$scope.selected].__checked = true;
						$scope.editor.save();
						$event.preventDefault();
					}
				};
				$scope.$watch('selected', function(newValue) {
					$element.find('.list-group-item-info').removeClass('list-group-item-info');
					//TODO: set acOption as the row, so I can just reuse that class rather than having multiple [both acOptions and acCheckbox]
					$element.find('.acOption'+$scope.selected).addClass('list-group-item-info');
					$element.find('.acOptions'+$scope.selected + ' :checkbox').focus();
					//this is incase the item hasn't been rendered yet...
					//we essentially just try again
					$timeout(function() {
						$element.find('.list-group-item-info').removeClass('list-group-item-info');
						$element.find('.acOption'+$scope.selected).addClass('list-group-item-info');
						$element.find('.acOption'+$scope.selected + ' :checkbox').focus();
					}, 100);
				});



				//====================
				//
				//====================
				$scope.load = function()
				{
					$scope.references = [];

					if($scope.binding)
					{
						var oBinding = $scope.binding;

						if (!angular.isArray(oBinding))
							oBinding = [oBinding];

						$scope.references = $scope.clone(oBinding);

						angular.forEach(oBinding, function(binding, index)
						{
							$scope.references[index].__loading = true;
							$scope.references[index].__binding = binding;

							$scope.loaderFn({ identifier : binding.identifier })
								  .then(function(data)  { $scope.load_onSuccess(index, data) },
										function(error) { $scope.load_onError  (index, error.data, error.status) });
						});
					}
				};

				//====================
				//
				//====================
				$scope.load_onSuccess = function(index, data)
				{
					var oBinding = $scope.references[index].__binding;

					$scope.references[index] = data;
					$scope.references[index].__binding   = oBinding;
					$scope.references[index].__loading   = false;
					$scope.references[index].__hasError  = false;
					$scope.references[index].__error     = undefined;
					$scope.references[index].__errorCode = undefined;
				}

				//====================
				//
				//====================
				$scope.load_onError = function(index, error, status)
				{
					$scope.references[index].__error = error || "unknown error";
					$scope.references[index].__errorCode = status;
					$scope.references[index].__loading = false;
				}

				//====================
				//
				//====================
				$scope.save = function()
				{
					var oNewBinding = [];

					angular.forEach($scope.references, function(ref, index)
					{
						if(ref.__binding)
							oNewBinding.push(ref.__binding);
					});

					if ($.isEmptyObject(oNewBinding))
						oNewBinding = undefined;

					if (oNewBinding && !$scope.multiple)
						oNewBinding = oNewBinding[0];

					$scope.binding = oNewBinding
				};

				//====================
				//
				//====================
				$scope.remove = function(reference)
				{
					var index= $scope.references.indexOf(reference);

					if(index>=0)
						$scope.references.splice(index, 1);

					$scope.save();
				};

				//====================
				//
				//====================
				$scope.loadAllReferences = function(reload)
				{
					if($scope.editor.references && !reload)
						return;

					$scope.isLoading = true;

					$scope.loaderFn({ identifier: null }).then(
						function(data) {
							$scope.isLoading = false;
							$scope.editor.references = $scope.clone(data);
							$element.find('.km-reference-search').focus();
						},
						function(err) {
							$scope.isLoading = false;
							$scope.editor.error = err;
						});
				};

				//====================
				//
				//====================
				$scope.clone = function(data)
				{
					return angular.fromJson(angular.toJson(data)); //clone;
				}

				//====================
				//
				//====================
				$scope.addReference = function()
				{
					$scope.loadAllReferences();
					$scope.editor.clearChecks();
					$scope.editor.search  = null;
					$scope.editor.visible = true;
				};

				//====================
				//
				//====================
				$scope.editor.save = function()
				{
					$.each($scope.editor.references, function(index, ref)
					{
						var oNewRef = $scope.clone(ref); //clone;

						oNewRef.__binding = { identifier: ref.identifier };
						oNewRef.__checked = undefined;

						if (ref.__checked) {
							if (!$scope.multiple)
								$scope.references = [];

							$scope.references.push(oNewRef);
							$scope.save();

							if (!$scope.multiple)
								return false;
						}
					});

					$scope.editor.close();
				}

				//====================
				//
				//====================
				$scope.editor.clearChecks = function()
				{
					if(!$scope.editor.references)
						return;

					angular.forEach($scope.editor.references, function(value) {
						value.__checked = false;
					});
				}

				//====================
				//
				//====================
				$scope.editor.close = function()
				{
					$scope.editor.search  = null;
					$scope.editor.visible = false;
					$element.find('.km-reference-select').focus();
				}

				//====================
				//
				//====================
				$scope.editor.filterExcludeSelected = function(ref)
				{
					return !_.findWhere($scope.references, { identifier: ref.identifier });
				}

				//====================
				//
				//====================
				$scope.editor.sortReference = function(ref)
				{
					if($scope.orderByFn)
						return $scope.orderByFn({reference : ref});
				}

				//load all references ahead of time, so the user doesnt have to wait on first load.
				$scope.loadAllReferences();
			}]
		};
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmSelect', function ()
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-select.html',
			replace: true,
			transclude: false,
			require : "?ngModel",
			scope: {
			  binding      : "=ngModel",
			  itemsFn      : "&items",
			  ngDisabledFn : "&ngDisabled",
			  required     : "@",
			  placeholder  : "@",
			  bindingType  : "@",
			  allowOther   : "@",
			  minimumFn    : "&minimum",
			  maximumFn    : "&maximum",
			},
			link: function ($scope, $element, $attrs, ngModelController)
			{
			  $scope.identifier = null;
			  $scope.rootItems  = null;
			  $scope.attr       = $attrs;
			  $scope.multiple   = $attrs.multiple   !== undefined && $attrs.multiple   !== null;
			  $scope.watchItems = $attrs.watchItems !== undefined && $attrs.watchItems !== null;
				 $scope.displayCount = 3;

			  $scope.$watch('identifier', $scope.save);
			  $scope.$watch('items',      $scope.load);

			  $scope.$watch('binding', function(newBinding) {
				 ngModelController.$setViewValue($scope.binding);
				 if (newBinding)
					$scope.autoInit().then($scope.load);
			  });

			  if ($scope.watchItems)
				 $scope.$watch($scope.itemsFn, function(items) {
					if (items)
					  $scope.autoInit(true).then($scope.load);
				 });

			  $element.find('.dropdown-menu').click(function(event) {
				 if ($scope.multiple && $scope.getSelectedItems().length!=0)
					event.stopPropagation();
			  });

			  if ($scope.multiple)
				 $element.find('.dropdown-toggle').popover({
					trigger: "hover",
					html : true,
					placement:"top",
					content: function() {
					  var oNames = _.map($scope.getTitles(), function(o) {
						 return html.encode(o)
					  });

					  if (!oNames || !oNames.length)
						 return null;

					  return "<ul><li style=\"width:500px;\">" + oNames.join("</li>\n<li>") + "</li></ul>";
					}
				 });


			  $scope.$on('clearSelectSelection', function(){
				 $scope.clearSelection();
			  })
			},
			controller: ["$scope", "$q","$filter", "$timeout", function ($scope, $q, $filter, $timeout)
			{
			  //==============================
			  //
			  //==============================
			  function transform(data) {
				 if (_.isArray(data)) {
					data = _.filter(data, _.isObject);
					data = _.map   (data, function(d) {
					  return {
						 identifier: d.identifier,
						 title: d.title || d.name,
						 children: transform(d.children || d.narrowerTerms),
						 selected: false
					  }
					});
				 }

				 return data;
			  }

			  //==============================
			  //
			  //==============================
			  function flaten(subTree) {
				 var oResult = [];

				 _.each(subTree, function(o) {
					oResult.push(o);

					if (o.children)
					  oResult = _.union(oResult, flaten(o.children));
				 });

				 return oResult;
			  }
			  //==============================
			  //
			  //==============================
			  $scope.autoInit = function(forceReinit) {

				 if (forceReinit){
					$scope.isInit = false;
					$scope.__loading = false;
				 }

				 var deferred = $q.defer();

				 if ($scope.isInit) {
					$timeout(function() {
					  if ($scope.allItems)
						 deferred.resolve()
					  else
						 deferred.reject("Data not loaded");
					});
				 }
				 else {
					$scope.isInit = true;
					$scope.setError(null);
					$scope.__loading = true;

					$q.when($scope.itemsFn(),
					  function(data) { // on success
						 $scope.__loading = false;
						 $scope.rootItems = transform(data); //clone values
						 $scope.allItems  = flaten($scope.rootItems);

						 deferred.resolve();
					  }, function(error) { // on error
						 $scope.__loading = false;
						 $scope.setError(error);
						 deferred.reject(error);
					  });
				 }

				 return deferred.promise;
			  }

			  //==============================
			  //
			  //==============================
			  $scope.getTitle = function(maxCount, truncate)
			  {
				 if ($scope.__loading)
					return "Loading...";

				 if (maxCount === undefined || maxCount === null)
					maxCount = -1;

				 var oNames = $scope.getTitles();

				 if(truncate) {
					oNames = _.map(oNames, function(name) {
					  return $filter('truncate')(name, 60, '...');
					});
				 }

				 if (oNames.length == 0)
					return $scope.placeholder || "Nothing selected...";
				 else if (maxCount<0 || oNames.length <= maxCount)
					return oNames.join(', ');

				 return "" + oNames.length + " of "+$scope.allItems.length+" selected";
			  }

			  //==============================
			  //
			  //==============================
			  $scope.getTitles = function()
			  {
				 return _.map($scope.getSelectedItems(), function(o) {
					return $filter("lstring")(o.title || o.name, $scope.locale);
				 });
			  }

			  //==============================
			  //
			  //==============================
			  $scope.getMinimum = function()
			  {
				 var value = $scope.minimumFn();

				 if (isNaN(value))
					value = 0;

				 return Math.max(value, 0);
			  }

			  //==============================
			  //
			  //==============================
			  $scope.getMaximum = function()
			  {
				 var value = $scope.maximumFn();

				 if (isNaN(value))
					value = 2147483647;

				 return Math.min(value, 2147483647);
			  }

			  //==============================
			  // in tree order /deep first
			  //==============================
			  $scope.getSelectedItems = function() {
				 return _.where($scope.allItems||[], { selected : true });
			  }

			  //==============================
			  //
			  //==============================
			  $scope.hasSelectedItems = function(subItems) {
				 return _.findWhere($scope.allItems||[], { selected : true })!==undefined;
			  }

			  //==============================
			  //
			  //==============================
			  $scope.load = function()
			  {
				 if (!$scope.allItems) // Not initialized
					return;

				 var oBinding = $scope.binding || [];

				 if (!_.isArray(oBinding) && (_.isString(oBinding) || _.isObject(oBinding)))
					oBinding = [oBinding];

				 if (!_.isArray(oBinding))
					throw "Value must be array"

				 oBinding = _.map(oBinding, function(item) {
					return _.isString(item) ? { identifier: item } : item;
				 });

				 angular.forEach($scope.allItems, function(item) {
					item.selected = _.find(oBinding, function(o) { return o.identifier == item.identifier })!==undefined;
				 });
			  }

			  //==============================
			  //
			  //==============================
			  $scope.save = function()
			  {
				 if (!$scope.allItems) // Not initialized
					return;

				 var oBindings = _.map($scope.getSelectedItems(), function(o) {
					return {
					  identifier: o.identifier,
					  customValue : o.customValue
					}
				 });

				 if ($scope.bindingType == "string" || $scope.bindingType == "string[]")
					oBindings = _.pluck(oBindings, 'identifier');

				 if (!$scope.multiple)
					oBindings = _.first(oBindings);

				 if ($.isEmptyObject(oBindings))
					oBindings = undefined;

				 $scope.binding = oBindings;
			  };

			  //==============================
			  //
			  //==============================
			  $scope.isRequired = function()
			  {
				 return $scope.required!=undefined;
			  }

			  //==============================
			  //
			  //==============================
			  $scope.setError = function(error) {
				 if (!error) {
					$scope.error = null;
					return;
				 }

				 if (error.status == 404) $scope.error = "Items not found";
				 else                     $scope.error = error.data || "unkown error";
			  }

			  //==============================
			  //
			  //==============================
			  $scope.chooseOther = function() {
				 alert("todo");
			  }

			  //==============================
			  //
			  //==============================
			  $scope.clearSelection = function() {
				 _.each($scope.allItems || [], function(item) {
					item.selected = false;
				 });

				 $scope.save();
			  }

			  //==============================
			  //
			  //==============================
			  $scope.clearSelection = function() {
				 _.each($scope.allItems || [], function(item) {
					item.selected = false;
				 });

				 $scope.save();
			  }

			  //==============================
			  //
			  //==============================
			  $scope.itemEnabled = function(item) {

				 if ( $scope.getMinimum() > 0 && $scope.getSelectedItems().length <= $scope.getMinimum())
					if (item == null || $scope.getSelectedItems().indexOf(item) >= 0)
					  return false;

				 if ($scope.getMaximum() < $scope.allItems.length && $scope.getSelectedItems().length >= $scope.getMaximum())
					if (item != null && $scope.getSelectedItems().indexOf(item) < 0)
					  return false;
				 return true;
			  }

			  //==============================
			  //
			  //==============================
			  $scope.clicked = function(clickedItem) {

				 if (!$scope.itemEnabled(clickedItem))
					return;

				 if ($scope.multiple && clickedItem)
					clickedItem.selected = !clickedItem.selected;

				 if (!$scope.multiple || !clickedItem) {
					_.each($scope.allItems||[], function(item) {
					  item.selected = (item == clickedItem);
					});
				 }

				 $scope.save();
			  }
			  $('#filterText').on("click", "*", function (e) {
							e.stopPropagation();
					  });
						$(document).on('click', '#filterText input', function (e) {
							e.stopPropagation();
					  });
			}]
     }
	})

	//============================================================
	//
	//
	//============================================================
	.directive('kmToggle', function ()
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-toggle.html',
			replace: true,
			transclude: false,
			scope: {
				binding      : '=ngModel',
				ngDisabledFn : '&ngDisabled',
				placeholder  : "@",
			},
			link: function ($scope, $element, $attrs, ngModelController)
			{
				//$scope.value = false;

			},
			controller: ["$scope", function ($scope)
			{
				$scope.$watch('value', function(oldValue, newValue){
					if(oldValue!=undefined)
						$scope.binding = newValue;
				});

			}]

		};
	})


	//============================================================
	//
	//
	//============================================================
	.directive('kmYesNo', [function ()
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-yes-no.html',
			replace: true,
			transclude: false,
			scope: {
				binding      : '=ngModel',
				ngDisabledFn : '&ngDisabled',
				required     : "@"
			},
			link: {},
			controller: ["$scope",  function ($scope)
			{
				//==============================
				//
				//==============================
				$scope.isRequired = function()
				{
					return $scope.required!=undefined
						&& $.isEmptyObject($scope.binding);
				}
			}]
		};
	}])

	//============================================================
	//
	//
	//============================================================
	.directive('kmDate', [function ()
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-date.html',
			replace: true,
			transclude: false,
			scope: {
				binding      : '=ngModel',
				placeholder  : '@',
				ngDisabledFn : '&ngDisabled'
			},
			link: function($scope, $element, $attr) {
				$element.datepicker({
					format: "yyyy-mm-dd",
					autoclose: true
				}).on('changeDate', function(event) {
					$element.find('input').focus();
				});
			},
			controller: ["$scope", function ($scope)
			{
			}]
		};
	}])

	//============================================================
	//
	//
	//============================================================
	.directive('kmFormStdButtons', ["$q", "$timeout", function ($q, $timeout)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-form-std-buttons.html',
			replace: true,
			transclude: true,
			scope: {
				getDocumentFn : '&document',
				hideSave 	  : '=?'
			},
			link: function ($scope, $element)
			{
				$scope.errors              = null;
				$scope.hideSave = $scope.hideSave || false;

				//BOOTSTRAP Dialog handling

				var qSaveDialog   = $element.find("#dialogSave");
				var qCancelDialog = $element.find("#dialogCancel");

				$scope.saveDialogDefered = [];
				$scope.cancelDialogDefered = [];

				$scope.showSaveDialog = function(visible) {

					var isVisible = qSaveDialog.css("display")!='none';

					if(visible == isVisible)
						return $q.when(isVisible);

					if(visible)
						$scope.updateSecurity();

					var defered = $q.defer();

					$scope.saveDialogDefered.push(defered)

					qSaveDialog.modal(visible ? "show" : "hide");

					return defered.promise;
				}

				$scope.showCancelDialog = function(visible) {
			  if($('form').filter('.dirty').length == 0) {
						$scope.$emit("documentClosed");
				 return;
			  }

					var isVisible = qCancelDialog.css("display")!='none';

					if(visible == isVisible)
						return $q.when(isVisible);

					var defered = $q.defer();

					$scope.cancelDialogDefered.push(defered)

					qCancelDialog.modal(visible ? "show" : "hide");

					return defered.promise;
				}

				qSaveDialog.on('shown.bs.modal' ,function() {

					$timeout(function(){

						var promise = null;
						while((promise=$scope.saveDialogDefered.pop()))
							promise.resolve(true);
					});
				});

				qSaveDialog.on('hidden.bs.modal' ,function() {

					$timeout(function(){

						var promise = null;
						while((promise=$scope.saveDialogDefered.pop()))
							promise.resolve(false);
					});
				});

				qCancelDialog.on('shown.bs.modal' ,function() {

					$timeout(function(){

						var promise = null;
						while((promise=$scope.cancelDialogDefered.pop()))
							promise.resolve(true);
					});
				});

				qCancelDialog.on('hidden.bs.modal' ,function() {

					$timeout(function(){

						var promise = null;
						while((promise=$scope.cancelDialogDefered.pop()))
							promise.resolve(false);
					});
				});
			},
			controller: ["$scope", "IStorage", "editFormUtility", function ($scope, storage, editFormUtility)
			{
				//====================
				//
				//====================
				$scope.updateSecurity = function()
				{
					$scope.security = {};
					$scope.loading = true;

					$q.when($scope.getDocumentFn()).then(function(document){

						if(!document || !document.header)
							return;

						var identifier = document.header.identifier;
						var schema     = document.header.schema;

						var a = storage.documents.exists(identifier).then(function(exist){

							var q = exist
								  ? storage.documents.security.canUpdate(document.header.identifier, schema)
								  : storage.documents.security.canCreate(document.header.identifier, schema);

							return q.then(function(allowed) {
								$scope.security.canSave = allowed
							});
						})

						var b = storage.drafts.exists(identifier).then(function(exist){

							var q = exist
								  ? storage.drafts.security.canUpdate(document.header.identifier, schema)
								  : storage.drafts.security.canCreate(document.header.identifier, schema);

							return q.then(function(allowed) {
								$scope.security.canSaveDraft = allowed
							});
						})

						return $q.all([a,b]);

					}).finally(function(){

						$scope.loading = false;
					});
				}


				//====================
				//
				//====================
				$scope.publish = function()
				{
					$scope.loading = true;

					var qDocument = $scope.getDocumentFn();
					var qReport   = validate(qDocument);

					return $q.all([qDocument, qReport]).then(function(results) {

						var document         = results[0];
						var validationReport = results[1];

						if(!document)
							throw "Invalid document";

						//Has validation errors ?
						if(validationReport && validationReport.errors && validationReport.errors.length>0) {

							$scope.$emit("documentInvalid", validationReport);
						}
						else return $q.when(editFormUtility.publish(document)).then(function(documentInfo) {

							if(documentInfo.type=='authority'){
								//in case of authority save the CNA as a contact in drafts
								saveAuthorityInContacts(documentInfo);
							}
							$scope.$emit("documentPublished", documentInfo, document);
							return documentInfo;

						});
					}).catch(function(error){

						$scope.$emit("documentError", { action: "publish", error: error })

					}).finally(function(){

						return $scope.closeDialog();

					});
				};

				//====================
				//
				//====================
				$scope.publishRequest = function()
				{
					$scope.loading = true;

					var qDocument = $scope.getDocumentFn();
					var qReport   = validate(qDocument);

					return $q.all([qDocument, qReport]).then(function(results) {

						var document         = results[0];
						var validationReport = results[1];

						if(!document)
							throw "Invalid document";

						//Has validation errors ?
						if(validationReport && validationReport.errors && validationReport.errors.length>0) {

							$scope.$emit("documentInvalid", validationReport);
						}
						else return $q.when(editFormUtility.publishRequest(document)).then(function(workflowInfo) {

							if(workflowInfo.type=='authority'){
								//in case of authority save the CNA as a contact in drafts
								saveAuthorityInContacts(workflowInfo);
							}
							$scope.$emit("documentPublishRequested", workflowInfo, document)
							return workflowInfo;

						});
					}).catch(function(error){

						$scope.$emit("documentError", { action: "publishRequest", error: error })

					}).finally(function(){

						return $scope.closeDialog();

					});
				};

				//====================
				//
				//====================
				$scope.saveDraft = function()
				{
					$scope.loading = true;

					return $q.when($scope.getDocumentFn()).then(function(document)
					{
						if(!document)
							throw "Invalid document";

						return editFormUtility.saveDraft(document);

					}).then(function(draftInfo) {

						if(draftInfo.type=='authority'){
							//in case of authority save the CNA as a contact in drafts
							saveAuthorityInContacts(draftInfo);
						}
						$scope.$emit("documentDraftSaved", draftInfo)
						return draftInfo;

					}).catch(function(error){

						$scope.$emit("documentError", { action: "saveDraft", error: error })

					}).finally(function(){

						return $scope.closeDialog();

					});
				};

				saveAuthorityInContacts = function(draftInfo){

					var document = $scope.getDocumentFn();
					if(!document)
						document = draftInfo;

					$q.when(document).then(function(document)
					{
						//replace the last char of authority doc GUID with C to create a new GUID for contact
						//this will help for easy update
						var id =''
						if(draftInfo.identifier)
							id = draftInfo.identifier;
						else if(draftInfo.data.identifier)
							id = draftInfo.data.identifier;

						if(id=='') {
							console.log('identifier not found in document info passed');
							return;
						}

						id = id.substr(0, id.length-1) + 'C'

						var qDocument = {
											header: {
														identifier : id,
														schema   : "contact",
														languages: ["en"]
													},
											type: "CNA" ,
											government : document.government,
											source: id,
											organization : document.name,
											organizationAcronym:{en:'NA'},
											city : document.city,
											country : document.country,
											phones : document.phones,
											emails : document.emails
										}

						if(document.address)qDocument.address = document.address;
						if(document.state)qDocument.state = document.state;
						if(document.postalCode)qDocument.postalCode = document.postalCode;
						if(document.websites)qDocument.websites = document.websites;
						if(document.faxes)qDocument.faxes = document.faxes;

						editFormUtility.saveDraft(qDocument);
					});
				}

				//====================
				//
				//====================
				$scope.close = function()
				{
					return $scope.closeDialog().then(function() {

						$scope.$emit("documentClosed")

					}).catch(function(error){

						$scope.$emit("documentError", { action: "close", error: error })

					}).finally(function(){

						return $scope.closeDialog();

					});
				};

				//====================
				//
				//====================
				function validate(document) {

					return $q.when(document).then(function(document){

						if(!document)
							throw "Invalid document";

						return storage.documents.validate(document);

					}).then(function(result) {

						return result.data || result;
					});
				}

				//====================
				//
				//====================
				$scope.checkErrors = function()
				{
					$scope.errors = "";

					if($scope.errors.trim()=="")
						$scope.errors = null;
				};

				//====================
				//
				//====================
				$scope.closeDialog = function()
				{
					return $q.all([$scope.showSaveDialog(false), $scope.showCancelDialog(false)]).finally(function(){
						$scope.loading = false;
					});
				};
			}]
		};
	}])

	//============================================================
	//
	//
	//============================================================
	//TODO: out of date, needs to be updated with current select, or select needs to be updated.
	.directive('kmFormLanguages', ["$q", function ($q)
	{
		return {
			restrict: 'EAC',
			template: '<span ng-show="isVisible()"><span km-select multiple ng-model="binding" binding-type="string" placeholder="Available Languages" items="locales|orderBy:\'name\'" minimum="1"></span></span>',
			replace: true,
			transclude: true,
			scope: {
				binding : '=ngModel',
			},
			controller: ["$scope", "IStorage", function ($scope, storage) {
				$scope.locales = [
					{identifier:"ar", name:"Arabic"  },
					{identifier:"en", name:"English" },
					{identifier:"es", name:"Spanish" },
					{identifier:"fr", name:"French"  },
					{identifier:"ru", name:"Russian" },
					{identifier:"zh", name:"Chinese" }
				];

				$scope.isVisible = function() {
					return $scope.binding!==undefined && $scope.binding!==null;
				}
			}]
		};
	}])

	//============================================================
	//
	//
	//============================================================
	.directive('kmDocumentValidation', ["$timeout", function ($timeout)
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-document-validation.html',
			replace: true,
			transclude: true,
			scope: {
				report : '=ngModel',
			},
			link: function ($scope, $element, $attr) {

				//====================
				//
				//====================
				$scope.jumpTo = function(field) {

					var qLabel = $element.parents("[km-tab]:last").parent().find("form[name='editForm'] label[for='" + field + "']:first");
					var sTab   = qLabel.parents("[km-tab]:first").attr("km-tab");

					if (sTab) {
						var qBody = $element.parents("body:last");

						$scope.$parent.tab = sTab;

						$timeout(function jumpTo(){
							qBody.stop().animate({ scrollTop : qLabel.offset().top-50 }, 300);
						});
					}
				}

				//====================
				//
				//====================
				$scope.getLabel = function(field) {

					var qLabel = $element.parents("[km-tab]:last").parent().find("form[name='editForm'] label[for='" + field + "']:first");

					if (qLabel.size() != 0)
						return qLabel.text();

					return field;
				}

			},
			controller: ["$scope", function ($scope)
			{
				//====================
				//
				//====================
				$scope.isValid = function() {
					return $scope.report && (!$scope.report.errors || $scope.report.errors.length == 0);
				}

				//====================
				//
				//====================
				$scope.hasErrors = function() {
					return $scope.report && $scope.report.errors && $scope.report.errors.length != 0;
				}

				//====================
				//
				//====================
				$scope.getTranslation = function(code, property, param) {
					if (code==null || code==""            ) return "Unknown error";
					if (code == "Error.Mandatory"         ) return "Field is mandatory";
					if (code == "Error.InvalidValue"      ) return "The value specified is invalid";
					if (code == "Error.InvalidProperty"   ) return "This value cannot be specified";
					if (code == "Error.UnspecifiedLocale" ) return "A language is use but not speficied in your document";
					if (code == "Error.UnexpectedTerm"    ) return "A specified term cannot be used";
					if (code == "Error.InvalidType"       ) return "The fields type is invalid";
					return code;
				}
			}]
		};
	}])


	//============================================================
	//
	//
	//============================================================
	.directive('kmTab', ["$timeout", function ($timeout)
	{
		return {
			restrict: 'A',
			link: function ($scope, $element, $attr)
			{
				//==============================
				//
				//==============================
				$scope.$watch("tab", function tab(tab){

					var isCurrentTab = $attr.kmTab==tab;

					if(isCurrentTab) $element.show();
					else             $element.hide();

					if(isCurrentTab) {

						var qBody   = $element.parents("body:last");
						var qTarget = $element.parents("div:first").find("form[name='editForm']:first");

						if(qBody.scrollTop() > qTarget.offset().top) {
							$timeout(function()	{
								if (!qBody.is(":animated"))
									qBody.stop().animate({ scrollTop:  qTarget.offset().top-100 }, 300);
							});
						}
					}
				})
			}
		}
	}])

	//============================================================
	//
	//
	//============================================================
	.directive('kmControlGroup', function ()
	{
		return {
			restrict: 'EAC',
			templateUrl: '/afc_template/km-control-group.html',
			replace: true,
			transclude: true,
			scope: {
				name      : '@name',
				caption   : '@caption',
				isValidFn : "&isValid"
			},
			link: function ($scope, $element, $attr)
			{
				if ($attr.isValid) {
					$scope.hasError = function() { return false; }
					$scope.hasWarning = function() {
						return !$scope.isValidFn({ "name": $scope.name });
					}
				}
				else if ($scope.$parent.isFieldValid) {
					$scope.hasError = function() { return false; }
					$scope.hasWarning = function() {
						return !$scope.$parent.isFieldValid($scope.name);
					}
				}

				$scope.isRequired = function() {
					var val = $element.attr("required");
					return val !== undefined && val!==false && val!=="false";
				}

			},
			controller: ["$scope", function ($scope)
			{
				$scope.hasWarning = function() {  //default behavior

					if($scope.name && $scope.$parent && $scope.$parent.validationReport && $scope.$parent.validationReport.warnings) {

						return !!_.findWhere($scope.$parent.validationReport.warnings, { property : $scope.name })
					}

					return false; //default behavior
				}

				$scope.hasError = function() {  //default behavior

					if($scope.name && $scope.$parent && $scope.$parent.validationReport && $scope.$parent.validationReport.errors) {

						return !!_.findWhere($scope.$parent.validationReport.errors, { property : $scope.name })
					}

					return false;
				}
			}]
		};
	})
  .directive('afcInput', function () {
    return {
      restrict: 'EAC',
      scope: {
		  ngModel: '=',
  		  type: '@',
        title: '@',
        help: '@',
        placeholder: '@',
      },
      templateUrl: '/afc_template/string.html',
  		controller: function($scope, $element, $attrs) {
			$('input', $element).each(function() {
				for(var i in $attrs)
					if(i.substr(0,1) != '$' && !$scope[i] && i != 'ngModel')
						$(this).attr(i, $attrs[i]);
			});
		},
    };
  })
  .directive('afcText', function() {
    return {
      restrict: 'EAC',
      scope: {
		  binding: "=ngModel",
        title: '@',
        placeholder: '@',
        rows: '@',
        help: '@',
      },
      templateUrl: '/afc_template/text.html',
  		controller: function($scope, $element, $attrs) {
			$('textarea', $element).each(function() {
				for(var i in $attrs)
					if(i.substr(0,1) != '$' && !$scope[i] && i != 'ngModel')
						$(this).attr(i, $attrs[i]);
			});
		},
    };
  })
  .directive('afcOptions', function() {
    return {
      restrict: 'AEC',
      scope: {
		  binding: "=ngModel",
        options: '=',
        title: '@',
        placeholder: '@',
        help: '@',
      },
      templateUrl: '/afc_template/options.html',
		controller: function($scope, $element, $attrs, $transclude, Localizer) {
			for(var key in $scope.options)
	  			$scope.options[key] = Localizer.phrase($scope.options[key]);

			$('select', $element).each(function() {
				for(var i in $attrs)
					if(i.substr(0,1) != '$' && !$scope[i] && i != 'ngModel')
						$(this).attr(i, $attrs[i]);
			});
		},
    };
  })
	//TODO: switch binding to ngModel... because it's dumb to use another name
  .directive('tabbedTextareas', function($timeout) {
	  return {
		  restrict: 'AEC',
  			scope: {
			  binding: "=ngModel",
			  tabs: "=",
  			  tabindex: "@",
  			  rows: "@",
  			  hideUnfocused: "@",
			  deletableTabs: "@",
  			  helpKey: "@",
  			  keyKey: "@",
  			  titleKey: "@",
			},
  			templateUrl: '/afc_template/tabbed-textareas.html',
  			controller: function($scope, $element, $attrs, $transclude) {
				//TODO: this initialization is hacky and will probably fall apart when part of an object is defined, but not the whole part. ie. isValid will fail.
				if(typeof $scope.binding === 'undefined') {
	  				$scope.binding = {};
					for(var i=0; i!=$scope.tabs.length; ++i)
	  					$scope.binding[$scope.tabs[i].key] = '';
				}
			   else if(typeof $scope.binding !== 'object') {
					var message = "Tabbed Textarea's ngModel binding requires an object or nothing. No other types are allowed, to ensure a value isn't improperly overwritten. Binding: ";
	  				console.log(message, $scope.binding);
	  				throw message + $scope.binding;
				}

  				$scope.isValid = function(key) {
					return ($scope.binding[key] && $scope.binding[key].length > 20);
				};

  				$scope.showTab = function($event, $index, key) {
					var root = $($event.target).parent().parent().parent();

					root.find('.tabbed-textarea').not('.tab'+key).hide();
					root.find('.tab'+key).show()
					root.find('.textarea'+key).focus();
					root.find('.atab').removeClass('active');
					root.find('.'+$index+'th-tab').addClass('active');
				};
				$scope.overwriteKeys = function($event, $index) {
					var root = $($event.target).parent().parent().parent().parent();

					if($event.which == 9) {
						$timeout(function() {
							root.find('.'+($index + 1)+'th-button').focus();
						});
					}
				};
				$scope.maybeHide = function($event, $index) {
					if($scope.hideUnfocused == 'true') {
						$($event.target).parent().hide();
						//TODO: change all this parent.parent jazz, with ancestor based on class, so the html structure won't break everything
						$($event.target).parent().parent().parent().parent().find('.'+$index+'th-tab').removeClass('active');
					}
				};
			},
	  };
  })
  .directive('lonlatInput', function() {
    return {
      restrict: 'AEC',
      scope: {
		binding: "=ngModel",
        help: '@',
      },
      templateUrl: '/afc_template/lonlat.html',
		controller: function($scope, $element, $attrs, $transclude) {
			var map = L.map('map', {
				center: [30, 15],
				zoom: 1,
				scrollWheelZoom: false,
			});
			map.on('zoomend', function(e) {
			    $scope.binding.zoom = map.getZoom();
			    
	  		    if($scope.$$phase)
			        $scope.$digest(); //necessary for some reason? Stupid Angular.
			});
			L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);

			var marker;
			$scope.newMarker = function() {
				if(marker)
					map.removeLayer(marker);
				marker = new L.Marker({lat: $scope.binding.lat, lng: $scope.binding.lng});
				map.setZoom($scope.binding.zoom);
				map.addLayer(marker);
	  		    map.setView({lat: $scope.binding.lat, lng: $scope.binding.lng}, $scope.binding.zoom);
			};
			map.on('click', function(e) {
				$scope.binding.lat = e.latlng.lat;
				$scope.binding.lng = e.latlng.lng;
				$scope.$apply(); //necessary, because we aren't in an angular event

				$scope.newMarker();
			});

			$('input', $element).each(function() {
				for(var i in $attrs)
					if(i.substr(0,1) != '$' && !$scope[i] && i != 'ngModel' && i != 'id')
						$(this).attr(i, $attrs[i]);
			});
			if(typeof $scope.binding === 'undefined')
	  			$scope.binding = {zoom: 1};
	  		else {
	  		    $scope.newMarker();
	  		    map.setView({lat: $scope.binding.lat, lng: $scope.binding.lng}, $scope.binding.zoom);
	  		}
		},
    };
  })
	.directive('afcAutocomplete', function() {
    return {
      restrict: 'AEC',
      scope: {
		  binding: '=ngModel',
  		  source: '=',
		  filter: '=?',
		  mapping: '=?',
  		  preview: '=?',
  		  placeholder: '@?',
  		  maxmatches: '@?',
  		  minchars: '@?',
  		  selectbox: '@?',
		  multiple: '@?',
		  ngDisabledFn : "&ngDisabled",
      },
      templateUrl: '/afc_template/afc-autocomplete.html',
		controller: function($scope, $element, $attrs, $compile, $timeout, $q) {
			//////Event Code////////
			$scope.updateSelected = function(index) {
				$scope.selected = index;
				if(index == -1)
					$scope.current = {};
				else
					$scope.current = $scope.buttonOverrideFilter()[$scope.selected];
			};

			$scope.keydown = function($event, i) {
				//TODO: don't switch up and down if we aren't showing results yet.
				if($event.which == 38) {
					if($scope.selected > 0)
						$scope.updateSelected($scope.selected - 1);
				}
				else if($event.which == 40) {
					if($scope.selected < ($scope.items.length - 1))
						$scope.updateSelected($scope.selected + 1);
				}
				else if($event.which == 13) {
					if($scope.filteredLength >= 1) {
						$scope.enterSelected();
					}
					$event.preventDefault();
					$scope.hideOptions();
					$scope.dontDoFilterOptions = true;
				}
				else if($event.which == 9 || $event.which == 188) {
					if($scope.filteredLength === 1) {
						$scope.enterSelected();
						if($scope.multiple) {
							$event.preventDefault();
							$scope.hideOptions();
						}
					}
				} else
					$scope.buttonActivated = false; //we are changing the filter, so we no longer want to show all results
			};

			$scope.keyup = function(query, $event) {
				//hackish...
				if($scope.dontDoFilterOptions)
					return $scope.dontDoFilterOptions = false;

				//if this is a single autocomplete, then maintain the binding
				if(!$scope.multiple)
					maintainBinding($scope, 'binding', $scope.bindingDisplay);

				filterOptions(query).then(function(items) {
					if(query.length >= minchars && $scope.filteredLength <= maxmatches || $scope.buttonActivated) //buttonActivated => only using arrow keys. Display hasn't changed
						$scope.showOptions();
					else
						$element.find('.list-group').hide();
				});
			};

			//only called when multiple = false
			function maintainBinding(binding, bindingKey, displayBinding) {
				filterOptions(displayBinding).then(function(items) {
					//If a regular text box, then keep binding in sync with what is displayed.
					if(!$scope.selectbox)
						binding[bindingKey] = displayBinding;

					//if selectbox, first try and match for binding. Ie. non-multiple expects the binding to always be up to date.
					if($scope.selectbox) {
						_.each(items, function(item) {
							if(item.__value.toLowerCase() === displayBinding.toLowerCase()) {
								binding[bindingKey] = $scope.mapping(item);
							}
						});
					}
				});
			}

			$scope.spansKeyup = function(index) {
				$scope.keyup($scope.displaySpans[index]);
				maintainBinding($scope.binding, index, $scope.displaySpans[index]);
			};


			/////ENTER CODE//////////
			$scope.enterSelected = function($index) {
				setBindingFromInput($scope.current);
			};

			var itemChosen = false;
			$scope.clickSelected = function($index) {
				itemChosen = true;

				//Either the full list or the filtered list, depending on what is currently showing.
				setBindingFromInput(
					$scope.buttonOverrideFilter()[$index]
				);
			};

			function setBindingFromInput(element) {
				if($scope.multiple) {
					toggleOption(element);
					$scope.bindingDisplay = '';
				}
				else
					$scope.binding = $scope.mapping(element);

				$scope.updateSelected(-1);
			};

			function toggleOption(element) {
				var mapping = $scope.mapping(element);
				var spliceIndex = indexOfPredicate($scope.binding, function(item) {
					return _.isEqual(item, mapping);
				});

				if(spliceIndex != -1)
					$scope.removeSpan(spliceIndex); //delete it, because we're "unselecting" it with a click.
				else {
					$scope.binding.push(mapping); //add what we clicked.
					$scope.displaySpans.push(element.__value);
				}
			}
			////////////END ENTER CODE

			////////////////////////END EVENTS

			//////Filter Functions//
			var prevValue;
			var deferred;
			function filterOptions(query) {
				//Do nothing if the value hasn't changed.
				if(prevValue === query) {
					return deferred.promise; //old promise
				} else {
					deferred = $q.defer();
					prevValue = query;
				}

				//console.log('prev, cur: ', prevValue, query);
				GetSourceItems().then(function(items) {
					$scope.displayItems = $scope.filter(query, items);

					//reselected the one we had selected if possible.
					if($scope.selected != -1) {
						var i;
						for(i = 0; i != $scope.displayItems.length; ++i) {
							if($scope.displayItems[i].__value == $scope.current.__value) {
								$scope.updateSelected(i); break;
							}
						}

						if(i === $scope.displayItems.length)
							$scope.updateSelected(0);
					}
					//else, if we have results and we didn't have anything selected, then select first item.
					else if($scope.displayItems.length !== 0)
						$scope.updateSelected(0);

					$scope.filteredLength = $scope.displayItems.length;
					deferred.resolve($scope.displayItems);
				});
				return deferred.promise; //the promise we used.
			};
			function GetSourceItems(){
				if(typeof $scope.source == 'function')
					return $scope.source();
				else{
					var def = $q.defer();
					def.resolve($scope.source);
					return def.promise;
				}
			}
			$scope.buttonOverrideFilter = function() {
				//console.log('button override: ', $scope.buttonActivated);
				if($scope.buttonActivated)
					return $scope.items;
				else
					return $scope.displayItems;
			};
			////////////////////////END FILTERS

			//////Visual Functions//
			$scope.removeSpan = function(index) {
				$scope.binding.splice(index, 1);
				$scope.displaySpans.splice(index, 1);
			};

			//Toggle whether or not to show the all options
			$scope.toggleAllOptions = function(buttonActivate) {
				if(buttonActivate)
					//TODO: rename to 'showAll' or something
					$scope.buttonActivated = true;
				if($element.find('.list-group').is(':hidden') || $element.find('#'+$scope.title).is(':focus')) {
					$scope.showOptions();
				} else
					$scope.hideOptions();
			};
			$scope.showOptions = function() {
				$element.find('.list-group').show();
			};
			$scope.hideOptions = function() {
				$element.find('.list-group').hide();
				$scope.buttonActivated = false;
			};

			$scope.delayHideOptions = function() {
				$timeout(function() {
					$scope.hideOptions();
				}, 250);
			};

			$scope.updateHideOptions = function() {
				//TODO: do this somehow with promises... this is just hacking around the event model with timeouts
				$timeout(function() {
					$scope.buttonActivated = false;
				}, 150);
				//Honestly... the browser should trigger all events, before evaluating them, and an event should definitely be able to trigger while it has display: none... grrr....
				$timeout(function() {
					if(!$scope.buttonActivated)
						$scope.hideOptions();

					if(itemChosen) {
						itemChosen = false;
						//we've already selected something, so we're done.
						return;
					}

					//Now we need to update the binding, or add the item if need be.
					var string = $scope.bindingDisplay;

					//non-multiple selectboxes need to clear their binding if the display doesnt match anything
					if(!$scope.multiple) {
						if($scope.selectbox) {
							if(reverseMap($scope.binding, $scope.items) !== $scope.bindingDisplay)
								$scope.bindingDisplay = $scope.binding = null;
						}
					} else {	//Multiple
						if($scope.selectbox)
							GetSourceItems().then(function(results) {
								for(var i=0; i!=results.length; ++i)
									if(results[i].__value == string) {
										$scope.binding.push(results[i]);
										$scope.displaySpans.push(results[0].__value);
									}

								$scope.bindingDisplay = '';
							});
						else
							if(string != '') //we'll allow any option, but empty string.
								$scope.binding.push(string);
					}
				}, 250);
			};

			$scope.updateSpan = function(index, string) {
				$timeout(function() {
					if($scope.selectbox) {
						//if the display no longer represents the binding, then the binding is now invalidated, so clear.
						if(reverseMap($scope.binding[index], $scope.items) !== string)
							$scope.removeSpan(index);
					} else {
						if(string == '')
							$scope.removeSpan(index);
						else
							$scope.binding[index] = string;
					}

					$scope.hideOptions();
				}, 250);
			};
			////////////////////////END VISUALS

			$scope.$watch('binding', function(newValue) {
				if($scope.multiple) {
					//if multiepl select box, then we need to revers map the displaySpan texts
					if($scope.selectbox)
							GetSourceItems().then(function(items) {
								for(var i=0; i!=$scope.binding.length; ++i)
									$scope.displaySpans[i] = reverseMap($scope.binding[i], items);
							});
					else {
						//if regular multiple, then just directly put the displaySpans in as the bindings
						for(var i=0; i!=$scope.binding.length; ++i)
							$scope.displaySpans[i] = $scope.binding[i];
					}

				} else {
					if($scope.selectbox)
						GetSourceItems().then(function(items) {
								$scope.bindingDisplay = reverseMap($scope.binding, items);
							});
					else
						$scope.bindingDisplay = $scope.binding;
				}
			});


			function reverseMap(value, items) {
				var foundItem = '';
				_.each(items, function(item) {
					if(JSON.stringify($scope.mapping(item)) == JSON.stringify(value))
						foundItem = item.__value;
				});
				//TODO: add error, for if no item matched

				return foundItem;
			};

			//////Initializing//////
			$scope.items = []; //start empty, but then attempt to fill it.
			//console.log(GetSourceItems());
			GetSourceItems().then(function(items) {
				//console.log(items , '2975');
				$scope.items = items;
			});

			$scope.bindingDisplay = ""; //so the display won't be empty.
			$scope.displaySpans = [];

			//TODO: inherit and create a separate control for selectboxes.
			if($scope.mapping && !$scope.selectbox)
				throw "You can't have a mapping without a selectbox. Mappings are only for selectbox configurations.";
			if(!$scope.mapping)
				$scope.mapping = function(item) {
					return item.__value;
				};
			if(!$scope.filter)
				$scope.filter = function($query, items) {
					// console.log('items: ', items);
					// console.log('query: ', $query);
					var matchedOptions = [];
					for(var i = 0; i != items.length; ++i) //_value is always there.
						if(items[i].__value.toLowerCase().indexOf($query.toLowerCase()) != -1)
							matchedOptions.push(items[i]);

					// console.log('matched: ', matchedOptions);
					return matchedOptions;
				};

			if(typeof $scope.maxmatches == 'undefined') $scope.maxmatches = 7;
			else if($scope.maxmatches <= 0)	$scope.maxmatches = 999999;
			if(typeof $scope.minchars == 'undefined') $scope.minchars = 1;
			if(typeof $scope.selectbox == 'undefined') $scope.selectbox = false;
			var maxmatches = $scope.maxmatches;
			var minchars = $scope.minchars;

			$scope.hidePreview = false;
			if($scope.multiple == 'true' && typeof $scope.binding == 'undefined')
				$scope.binding = [];

			$scope.updateSelected(-1);

			//transfer attributes to the internal input
			$('input', $element).each(function() {
				for(var i in $attrs)
					if(i.substr(0,1) != '$' && !$scope[i] && i != 'ngModel')
						$(this).attr(i, $attrs[i]);
			});
			////////////////////////END INIT
		},
	 }
	})
	.directive('lwfcAutocomplete', function() {
    return {
      restrict: 'AEC',
      scope: {
		  binding: '=ngModel',
  		  source: '=',
  		  preview: '=?',
		  title: '@',
  		  placeholder: '@',
        help: '@?',
  		  maxmatches: '@?',
  		  minchars: '@?',
  		  selectbox: '@?',
      },
      templateUrl: '/afc_template/lwfc-autocomplete.html',
  	 };
  })

	.directive('compile', function($compile) {
	  // directive factory creates a link function
	  return {
       restrict: 'A',
		 controller: function($scope, $element, $attrs) {
			 $scope.$watch(
				function($scope) {
				  // watch the 'compile' expression for changes
				  return $scope.$eval($attrs.compile);
				},
				function(value) {
				  // when the 'compile' expression changes
				  // assign it into the current DOM
				  $element.html(value);

				  // compile the new DOM and link it to the current
				  // $scope.
				  // NOTE: we only compile .childNodes so that
				  // we don't get into infinite loop compiling ourselves
				  $compile($element.contents())($scope);
				}
			 );
		  },
	  };
	})

	//============================================================
	//
	//
	//
	//============================================================
	.filter("term", ["$http", '$filter', function($http, $filter) {
		var cacheMap = {};

		return function(term, locale) {

			if(!term)
				return "";

			if(term && angular.isString(term))
				term = { identifier : term };

			locale = locale||"en";

			if(term.customValue)
				return $filter("lstring")(term.customValue, locale);

			if(cacheMap[term.identifier])
				return $filter("lstring")(cacheMap[term.identifier].title, locale) ;

			cacheMap[term.identifier] = $http.get("/api/v2013/thesaurus/terms?termCode="+encodeURIComponent(term.identifier),  {cache:true}).then(function(result) {

				cacheMap[term.identifier] = result.data;

				return $filter("lstring")(cacheMap[term.identifier].title, locale);

			}).catch(function(){

				cacheMap[term.identifier] = term.identifier;

				return term.identifier;

			});
		};
	}])

  .factory('Thesaurus', [function() {
		return {
			buildTree : function(terms) {
				var oTerms    = [];
				var oTermsMap = {};

				Enumerable.from(terms).forEach(function(value) {
					var oTerm = {
						identifier  : value.identifier,
						title       : value.title,
						description : value.description
					}

					oTerms.push(oTerm);
					oTermsMap[oTerm.identifier] = oTerm;
				});

				for (var i = 0; i < oTerms.length; ++i) {
					var oRefTerm = terms [i];
					var oBroader = oTerms[i];

					if (oRefTerm.narrowerTerms && oRefTerm.narrowerTerms.length > 0) {
						angular.forEach(oRefTerm.narrowerTerms, function(identifier) {
							var oNarrower = oTermsMap[identifier];

							if (oNarrower) {
								oBroader.narrowerTerms = oBroader.narrowerTerms || [];
								oNarrower.broaderTerms = oNarrower.broaderTerms || [];

								oBroader.narrowerTerms.push(oNarrower);
								oNarrower.broaderTerms.push(oBroader);
							}
						});
					}
				}

				return Enumerable.from(oTerms).where("o=>!o.broaderTerms").toArray();
			}
		}
	}])
	.filter("lstring", function() {
    return function(ltext, locale) {
      if(!ltext)
        return "";

      if(angular.isString(ltext))
        return ltext;

      var sText;

      if(!sText && locale)
        sText = ltext[locale];

      if(!sText)
        sText = ltext.en;

      if(!sText) {
        for(var key in ltext) {
          sText = ltext[key];
          if(sText)
            break;
        }
      }

      return sText||"";
    }
	})

  .filter("truncate", function() {
		return function(text, maxSize, suffix) {

			if (!maxSize)
				return text;

			if (!suffix)
				suffix = "";

			if(!text)
				return "".su;

			if (text.length > maxSize)
				text = text.substr(0, maxSize) + suffix;

			return text;
		};
	})
  .factory("IStorage", ["authHttp", "$q", "authentication", "realm", function($http, $q, authentication, defaultRealm) {
		return new function()
		{
			var serviceUrls = { // Add Https if not .local
				documentQueryUrl   : function() { return "/api/v2013/documents/"; },
				documentUrl        : function() { return "/api/v2013/documents/:identifier"; },
				validateUrl        : function() { return "/api/v2013/documents/x/validate"; },
				draftUrl           : function() { return "/api/v2013/documents/:identifier/versions/draft"; },
				attachmentUrl      : function() { return "/api/v2013/documents/:identifier/attachments/:filename"; },
				securityUrl        : function() { return "/api/v2013/documents/:identifier/securities/:operation"; },
				draftSecurityUrl   : function() { return "/api/v2013/documents/:identifier/versions/draft/securities/:operation"; },
				draftLockUrl       : function() { return "/api/v2013/documents/:identifier/versions/draft/locks/:lockID"; }
			};

			//==================================================
			//
			// Documents
			//
			//==================================================
			this.documents = {

				//===========================
				//
				//===========================
				"query" : function(query, collection, params)
				{
					console.log('query');
					console.log(params);
					params            = _.extend({}, params||{});
					params.collection = collection;
					params.$filter    = query;

					if (query && !collection)
						params.collection = "my";

					var useCache = !!params.cache;
					if(!params.cache)
						params.cache = true;

					var oTrans = transformPath(serviceUrls.documentQueryUrl(), params);

					return $http.get(oTrans.url, {  params : oTrans.params, cache:useCache });

					//TODO: return result.data;
				},

				//===========================
				//
				//===========================
				"get" : function(identifier, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					var useCache = !!params.cache;

					if(!params.cache)
						params.cache = true;


					var oTrans = transformPath(serviceUrls.documentUrl(), params);

					return $http.get(oTrans.url, { params : oTrans.params, cache:useCache });

					//TODO: return result.data;

				},

				//===========================
				//
				//===========================
				"exists" : function(identifier, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					var useCache = !!params.cache;

					if(!params.cache)
						params.cache = true;


					var oTrans = transformPath(serviceUrls.documentUrl(), params);

					return $http.head(oTrans.url, { params : oTrans.params, cache:useCache }).then(function() {

						return true;

					}).catch(function(error) {

						if(error.status!="404")
							throw "Error";

						return false;
					});
				},

				//===========================
				//
				//===========================
				"put" : function(identifier, data, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					if (!params.schema && data && data.header && data.header.schema)
						params.schema = data.header.schema;

					var oTrans = transformPath(serviceUrls.documentUrl(), params);

					return $http.put(oTrans.url, data, { "params" : oTrans.params }).then(function(result){
						return result.data;
					});
				},

				//===========================
				//
				//===========================
				"delete" : function(identifier, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					var oTrans = transformPath(serviceUrls.documentUrl(), params);

					return $http.delete(oTrans.url, { "params" : oTrans.params });
				},

				//===========================
				//
				//===========================
				"validate" : function(document, params)
				{
					params = clone(params || {});

					if (!params.schema && document && document.header && document.header.schema)
						params.schema = document.header.schema;

					var oTrans = transformPath(serviceUrls.validateUrl(), params);

					return $http.put(oTrans.url, document, { "params" : oTrans.params });

					//TODO: return result.data;
				},

				//===========================
				//
				//===========================
				"security": {
					canCreate: function(identifier, schema, metadata) {
						return canDo(serviceUrls.securityUrl(), "create", identifier, schema, metadata);
					},

					canUpdate: function(identifier, schema, metadata) {
						return canDo(serviceUrls.securityUrl(), "update", identifier, schema, metadata);
					},

					canDelete: function(identifier, schema, metadata) {
						return canDo(serviceUrls.securityUrl(), "delete", identifier, schema, metadata);
					}
				}
			};

			//==================================================
			//
			// Drafts
			//
			//==================================================
			this.drafts = {

				//===========================
				//
				//===========================
				"query" : function(query, params)
				{
					params            = clone(params||{});
					params.collection = "mydraft";
					params.$filter    = query;

					var useCache = !!params.cache;

					if(!params.cache)
						params.cache = true;

					var oTrans = transformPath(serviceUrls.documentQueryUrl(), params);

					return $http.get(oTrans.url, {  params : oTrans.params, cache:useCache });

					//TODO: return result.data;
				},


				//===========================
				//
				//===========================
				"get" : function(identifier, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					var useCache = !!params.cache;

					if(!params.cache)
						params.cache = true;

					var oTrans = transformPath(serviceUrls.draftUrl(), params);

					return $http.get(oTrans.url, {  params : oTrans.params, cache:useCache });

					//TODO: return result.data;
				},

				//===========================
				//
				//===========================
				"exists" : function(identifier, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					var useCache = !!params.cache;

					if(!params.cache)
						params.cache = true;

					var oTrans = transformPath(serviceUrls.draftUrl(), params);

					return $http.head(oTrans.url, {  params : oTrans.params, cache:useCache }).then(function() {

						return true;

					}).catch(function(error) {

						if(error.status!="404")
							throw "Error";

						return false;
					});
				},

				//===========================
				//
				//===========================
				"put" : function(identifier, data, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					if (!params.schema && data && data.header && data.header.schema)
						params.schema = data.header.schema;

					var oTrans = transformPath(serviceUrls.draftUrl(), params);

					return $http.put(oTrans.url, data, { "params" : oTrans.params }).then(function(result){
						return result.data;
					});
				},

				//===========================
				//
				//===========================
				"delete" : function(identifier, params)
				{
					params            = clone(params||{});
					params.identifier = identifier;

					var oTrans = transformPath(serviceUrls.draftUrl(), params);

					return $http.delete(oTrans.url, { "params" : oTrans.params });

					//TODO: return result.data;
				},

				//===========================
				//
				//===========================
				"security": {
					canCreate: function(identifier, schema, metadata) {
						return canDo(serviceUrls.draftSecurityUrl(), "create", identifier, schema, metadata);
					},

					canUpdate: function(identifier, schema, metadata) {
						return canDo(serviceUrls.draftSecurityUrl(), "update", identifier, schema, metadata);
					},

					canDelete: function(identifier, schema, metadata) {
						return canDo(serviceUrls.draftSecurityUrl(), "delete", identifier, schema, metadata);
					}
				},

				"locks" : {

					//===========================
					//
					// Not tested
					//
					//===========================
					"delete" : function(identifier, lockID)
					{
						var params = {
							identifier : identifier,
							lockID     : lockID
						};

						var oTrans = transformPath(serviceUrls.draftLockUrl(), params);

						return $http.delete(oTrans.url).then(function(success) {
							return success.data;
						});
					}
				}
			};

			this.attachments = {

				//===========================
				//
				// Not tested
				//
				//===========================
				"put" : function(identifier, file, params)
				{
					params            = params || {};
					params.identifier = identifier;
					params.filename   = file.name;

					var contentType = params.contentType || getMimeTypes(file.name, file.type || "application/octet-stream");

					params.contentType = undefined;

					var oTrans = transformPath(serviceUrls.attachmentUrl(), params);

					return $http.put(oTrans.url, file, {
						"headers" : { "Content-Type": contentType },
						"params"  : oTrans.params
					}).then(
					function(success) {
						return angular.extend(success.data || {}, { "url": oTrans.url });
					},
					function(error) {
						error.data = angular.extend(error.data || {}, { "url": oTrans.url });
						throw error;
					});
				},

				"getMimeType": function(file)
				{
					return getMimeTypes(file.name, file.type || "application/octet-stream");
				}
			};

			//==================================================
			//
			//
			//==================================================
			var getMimeTypes = function(filename, defaultMimeType) {
				var sMimeType = defaultMimeType || "application/octet-stream";

				if (filename && sMimeType == "application/octet-stream")
				{
					var sExt   = "";
					var iIndex = filename.lastIndexOf(".");

					if (iIndex >= 0)
						sExt = filename.substring(iIndex).toLowerCase();

					if (sExt == ".json")    sMimeType = "application/json";
					if (sExt == ".geojson") sMimeType = "application/json";
					if (sExt == ".xml")     sMimeType = "application/xml";
				}

				return sMimeType;
			};

			//==================================================
			//
			//
			//==================================================
			var clone = function(obj) {

				if (obj === null)
					return null;

				if (obj === undefined)
					return undefined;

				return angular.fromJson(angular.toJson(obj));
			};

			//===========================
			//
			// Replace :xyz in path with value in params
			// query part will be done by $http
			//
			//===========================
			var transformPath = function(url, params)
			{
				var oRegex     = /\:\w+/g;
				var oMatch     = null;
				var qMatches   = [];
				var oNewParams = {};

				while ((oMatch = oRegex.exec(url)) !== null) {
					oMatch.key   = oMatch[0].substring(1);
					oMatch.value = oMatch[0];
					qMatches.splice(0, 0, oMatch);
				}

				for(var key in params||{}) {
					var bExist = false;

					for(var i in qMatches) {
						if (qMatches[i].key != key)
							continue;

						bExist = true;
						qMatches[i].value = params[key].toString();
					}

					if(!bExist)
						oNewParams[key] = params[key];
				}

				for (var j in qMatches) {
					url = replaceAt(url, qMatches[j].index, qMatches[j][0].length, encodeURIComponent(qMatches[j].value));
				}

				return { "url" : url, "params" : oNewParams };
			};

			//===========================
			//
			// Calls storage security
			//
			//===========================
			var canDo = function(patternPath, operation, identifier, schema, metadata) {

				metadata = angular.extend({}, { "schema": schema }, metadata || {});

				return $q.when(authentication.getUser()).then(function(user) {

					if (!metadata.government && user.government)
						metadata = angular.extend(metadata, { "government": user.government });

					if (!metadata.realm && defaultRealm)
						metadata = angular.extend(metadata, { "realm": defaultRealm });

					var params = {
						"identifier" : identifier || "x",
						"operation"  : operation,
						"metadata"   : metadata
					};

					var oTrans = transformPath(patternPath, params);

					return $http.get(oTrans.url, { "params": oTrans.params })

				}).then(function(res) {

					return res.data.isAllowed;
				});
			};

			//===========================
			//
			// Replace :xyz in path with value in params
			// query part will be done by $http
			//
			//===========================
			var replaceAt = function(str, index, len, newText) {
				return str.substring(0, index) + newText + str.substring(index+len);
			};

			return this;
		}();
	}])

  .factory('authentication', ["$http", "$browser", function($http, $browser) {

		var currentUser = null;

		//============================================================
	    //
	    //
	    //============================================================
		function getUser () {

			if(currentUser) return currentUser;

			var headers = { Authorization: "Ticket " + $browser.cookies().authenticationToken };

			currentUser = $http.get('/api/v2013/authentication/user', { headers: headers}).then(function onsuccess (response) {
				return response.data;
			}, function onerror (error) {
				return { userID: 1, name: 'anonymous', email: 'anonymous@domain', government: null, userGroups: null, isAuthenticated: false, isOffline: true, roles: [] };
			});

			return currentUser;
		}

		//============================================================
	    //
	    //
	    //============================================================
		function signOut () {

			document.cookie = "authenticationToken=; path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT";
			reset();
		}

		//============================================================
	    //
	    //
	    //============================================================
		function reset () {

			currentUser = undefined;
		}

		return { getUser: getUser, signOut: signOut, reset: reset };

	}])

	.factory('authHttp', ["$http", "$browser", function($http, $browser) {

		function addAuthentication(config) {

			if(!config)         config         = {};
			if(!config.headers) config.headers = {};

			if($browser.cookies().authenticationToken) config.headers.Authorization = "Ticket "+$browser.cookies().authenticationToken;
			else                                       config.headers.Authorization = undefined;

			return config;
		}

		function authHttp(config) {
			return $http(addAuthentication(config));
		}

		authHttp["get"   ] = function(url,       config) { return authHttp(angular.extend(config||{}, { 'method' : "GET"   , 'url' : url })); };
		authHttp["head"  ] = function(url,       config) { return authHttp(angular.extend(config||{}, { 'method' : "HEAD"  , 'url' : url })); };
		authHttp["delete"] = function(url,       config) { return authHttp(angular.extend(config||{}, { 'method' : "DELETE", 'url' : url })); };
		authHttp["jsonp" ] = function(url,       config) { return authHttp(angular.extend(config||{}, { 'method' : "JSONP" , 'url' : url })); };
		authHttp["post"  ] = function(url, data, config) { return authHttp(angular.extend(config||{}, { 'method' : "POST"  , 'url' : url, 'data' : data })); };
		authHttp["put"   ] = function(url, data, config) { return authHttp(angular.extend(config||{}, { 'method' : "PUT"   , 'url' : url, 'data' : data })); };

		return authHttp;
	}])
	.directive('selectAllOnClick', function() {
		return {
			restrict: 'A',
			link: function(scope, element, attrs) {
				element.on('focus', function() {
					console.log('selectAllOnClick');
					this.setSelectionRange(0, 9999);
				});
			},
		};
	})
    .filter('to_trusted', function($sce){
        return function(html) {
            return $sce.trustAsHtml(html);
        };
    })
;

//a helper function
function indexOfPredicate(array, predicate) {
	for(var i=0; i!=array.length; ++i)
		if(predicate(array[i]))
			return i;

	return -1;
}
