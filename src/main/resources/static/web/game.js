Vue.config.devtools = true

var params = window.location.search.split("=");

const id = params[1];



var game = new Vue({
    el: "#app",
    data: {
        imgs: ["./img/battleship-07.png", "./img/battleship-08.png", "./img/battleship-09.png", "./img/battleship-10.png", "./img/640px-Submarine.svg.png"],
        gameId: 0,
        url: "/api/game_view/" + id,
        positions: [],
        ships: [],
        shipType: "",
        salvoes: [],
        player: "",
        dateGame: "",
        message: "",
        checked: false,
        lives: 0,
        send: false,
        currentSalvo: {
            turn: "",
            salvoLocations: []
        },
        oldTurn: 0

    },

    created() {
        this.getData();
        this.dashboard();
    },

    methods: {
        getData() {
            fetch(this.url).then((response) => {
                response.json().then((data) => {

                    if (data.Info) {
                        if (data.Info.myShips.ships.ships.length > 0) {
                            this.send = true;
                        }
                        console.log(data);
                        this.ships = data.Info.myShips.ships.ships
                        this.salvoes = data.Info.mySalvoes.salvoes.salvos.sort((a, b) => {
                            return a.turn - b.turn
                        });
                        this.oldTurn = this.salvoes[this.salvoes.length - 1].turn;
                        this.gameId = data.Info.DataGP.dataGame.id;
                        this.dateGame = data.Info.DataGP.created;
                        this.player = data.Info.DataGP.dataGame.player.user;

                        this.showShips();
                        this.showSalvos();

                        if (data.Info.Enemy.salvoesEnemy) {
                            let salvoesEnemy = data.Info.Enemy.salvoesEnemy.salvos;
                            this.checkHit(salvoesEnemy);
                        }


                    } else {
                        this.message = data.error
                    }

                })
            });
        },
        dragStart(wich, ev) {
            this.shipType = "";
            ev.dataTransfer.setData("text", ev.target.id);
            switch (ev.target.id) {
                case 'z0':
                    this.shipType = "Destroyer";
                    break;
                case 'z1':
                    this.shipType = "Carrier";
                    break;
                case 'z2':
                    this.shipType = "Battleship";
                    break;
                case 'z3':
                    this.shipType = "Cruiser";
                    break;
                case 'z4':
                    this.shipType = "Submarine";
                    break;
            }
            let img = document.getElementById(event.target.id);
            if (img.classList.contains("vertical")) {
                this.checked = true;
            } else {
                this.checked = false;
            }
        },
        drop(event) {
            let cs = {
                type: this.shipType,
                locations: []
            }

            var data = event.dataTransfer.getData("text");
            let i = data.split("").pop();

            if (i == 0) {
                this.lives = 2;
            }
            if (i == 1) {
                this.lives = 5;
            }
            if (i == 2) {
                this.lives = 4;
            }
            if (i == 3) {
                this.lives = 3;
            }
            if (i == 4) {
                this.lives = 4;
            }

            //////locations Ship////////////////////////////
            let abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];
            let ship = [];
            let id = event.target.id;

            if (id.includes("z")) {
                return;
            }
            let letter = id.split("").shift();
            let j = parseInt(id.split("").pop());
            let limit = j + this.lives;

            if (!this.checked) {
                for (j; j < limit; j++) {
                    if (j > 9) {
                        return;
                    }
                    for (let w = 0; w < this.ships.length; w++) {
                        for (let y = 0; y < this.ships[w].locations.length; y++) {
                            if (this.ships[w].locations[y] == letter + j) {
                                return;
                            }
                        }
                    }
                    cs.locations.push(letter + j);
                }

                this.ships.push(cs);

            } else {
                let v = abc.indexOf(letter);
                let limits = v + this.lives;

                for (v; v < limits; v++) {
                    if (v > 9) {
                        return;
                    }
                    for (let w = 0; w < this.ships.length; w++) {
                        for (let y = 0; y < this.ships[w].locations.length; y++) {
                            if (this.ships[w].locations[y] == abc[v] + j) {
                                return;
                            }
                        }
                    }
                    cs.locations.push(abc[v] + j);
                }
                this.ships.push(cs);
            }

            let div = document.getElementById("div" + i);
            div.style.display = 'none';
            event.target.appendChild(document.getElementById(data));
            document.getElementById(data).setAttribute('draggable', false);

            console.log(this.ships);

        },
        addSalvoLocation(event) {
            this.currentSalvo.turn = this.oldTurn + 1;
            let div = document.getElementById(event.target.id);
            let id = event.target.id;


            if (!div.classList.contains("salvo") && this.currentSalvo.salvoLocations.length < 5) {
                this.currentSalvo.salvoLocations.push(div.innerText);
                div.classList.add("salvo");
            } else if (div.classList.contains("salvo") && this.currentSalvo.salvoLocations.length > 0) {
                if (this.currentSalvo.salvoLocations.includes(div.innerText)) {
                    let pos = this.currentSalvo.salvoLocations.indexOf(div.innerText);
                    this.currentSalvo.salvoLocations.splice(pos, 1);
                    div.classList.remove("salvo");
                }
            }
            console.log(this.currentSalvo)
        },

        showShips() {

            let table = document.querySelectorAll("#ships > div");
            this.ships.forEach(ship => {


                let img = "";
                let vertical = false;
                let hits = 0;
                let id = "";

                switch (ship.Type) {
                    case 'Carrier':
                        img = this.imgs[1];
                        hits = 5;
                        id = "z1"
                        break;
                    case 'Cruiser':
                        img = this.imgs[3];
                        hits = 3;
                        id = "z3"
                        break;
                    case 'Battleship':
                        img = this.imgs[2];
                        hits = 4;
                        id = "z2";
                        break;
                    case 'Submarine':
                        img = this.imgs[4];
                        hits = 4;
                        id = "z4"
                        break;
                    case 'Destroyer':
                        img = this.imgs[0];
                        hits = 2;
                        id = "z0"
                        break;
                }
                //verify ship's orientation
                if (ship.Locations[0].split("").pop() == ship.Locations[1].split("").pop()) {
                    vertical = true;
                }

                //create img for ship obtained
                let s = document.createElement("img");
                s.setAttribute("src", img);
                s.setAttribute("id", id);

                table.forEach(div => {
                    //paint locations as shipPart
                    ship.Locations.forEach(loc => {
                        if (div.id == loc) {
                            div.classList.add("shipPart")
                        }
                    })
                    //Place img with the correct orientation in the right place
                    if (div.id == ship.Locations[0] && vertical == false) {

                        div.append(s);
                    } else if (div.id == ship.Locations[0] && vertical == true) {
                        s.setAttribute("class", "vertical")
                        s.setAttribute('style', 'transform:rotate(-90deg)');
                        div.append(s);
                    }
                })


            });
        },
        showSalvos() {
            let table = document.querySelectorAll("#salvo > div");
            this.salvoes.forEach(salvo => {
                salvo.Locations.forEach(s => {
                    table.forEach(div => {
                        if (div.innerText == s) {
                            div.classList.add("salvo");
                        }
                    })
                });

            });
        },
        checkHit(turns) {
            //recibe salvo de player contrario y turno y compara con posiciones del barco para establecer resultado
            //puede ser AGUA, TOCADO y HUNDIDO

            this.ships.forEach(ship => {
                ship.Locations.forEach(pos => {
                    turns.forEach(turn => {
                        turn.Locations.forEach(psalvo => {
                            if (psalvo == pos) {
                                console.log("pos:" + pos + ", is Hit!!");
                            } else(
                                console.log("Noop, pos: " + pos + " fail!!")
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
        },
        sendShips(e) {
            console.log(e.target.id);
            console.log("enviando");
            let ourData = [];
            this.ships.forEach(s => {
                ourData.push(s);
            })
            fetch("/api/games/players/" + id + "/ships", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    method: 'POST',
                    body: JSON.stringify(ourData)
                })
                .then(function (r) {
                    return r.json();
                    //window.location = "./games.html";
                }).then(data => {
                    console.log(data);
                    document.getElementById("shipSender").style.display = "none";
                    document.getElementById("reset").style.display = "none";
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },
        sendSalvos(e) {
            console.log(e.target.id);
            console.log("enviando");
            let ourData = this.currentSalvo;
            console.log(ourData);
            fetch("/api/games/players/" + id + "/salvos", {
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    method: 'POST',
                    body: JSON.stringify(ourData)
                })
                .then(function (r) {
                    return r.json();
                    //window.location = "./games.html";
                }).then(data => {
                    console.log(data);
                    location.reload();
                    // document.getElementById("shipSender").style.display = "none";
                    // document.getElementById("reset").style.display = "none";
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },

        rotate(ev) {

            let el = ev.target;
            if (el.checked == true) {
                var img = document.getElementById(el.previousElementSibling.id);
                img.setAttribute('style', 'transform:rotate(-90deg)');
                img.classList.add("vertical");

            } else {
                var img = document.getElementById(el.previousElementSibling.id);
                img.setAttribute('style', 'transform:rotateX(deg)');
                img.classList.remove("vertical");
            }
        },
        reset(e) {
            location.reload();
        }

    }
});