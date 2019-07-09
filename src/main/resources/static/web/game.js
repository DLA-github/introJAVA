Vue.config.devtools = true

var params = window.location.search.split("=");
console.log(params);
const id = params[1];



console.log(id);

var game = new Vue({
    el: "#app",
    data: {
        game: [],
        url: "/api/game_view/" + id,
        positions: [],
        ships: [],
        salvoes: [],
        userId: 0

    },

    created() {
        this.getData();
        this.dashboard();
    },

    methods: {
        getData() {
            fetch(this.url).then((response) => {
                response.json().then((data) => {
                    this.game = data;
                    console.log(data);
                    let currentGamePlayer = this.game.gamePlayers.filter(gp => gp.id == id);

                    this.userId = currentGamePlayer[0].player.id;

                    let locationsGP = this.game.ships.filter(gp => gp.gamePlayer == id);
                    this.ships = locationsGP[0].ships;

                    let salvoLocations = this.game.salvoes.filter(gp => gp.user.id == this.userId);
                    this.salvoes = salvoLocations[0].salvos;

                    let salvoesEnemy = this.game.salvoes.filter(gp => gp.user.id != this.userId);

                    this.showShips();
                    this.showSalvos();
                    this.checkHit(salvoesEnemy[0].salvos);

                })
            });
        },

        showShips() {
            let table = document.querySelectorAll("#ships > div");
            this.ships.forEach(ship => {
                ship.Locations.forEach(s => {
                    table.forEach(div => {
                        if (div.innerHTML == s) {
                            div.classList.add("shipPart");
                        }
                    })
                });

            });
        },
        showSalvos() {
            let table = document.querySelectorAll("#salvo > div");
            this.salvoes.forEach(salvo => {

                salvo.Locations.forEach(s => {
                    table.forEach(div => {
                        if (div.innerHTML == s) {
                            div.classList.add("salvo");
                        }
                    })
                });

            });
        },
        checkHit(turns) {
            //recibe salvo de player contrario y turno y compara con posiciones del barco para establecer resultado
            //puede ser AGUA, TOCADO y HUNDIDO

            console.log(turns);
            this.ships.forEach(ship => {
                ship.Locations.forEach(pos => {
                    turns.forEach(turn => {
                        turn.Locations.forEach(psalvo => {
                            if (psalvo == pos) {
                                console.log("pos:" + pos + ", is Hit!!");
                            } else(
                                console.log()
                            )
                        })
                    })
                })
            })
            // this.ships.Locations.forEach(pos => {
            //     if (pos != salvo) {
            //         alert("Fail!!");
            //     } else if (pos == salvo && numbHits < this.ship.hits) {
            //         alert("Touched!!")
            //     } else {
            //         alert("Sunken!!")
            //     }
            // })

        },

        dashboard() {
            let abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

            abc.forEach(a => {
                for (var i = 0; i < 10; i++) {
                    this.positions.push(a + i);
                }
            });
        }

    }
});