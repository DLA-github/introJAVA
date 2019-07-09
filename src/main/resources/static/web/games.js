Vue.config.devtools = true

var games = new Vue({
    el: "#app",
    data: {
        allGames: [],
        urlgames: "/api/games",
        urlplayers: "/api/players",
        allPlayers: []
    },

    created() {
        this.getDataGames();
        this.getDataPlayers();
    },

    methods: {
        getDataGames() {
            fetch(this.urlgames).then((response) => {
                response.json().then((data) => {
                    this.allGames = data;

                });
            });

        },
        getDataPlayers() {
            fetch(this.urlplayers).then((response) => {
                response.json().then((data) => {
                    let players = data;
                    this.allPlayers = players.sort((a, b) => {
                        a.totalGames - b.totalGames;
                    })
                    console.log(this.allPlayers);
                });
            });
        }


    }
});