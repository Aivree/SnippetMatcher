(function() {
    'use strict';

    angular.module('app', ['ui.bootstrap','app.shared', 'app.components', 'ui.router', 'ngResource'])
    .config(function($stateProvider, $urlRouterProvider) {
    
      $urlRouterProvider.otherwise('/home');
    
      $stateProvider
        
        // HOME STATES AND NESTED VIEWS ========================================
          .state('home', {
             url: '/home',
              templateUrl: 'partials/partial-home.view.html'
          })

          // nested list with custom controller
        .state('home.list', {
          url: '/list',
          templateUrl: 'partials/partial-home-list.view.html',
            controller: function($scope) {
                $scope.dogs = ['Bernese', 'Husky', 'Goldendoodle'];
            }
        })

        // nested list with just some random string data
        .state('home.paragraph', {
          url: '/paragraph',
          template: 'I could sure use a drink right now.'
        })
        
        // ABOUT PAGE AND MULTIPLE NAMED VIEWS =================================
          .state('about', {
            url: '/about',
            views: {
                '': { templateUrl: 'partials/partial-about.view.html' },
                'columnOne@about': { template: 'Look I am a column!' },
                'columnTwo@about': { 
                    templateUrl: 'partials/partial-table-data.view.html',
                    controller: 'scotchController'
                }
            }
            
        })
          .state('register', {
          url: '/register',
          templateUrl: 'components/core/register/register.view.html',
        });
         
    })

  .controller('scotchController', function($scope) {
    
    $scope.message = 'test';
   
    $scope.scotches = [
        {
            name: 'Macallan 12',
            price: 50
        },
        {
            name: 'Chivas Regal Royal Salute',
            price: 10000
        },
        {
            name: 'Glenfiddich 1937',
            price: 20000
        }
    ];
    
})
//////////////////////////////////////////
/////////////Pais Controller//////////////
//////////////////////////////////////////
  .controller("PaisCtrl", function ($http,$scope) {
          var app = this;
          $http.get("http://localhost:8090/pais")
            .success(function (data) {
                app.pais = data;
            })
          app.addPais = function (a) {
              var pais = a.nombre;
              $http({
                  url: 'http://localhost:8090/pais',
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { Nombre: pais },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                    $http.get("http://localhost:8090/pais")
                    .success(function (data) {
                    app.pais = data;
                    })
                })

              }
          app.Borrar = function (a) {
              var pais = a;
              $http({
                  url: 'http://localhost:8090/pais/delete/'+pais,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: pais },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {
                    var array = new Array();
                    var arrayAux = new Array();
                    array.push(data[0]);
                    var counter = 0;
                    console.log(app.pais);
                for (var item in app.pais) {
                  if(app.pais.length>counter){
                    if (app.pais[counter].id==array[0].id) {
                       
                        counter = counter + 1;
                    }        
                     else { 
                      arrayAux.push(app.pais[counter]);
                      counter = counter + 1; 
                    }
                }
              }
                    app.pais=arrayAux;
                    console.log(array);
                    console.log(app.pais);
                    //$scope.data.splice(index, 1);
                })
          }
          
      })
/////////////////////////////////////////////
////////////liga Controler//////////////////
/////////////////////////////////////////////
  .controller("LigaCtrl", function ($http) {
      var app = this;
        $http.get("http://localhost:8090/ligaPais")
        .success(function (data) {
            app.Liga = data[0].Ligasarray;
            app.pais=data[0].Paisarray;
                var array = new Array();
                var arrayAux = new Array();
                var counter = 0;

                for (var item in app.pais) {
                    if (app.pais[counter].status) {
                        arrayAux.push(app.pais[counter]);
                        counter = counter + 1;
                    } else { counter = counter + 1; }
                }
                app.pais = arrayAux;
            console.log(app.pais);
        })

      app.addLiga = function (a, b) {

          var answer = a.Nombre;
          var pais = a.Pais.id;
          

          $http({
              url: 'http://localhost:8090/liga',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Nombre: answer, Idpais: pais },

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
            .success(function (data) {
                var array = new Array();
                array = app.Liga;
                array.push(data[0]);
                app.Liga = array;

                //a.type=data[0].nombre
          
 
 
                console.log(array);
                console.log(app.Liga);
                //$scope.data.splice(index, 1);
            })
      }
                      app.Garca = function (a) {
                                $http.get("http://localhost:8090/ligaPais")
        .success(function (data) {
            app.Liga = data[0].Ligasarray;
            app.pais=data[0].Paisarray;
                var array = new Array();
                var arrayAux = new Array();
                var counter = 0;

                for (var item in app.pais) {
                    if (app.pais[counter].status) {
                        arrayAux.push(app.pais[counter]);
                        counter = counter + 1;
                    } else { counter = counter + 1; }
                }
                app.pais = arrayAux;
            console.log(app.pais);
        })

                      }
                      app.Borrar = function (a) {
              var liga = a;
              $http({
                  url: 'http://localhost:8090/liga/delete/'+liga,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: liga },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {


                    app.Liga=data[0].Ligasarray;
                    //$scope.data.splice(index, 1);
                })
          }

  })

//////////////////////////////////////////
  .controller("TorneoCtrl", function ($http,$scope) {
      var app = this;
        $scope.oneAtATime = true;

  $scope.groups = [
    {
      title: 'Dynamic Group Header - 1',
      content: 'Dynamic Group Body - 1'
    },
    {
      title: 'Dynamic Group Header - 2',
      content: 'Dynamic Group Body - 2'
    }
  ];

  $scope.items = ['Item 1', 'Item 2', 'Item 3'];

  $scope.addItem = function() {
    var newItemNo = $scope.items.length + 1;
    $scope.items.push('Item ' + newItemNo);
  };

  $scope.status = {
    isFirstOpen: false,
    isFirstDisabled: false
  };
      $http.get("http://localhost:8090/torneoLiga")
        .success(function (data) {

          app.liga = data[0].Ligaarray;
            app.torneo=data[0].Torneoarray;
            //app.torneo.fecha1="Wed Oct 25 2014 12:00:00 GMT-0500";
            console.log(app.torneo);
        })

  
      app.addTorneo = function (a) {

          var answer = a.Nombre;
          var pais = a.Liga.Id;
          
    console.log(pais);

          $http({
              url: 'http://localhost:8090/torneo',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Nombre: answer, Idliga: pais },

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
            .success(function (data) {
         $http.get("http://localhost:8090/torneoLiga")
        .success(function (data) {

          app.liga = data[0].Ligaarray;
            app.torneo=data[0].Torneoarray;
            //app.torneo.fecha1="Wed Oct 25 2014 12:00:00 GMT-0500";
            console.log(app.torneo);
        })
            })
      }
       app.Garca = function (a) {
                                $http.get("http://localhost:8090/torneoLiga")
        .success(function (data) {
                   app.liga = data[0].Ligaarray;
            app.torneo=data[0].Torneoarray;
        })

                      }


      app.Borrar = function (a) {
              var torneo = a;
              $http({
                  url: 'http://localhost:8090/torneo/delete/'+torneo,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: torneo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {


                    app.torneo=data[0].Torneoarray;
                    //$scope.data.splice(index, 1);
                })
          }
             app.isCollapsed=function ($scope) {
  $scope.isCollapsed = false;
}

  })
///////////////////////////////////////
//////////////////////////////////////////
  .controller("EquipoCtrl", function ($http,$scope) {
      var app = this;

      $http.get("http://localhost:8090/equipo")
        .success(function (data) {

          app.equipo = data

            //app.torneo.fecha1="Wed Oct 25 2014 12:00:00 GMT-0500";
            console.log(app.equipo);
        })

  
      app.addEquipo = function (a) {

          var nombre = a.Nombre;
          var apodo= a.Apodo;
          var ciudad = a.Ciudad;
          var estadio = a.Estadio;
          var logourl = a.Logourl;
          var color = a.Color;

          $http({
              url: 'http://localhost:8090/equipo',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Nombre: nombre, Ciudad: ciudad, Apodo: apodo, Logourl:logourl, Color:color},

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
            .success(function (data) {
 
      $http.get("http://localhost:8090/equipo")
        .success(function (data) {

          app.equipo = data

            //app.torneo.fecha1="Wed Oct 25 2014 12:00:00 GMT-0500";
            console.log(app.equipo);
        })
                //$scope.data.splice(index, 1);
            })
      }
       app.Garca = function (a) {
                                $http.get("http://localhost:8090/equipo")
        .success(function (data) {

          app.equipo = data
        })

                      }


      app.Borrar = function (a) {
              var torneo = a;
              $http({
                  url: 'http://localhost:8090/torneo/delete/'+torneo,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: torneo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {


                    app.torneo=data[0].Torneoarray;
                    //$scope.data.splice(index, 1);
                })
          }
             app.isCollapsed=function ($scope) {
  $scope.isCollapsed = false;
}

  })
///////////////////////////////////
.controller("JugadorCtrl", function ($http,$scope) {
      var app = this;

      $http.get("http://localhost:8090/jugadorentity")
        .success(function (data) {

          app.jugador = data[0].Jugadorarray;
          app.posicion= data[0].Posicionarray;

            //app.torneo.fecha1="Wed Oct 25 2014 12:00:00 GMT-0500";
            console.log(app.jugador);
        })

  
      app.addJugador = function (a) {

          var nombre = a.Nombre;
          var apellidopaterno= a.Apellidopaterno;
          var apellidomaterno = a.Apellidomaterno;
          var extra = " ";
          var posicion = a.posicion.Id;
          $http({
              url: 'http://localhost:8090/jugador',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Nombre: nombre, Apellidopaterno: apellidopaterno,Apellidomaterno:apellidomaterno,Extra:extra,Posicion:posicion},

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
            .success(function (data) {
      $http.get("http://localhost:8090/jugadorentity")
        .success(function (data) {

          app.jugador = data[0].Jugadorarray;
          app.posicion= data[0].Posicionarray;

            //app.torneo.fecha1="Wed Oct 25 2014 12:00:00 GMT-0500";
            console.log(app.jugador);
        })
            })
      }
       app.Garca = function (a) {
                                $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

                      }


      app.Borrar = function (a) {
              var torneo = a;
              $http({
                  url: 'http://localhost:8090/torneo/delete/'+torneo,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: torneo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {


                    app.torneo=data[0].Torneoarray;
                    //$scope.data.splice(index, 1);
                })
          }
             app.isCollapsed=function ($scope) {
  $scope.isCollapsed = false;
}

  })
///////////////////////////////////////
/////////////Wizard de Llamada
///////////////////////////////////////
  .controller("WizardCtrl", function ($http,$scope) {
      var app = this;
      app.Servirce= null
      app.datos=null

      $http.get("http://localhost:8090/equipo")
        .success(function (data) {

          app.equipo = data
        })
              $http.get("http://localhost:8090/pais")
        .success(function (data) {

          app.pais = data
        })
        $http.get("http://localhost:8090/equipo")
        .success(function (data) {
          app.equipo = data
        })

  

       app.Make = function (a,b,scope) {
        var restriccion=a;
        var item=b;
        if(a=="Sin Relaciones"&&b=="Pais"){
                                $http.get("http://localhost:8090/pais")
        .success(function (data) {

          app.Service ="http://localhost:8090/pais";
          app.datos=data;
        })


        }else if(a=="Sin Relaciones"&&b=="Liga"){
       $http.get("http://localhost:8090/liga")
        .success(function (data) {

          app.Service ="http://localhost:8090/liga";
          app.datos=data;
        })
        }else if(a=="Sin Relaciones"&&b=="Equipo"){
       $http.get("http://localhost:8090/equipo")
        .success(function (data) {

          app.Service ="http://localhost:8090/equipo";
          app.datos=data;
        })
        }
                      }


      app.Borrar = function (a) {
              var torneo = a;
              $http({
                  url: 'http://localhost:8090/torneo/delete/'+torneo,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: torneo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {


                    app.torneo=data[0].Torneoarray;
                    //$scope.data.splice(index, 1);
                })
          }
             app.isCollapsed=function ($scope) {
  $scope.isCollapsed = false;
}

  })
///////////////////////////////////////
/////////////Controlador de Posicion //
///////////////////////////////////////
.controller("PosicionCtrl", function ($http,$scope) {
      var app = this;

      $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

  
      app.addPosicion = function (a) {

          var nombre = a.Nombre;
          var abreviatura= a.Abreviatura;
          var extra = a.Extra;


          $http({
              url: 'http://localhost:8090/posicion',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Nombre: nombre, Abreviatura:abreviatura,Extra:extra},

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
            .success(function (data) {
        $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

                //$scope.data.splice(index, 1);
            })
      }
       app.Garca = function (a) {
                                $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

                      }


      app.Borrar = function (a) {
              var torneo = a;
              $http({
                  url: 'http://localhost:8090/posicion/delete/'+torneo,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: torneo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {

      $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

                   
                    //$scope.data.splice(index, 1);
                })
          }
             app.isCollapsed=function ($scope) {
  $scope.isCollapsed = false;
}

  })
////////////////////////////////////////
///////////////////////////////////////
/////////////Controlador de Partido //
///////////////////////////////////////
.controller("PartidoCtrl", function ($http,$scope) {
      var app = this;
            $http.get("http://localhost:8090/liga")
        .success(function (data) {

          app.liga = data
        })
              app.changeliga = function ($scope) {
      $http.get("http://localhost:8090/liga/"+$scope.Id)
        .success(function (data) {

          app.torneo = data
        })

    }
          app.changetorneo = function (a, b) {
      $http.get("http://localhost:8090/torneo/"+a.Id)
        .success(function (data) {
          app.local=data[0].Equipos;
          app.visitante=data[0].Equipos;
        })
        }
  $scope.today = function() {
    $scope.dt = new Date();
  };
  $scope.today();

  $scope.clear = function () {
    $scope.dt = null;
  };

  // Disable weekend selection
  $scope.disabled = function(date, mode) {
    return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
  };

  $scope.toggleMin = function() {
    $scope.minDate = $scope.minDate ? null : new Date();
  };
  $scope.toggleMin();

  $scope.open = function($event) {
    $event.preventDefault();
    $event.stopPropagation();

    $scope.opened = true;
  };

  $scope.dateOptions = {
    formatYear: 'yy',
    startingDay: 1
  };

  $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd-MM-yyyy', 'shortDate'];
  $scope.format = $scope.formats[2];
         

  app.addPartido = function (a) {
          var x=app.date.getUTCDate();
          var day = x.toString();
          var y=app.date.getUTCMonth();
          var month = y.toString();
          var z=app.date.getUTCFullYear();
          var year = z.toString();
          var idlocal=app.selectedlocal.Id;
          var idvisitante=app.selectedvisitante.Id
          var jornada="1";
          var idtorneo=app.selecttorneo.Id;
          var fecha ="10-10-2014";


          $http({
              url: 'http://localhost:8090/partido',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Idlocal: idlocal, Idvisitante:idvisitante,Idtorneo:idtorneo,Jornada:jornada,Fecha:app.date},

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
            .success(function (data) {

            })


        }

  })
//////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////
/////////////Controller Agregar jugadores a equipo en torneo///////////////
//////////////////////////////////////////////////////////////////////////
.controller("DatosPartidoCtrl", function ($http,$scope) {
      var app = this;
            $http.get("http://localhost:8090/liga")
        .success(function (data) {

          app.liga = data
        })
              app.changeliga = function ($scope) {
      $http.get("http://localhost:8090/liga/"+$scope.Id)
        .success(function (data) {

          app.torneo = data
        })

    }
          app.changetorneo = function (a, b) {
      $http.get("http://localhost:8090/torneo/"+a.Id)
        .success(function (data) {
          app.equipos=data[0].Equipos;

        })
        }
      app.changeequipo = function (a, b) {
      $http.get("http://localhost:8090/equipo/jugador/All/"+a.selectedequipo.Id)
        .success(function (data) {
          app.jugador=data[0].Jugador;
          app.nojugador=data[0].NoJugador;

        })
        }
        app.addJugador=function(a,b,c){
          var extra=" ";
          $http({
              url: 'http://localhost:8090/torneo/equipo',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Idtorneo: b.Id, Idequipo:a.Id,Extra:extra},

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
        .success(function (data) {
          

                //$scope.data.splice(index, 1);
            })



        }


 })
/////////////////////////////////////////////////////
/////////////////////////////////////////////////////
/////////////Controller Agregar equipo a Torneo///////////////
///////////////////////////////////////////////////////
.controller("EquipoTorneoCtrl", function ($http,$scope) {
    $scope.max = 200;

  $scope.random = function() {
    var value = 100;
    var type;

    if (value < 25) {
      type = 'success';
    } else if (value = 100) {
      type = 'Agregar';
    } else if (value < 75) {
      type = 'warning';
    } else {
      type = 'danger';
    }

    $scope.showWarning = (type === 'danger' || type === 'warning');

    $scope.dynamic = value;
    $scope.type = type;
  };
  $scope.random();

  $scope.randomStacked = function() {
    $scope.stacked = [];
    var types = ['success', 'info', 'warning', 'danger'];

    for (var i = 0, n = Math.floor((Math.random() * 4) + 1); i < n; i++) {
        var index = Math.floor((Math.random() * 4));
        $scope.stacked.push({
          value: Math.floor((Math.random() * 30) + 1),
          type: types[index]
        });
    }
  };
  $scope.randomStacked();
      var app = this;

      $http.get("http://localhost:8090/liga")
        .success(function (data) {

          app.liga = data
        })

      app.changeliga = function ($scope) {
      $http.get("http://localhost:8090/liga/"+$scope.Id)
        .success(function (data) {

          app.torneo = data
        })

                      }
                      
      app.changetorneo = function (a, b) {
      $http.get("http://localhost:8090/torneo/"+a.Id)
        .success(function (data) {
          app.equipostodos=data[0].NoEquipos;
          app.equipostorneo=data[0].Equipos;
        })
        }
      app.addEquipo = function (a,b) {
        var extra=" ";
          $http({
              url: 'http://localhost:8090/torneo/equipo',
              method: "POST",
              transformRequest: function (obj) {
                  var str = [];
                  for (var p in obj)
                      str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                  return str.join("&");
              },
              data: { Idtorneo: b.Id, Idequipo:a.Id,Extra:extra},

              headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
          })
        .success(function (data) {
          app.changetorneo(b,a);

                //$scope.data.splice(index, 1);
            })

        }

       app.Garca = function (a) {
                                $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

                      }


      app.Borrar = function (a) {
              var torneo = a;
              $http({
                  url: 'http://localhost:8090/posicion/delete/'+torneo,
                  method: "POST",
                  transformRequest: function (obj) {
                      var str = [];
                      for (var p in obj)
                          str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                      return str.join("&");
                  },
                  data: { id: torneo },

                  headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
              })
                .success(function (data) {

      $http.get("http://localhost:8090/posicion")
        .success(function (data) {

          app.posicion = data
        })

                   
                    //$scope.data.splice(index, 1);
                })
          }
  })
  .factory("GetUser", function($resource) {
  return $resource("http://localhost:8090/user:id");
})
  .controller("GetUserCtrl", function($scope, Post) {
  Post.query(function(data) {
    $scope.posts = data;
  });
});


})();
angular.module('ui.bootstrap').controller('TabsDemoCtrl', function ($scope) {
  $scope.tabs = [
    { title:'Dynamic Title 1', content:'Dynamic content 1' },
    { title:'Dynamic Title 2', content:'Dynamic content 2', disabled: true }
  ];

  $scope.alertMe = function() {
    setTimeout(function() {
      alert('You\'ve selected the alert tab!');
    });
  };
});
////////////////////////////////////////////
angular.module('ui.bootstrap').controller('AccordionDemoCtrl', function ($scope) {
  $scope.oneAtATime = true;

  $scope.groups = [
    {
      title: 'Dynamic Group Header - 1',
      content: 'Dynamic Group Body - 1'
    },
    {
      title: 'Dynamic Group Header - 2',
      content: 'Dynamic Group Body - 2'
    }
  ];

  $scope.items = ['Item 1', 'Item 2', 'Item 3'];

  $scope.addItem = function() {
    var newItemNo = $scope.items.length + 1;
    $scope.items.push('Item ' + newItemNo);
  };

  $scope.status = {
    isFirstOpen: false,
    isFirstDisabled: false
  };
});