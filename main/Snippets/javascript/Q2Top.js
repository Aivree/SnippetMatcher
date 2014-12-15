$http({
    method: 'POST',
    url: url,
    data: $.param({fkey: "key"}),
    headers: {'Content-Type': 'application/x-www-form-urlencoded'}
})