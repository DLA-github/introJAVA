var register = new Vue({
    el: "#app",
    data: {
        userName: "",
        password: ""
    },
    methods: {
        register() {
            var ourData = {
                userName: this.userName,
                password: this.password
            }
            fetch("/api/players", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    method: 'POST',
                    body: this.getBody(ourData)
                })
                .then(function (data) {
                    setTimeout(function () {
                        register.login();
                    }, 2000)

                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },
        getBody(json) {
            var body = [];
            for (var key in json) {
                var encKey = encodeURIComponent(key);
                var encVal = encodeURIComponent(json[key]);
                body.push(encKey + "=" + encVal);
            }
            return body.join("&");
        },
        login() {
            var ourData = {
                name: this.userName,
                pwd: this.password
            }
            fetch("/api/login", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    method: 'POST',
                    body: this.getBody(ourData)
                })
                .then(function (data) {
                    console.log("IN");
                    setTimeout(function () {
                        window.location = "./games.html";
                    }, 2000)

                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },
        back() {
            window.location = "./games.html";
        }
    }






});