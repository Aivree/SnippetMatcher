angular.module('vendorFactory', [])
    .factory('vendorFactory', function ($http) {
        var vendor = {};
        vendor.getVendors = function (callbacksuccess, callbackerror) {
            $http.get(ajaxurl+'json/getallvendors', {}).success(callbacksuccess).error(callbackerror);
        };

        vendor.delete = function (id, callbacksuccess, callbackerror) {
            console.log(id);
            $http.get(ajaxurl+'json/deletevendor', {
                params: {
                    vendorid: id
                }
            }).success(callbacksuccess).error(callbackerror);
        };

        vendor.getSingleVendor = function (id, callbacksuccess, callbackerror) {
            console.log(id);
            $http.get(ajaxurl+'json/getsinglevendor', {
                params: {
                    id: id
                }
            }).success(callbacksuccess).error(callbackerror);
        };
//Adding a Vendor
        vendor.addVendor = function (vendor, callbacksuccess, callbackerror) {
            console.log(vendor);
            $http({
                method: "post",
                url: ajaxurl+'json/addvendor',
                data: {
                    companyid: vendor.company,
                    name: vendor.name,
                    email: vendor.email,
                    contact: vendor.contact,
                    line1: vendor.address,
                    city: vendor.city,
                    state: vendor.state,
                    pincode: vendor.pincode,
                    country: vendor.country,
                    landmark: vendor.landmark,
                    vat: vendor.vat,
                    tin: vendor.tin,
                    pan: vendor.pan,
                    logo: vendor.logo
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
//Editing Vendor 
        vendor.editVendor = function (vendor, callbacksuccess, callbackerror) {
            console.log(vendor);
            $http({
                method: "post",
                url: ajaxurl+'json/editvendor',
                data: {
                    id: vendor.id,
                    companyid: vendor.company,
                    name: vendor.name,
                    email: vendor.email,
                    contact: vendor.contact,
                    line1: vendor.address,
                    city: vendor.city,
                    state: vendor.state,
                    pincode: vendor.pincode,
                    country: vendor.country,
                    landmark: vendor.landmark,
                    vat: vendor.vat,
                    tin: vendor.tin,
                    pan: vendor.pan,
                    logo: vendor.logo
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

        
        
        return vendor;
    });