Vue.config.devtools = true

var games = new Vue({
    el: "#app",
    data: {
        allGames: [],
        gpID: [],
        urlgames: "/api/games",
        urlplayers: "/api/leaderBoard",
        allPlayers: [],
        player: null,
        show: false,
        totalGames: []

    },

    created() {
        this.getDataGames();
        this.getDataPlayers();
    },

    methods: {
        getDataGames() {
            fetch(this.urlgames).then((response) => {
                response.json().then((data) => {
                    console.log(data);
                    if (data.length > 1) {
                        this.totalGames = data[1].othergames;
                        this.allGames = data[1].games;
                        this.player = data[0];
                        this.allGames.forEach(game => {
                            game.gamePlayers.forEach(gp => {
                                if (gp.player.user == this.player.playerName) {
                                    this.gpID.push(gp.id);
                                }
                            })
                        })
                    } else {
                        this.allGames = data[0].games;
                    }
                    console.log(this.allGames);
                });
            });

        },
        getDataPlayers() {
            fetch(this.urlplayers).then((response) => {
                response.json().then((data) => {
                    console.log(data);
                    let players = data;
                    this.allPlayers = players.sort((a, b) => {
                        a.totalGames - b.totalGames;
                    })

                });
            });
        },

        logout() {

            fetch("/api/logout", {
                    method: 'POST',
                })
                .then(function (data) {
                    console.log("OUT");

                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },

        newGame() {
            fetch("/api/games", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    method: 'POST',
                })
                .then(function (data) {
                    console.log(data);
                    return data.json()
                }).then((data) => {
                    console.log(data);
                    window.location = ("./game.html?gp=" + data.gpid);
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },
        join(e) {
            console.log(e);
            let id = e.target.attributes[0].value;
            fetch("/api/game/" + id + "/players", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    method: 'POST',
                })
                .then(function (data) {
                    return data.json()
                }).then((data) => {
                    console.log(data);
                    if (data.gpid) {
                        window.location = "./game.html?gp=" + data.gpid;
                    }
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },

    }
});