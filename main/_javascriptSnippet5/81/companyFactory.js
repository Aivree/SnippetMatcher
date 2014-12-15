angular.module('companyFactory', [])
    .factory('companyFactory', function ($http) {
        var factory = {};
        factory.getCompanies = function (callbacksuccess, callbackerror) {
            $http.get(ajaxurl+'json/getallcompanies', {}).success(callbacksuccess).error(callbackerror);
        };
        
        factory.deleteCompany = function (id, callbacksuccess, callbackerror) {
        console.log(id);
        $http.get(ajaxurl+'json/deletecompany', {
            params: {
                companyid: id
            }
        }).success(callbacksuccess).error(callbackerror);
    };
//Get Single Company
                factory.getSingleCompany = function (id, callbacksuccess, callbackerror) {
            console.log(id);
            $http.get(ajaxurl+'json/getsinglecompany', {
                params: {
                    id: id
                }
            }).success(callbacksuccess).error(callbackerror);
        };

        
        
        
        
    factory.addCompany = function (company, callbacksuccess, callbackerror) {
        console.log(company);
        $http({
                method: "post",
                url: ajaxurl+'json/addcompany',
                data: {
                user: "Vishal",
                name: company.name,
                email: company.email,
                contact: company.contact,
                line1: company.address,
                city: company.city,
                state: company.state,
                pincode: company.pincode,
                country: company.country,
                landmark: company.landmark,
                vat: company.vat,
                tin: company.tin,
                pan: company.pan,
                logo: company.logo
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                }

        }).success(callbacksuccess).error(callbackerror);
 };
//Editing Companies        
        
        factory.editCompany = function (company, callbacksuccess, callbackerror) {
            console.log(company);
            $http({
                method: "post",
                url: ajaxurl+'json/editcompany',
                data: {
                    id: company.id,
                    user: "Vishal",
                    name: company.name,
                    email: company.email,
                    contact: company.contact,
                    line1: company.address,
                    city: company.city,
                    state: company.state,
                    pincode: company.pincode,
                    country: company.country,
                    landmark: company.landmark,
                    vat: company.vat,
                    tin: company.tin,
                    pan: company.pan,
                    logo: company.logo
                },
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                transformRequest: function (obj) {
                    var str = [];
                    for (var p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                    return str.join("&");
                }
            }).success(callbacksuccess).error(callbackerror);
        };
        
        
        
        
        
        
        
        return factory;
    });
