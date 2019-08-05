Vue.config.devtools = true

var params = window.location.search.split("=");

const id = params[1];

var stompClient = null;

var turn = 1;



function resize() {
    // console.log(game.ships.length);
    // if (game.ships.length > 0) {
    //     setTimeout(function () {
    //         location.reload();
    //     }, 30000)
    // }

    // if (window.screen.width <= 700) {

    //     let tShip = document.querySelectorAll("#ships");
    //     let iTShip = document.querySelectorAll("#ships>*");
    //     console.log(iTShip)
    //     let tSalvo = document.querySelectorAll("#salvo");
    //     let iTSalvo = document.querySelectorAll("#salvo>*");

    //     tShip.forEach(a => {
    //         a.style.transform = "scale(.95)"
    //     });
    //     iTShip.forEach(a => {
    //         a.style.transform = "scale(.95)"
    //     });
    //     tSalvo.forEach(a => {
    //         a.style.transform = "scale(.95)"
    //     });
    //     iTSalvo.forEach(a => {
    //         a.style.transform = "scale(.95)"
    //     });

    // }
}

window.onload = resize;

var game = new Vue({
    el: "#app",
    data: {
        imgs: ["./img/battleship-07.png", "./img/battleship-08.png", "./img/battleship-09.png", "./img/battleship-10.png", "./img/640px-Submarine.svg.png"],
        gameId: 0,
        url: "/api/game_view/" + id,
        positions: [],
        gameEnemy: 0,
        ships: [],
        shipType: "",
        salvoes: [],
        player: "",
        dateGame: "",
        message: "",
        checked: false,
        lives: 0,
        send: false,
        sendSalvo: false,
        currentSalvo: {
            turn: 0,
            salvoLocations: []
        },
        oldTurn: 0,
        enemy: "",
        keys: [],
        enemyTurn: 0,
        ready: false,

    },

    created() {
        this.getData();
        this.dashboard();
        this.connectSocket();
    },

    methods: {
        getData() {
            console.log("getData");
            fetch(this.url).then((response) => {
                response.json().then((data) => {

                    if (data.Info) {
                        if (data.Info.myShips.ships.ships.length > 0) {
                            this.send = true;
                        }

                        this.ships = data.Info.myShips.ships.ships
                        this.salvoes = data.Info.mySalvoes.salvoes.salvos.sort((a, b) => {
                            return a.turn - b.turn
                        });
                        if (this.salvoes.length > 0) {
                            this.oldTurn = this.salvoes.length;
                            this.currentSalvo.turn = this.oldTurn + 1
                        } else {
                            this.oldTurn = 0;
                            this.currentSalvo.turn = this.oldTurn + 1
                        }
                        this.gameId = data.Info.DataGP.dataGame.id;
                        this.dateGame = data.Info.DataGP.created;
                        this.player = data.Info.DataGP.dataGame.player.user;
                        if (data.Info.DataGP.EnemyGame) {
                            this.enemy = data.Info.DataGP.EnemyGame.player.user;
                            this.gameEnemy = data.Info.DataGP.EnemyGame.id;
                        }

                        this.showShips();
                        this.getStatus();

                    } else {
                        this.message = data.error
                    }

                })
            });
        },
        connectSocket() {
            var socket = new SockJS('/salvo-socket');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, this.onConnected, this.onError);
        },
        onConnected() {
            console.log("onConnected");
            stompClient.subscribe('/connection/info', this.onChange);
            stompClient.subscribe('/connection/ready', this.onReady);
            this.sendSocket();

        },
        onError(error) {
            alert('Could not connect to WebSocket server. Please refresh this page to try again!')
        },

        sendSocket() {
            console.log("envío request");
            stompClient.send("/socket/request", {}, this.gameEnemy);
        },

        sendReady() {
            stompClient.send("/socket/ready", {}, this.gameId);
        },

        onChange(payload) {
            this.getData();
            this.enemyTurn = payload.body;
            if (this.sendSalvo == true) {
                this.sendSalvo = false;
                this.currentSalvo.salvoLocations = [];
                document.getElementById("salvoSender").style.display = "flex";
                this.showSalvos();
            }

        },
        onReady(payload) {
            this.ready = true;
            this.getData();
            this.showSalvos();
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

            let cs = {
                type: this.shipType,
                locations: [],
                life: this.lives,
                hits: [],
                sunk: false
            }

            //////locations Ship////////////////////////////
            let abc = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"];

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



        },
        addSalvoLocation(event) {
            //this.sendSocket();

            let div = document.getElementById(event.target.id);
            let id = event.target.id;
            if (Math.abs(this.enemyTurn - this.currentSalvo.turn) <= 1 && this.sendSalvo == false) {
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
            } else {
                console.log("no pinto");
            }

        },

        showShips() {
            if (this.send) {
                let tShip = document.querySelectorAll("#ships");
                tShip.forEach(a => {
                    a.style.transform = "scale(.60)"
                });
            }


            let table = document.querySelectorAll("#ships > div");
            this.ships.forEach(ship => {
                let img = "";
                let vertical = false;
                let hits = 0;
                let id = "";

                switch (ship.type) {
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
                        if (!ship.hits.includes(loc)) {
                            if (div.id == loc) {
                                div.classList.add("shipPart")
                            }
                        } else {
                            if (div.id == loc) {
                                div.classList.add("hit");
                            }
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
                });


            });

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
                    if (data.error) {
                        alert(data.error);
                    } else {
                        this.send = true;
                        this.sendReady();
                        document.getElementById("shipSender").style.display = "none";
                        document.getElementById("reset").style.display = "none";
                    }
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);
                });
        },
        sendSalvos(e) {
            let ourData = this.currentSalvo;

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
                }).then(data => {

                    if (data.error) {
                        alert(data.error);
                    } else if (data.message) {
                        let message = data.message;
                        if (message.includes("Game")) {
                            var modal = document.getElementById("myModal");

                            // Get the button that opens the modal
                            var btn = document.getElementById("myBtn");

                            // Get the <span> element that closes the modal
                            var span = document.getElementsByClassName("close")[0];

                            // When the user clicks the button, open the modal 
                            btn.onclick = function () {
                                modal.style.display = "block";
                            }

                            // When the user clicks on <span> (x), close the modal
                            span.onclick = function () {
                                modal.style.display = "none";
                            }

                            // When the user clicks anywhere outside of the modal, close it
                            window.onclick = function (event) {
                                if (event.target == modal) {
                                    modal.style.display = "none";
                                }
                            }
                        } else {
                            this.sendSocket();
                            this.sendSalvo = true;
                            document.getElementById("salvoSender").style.display = "none";
                        }
                    }
                })
                .catch(function (error) {
                    console.log('Request failure: ', error);

                });


        },
        getStatus() {

            let obj = [];
            let key = [];
            this.keys = [];
            let setObj = new Set;
            this.salvoes.forEach(salvo => {
                obj.push(salvo.Success);
                setObj = new Set(obj);

            });
            setObj.forEach(o => {
                key.push(Object.keys(o));
            });
            this.keys = key;


        },
        showSalvos() {
            let obj = [];
            let key = [];
            this.salvoes.forEach(salvo => {
                obj.push(salvo.Hits);

            });
            obj.forEach(o => {
                key.push(Object.keys(o));
            });

            key.forEach(k => {
                k.forEach(hit => {
                    document.getElementById("salvo" + hit).classList.add('hit');
                })
            })

            this.salvoes.forEach(salvo => {
                salvo.Locations.forEach(fail => {
                    if (!document.getElementById("salvo" + fail).classList.contains('hit')) {
                        document.getElementById("salvo" + fail).classList.add('fail');
                    }

                })
            })

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

    },
    updated() {
        this.$nextTick(this.showSalvos);
    },

    watch: {
        enemyTurn(val, oldVal) {
            if (oldVal > val) {
                this.enemyTurn = oldVal;
            }
            // this.getData();
            // this.showSalvos();
            // this.currentSalvo.Locations = [];
            // document.getElementById("salvoSender").style.display = "flex";
        },
        ships(val, oldVal) {
            console.log(val);
        }
    }




});