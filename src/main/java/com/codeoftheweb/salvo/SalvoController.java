package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.io.ObjectStreamClass;
import java.util.*;
import java.util.stream.Collectors;

@RestController


public class SalvoController {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(value="api/players", method = RequestMethod.POST)

    public ResponseEntity<Object> register(String userName, String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (repoPlayer.findByUserName(userName) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        repoPlayer.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Autowired
    private GameRepository repoGame;
     @Autowired
    private PlayerRepository repoPlayer;

    @Autowired
    private GamePlayerRepository repoGamePlayer;

    @Autowired
    private ShipRepository repoShip;

    @Autowired
    private SalvoRepository repoSalvo;

    @Autowired
    private ScoreRepository repoScore;


    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    @RequestMapping(value="api/games", method = RequestMethod.GET)

    public List<Map> getPlayerGames(Authentication authentication) {

        List<Object> gamesID = new ArrayList<>();
        Map<String, Object> dataGames= new HashMap<>();
        List<Map> data = new ArrayList<>();


        if(!isGuest(authentication)){

            List<GamePlayer> gamesPlayers = repoGamePlayer.findAll();


            List<Map> othergames = new ArrayList<>();
            List<Game> oGames = new ArrayList<>();

            List<GamePlayer>othergps = gamesPlayers
                    .stream().filter(gp->!gp.getPlayer().getUserName().equals(authentication.getName())).collect(Collectors.toList());

            for(GamePlayer gp: othergps){
                Map<String,Object> oGameInfo = new HashMap<>();
                oGameInfo.put("gameID",gp.getGame().getId());
                oGameInfo.put("Players",gp.getGame().getGamePlayers().size());
                oGameInfo.put("Enemy",gp.getPlayer().getUserName());
                othergames.add(oGameInfo);
            }

            Set<Map> totalGames = new HashSet<>(othergames);

            Player player = repoPlayer.findByUserName(authentication.getName());

            List<GamePlayer> myGamesPlayers = new ArrayList<>();

            gamesPlayers.stream().forEach(gamePlayer -> {
                if (gamePlayer.getPlayer().getUserName().equals(authentication.getName())) {
                    myGamesPlayers.add(gamePlayer);
                }
            });

            List<Game> myGames = new ArrayList<>();

            myGamesPlayers.forEach(gamePlayer -> {
                myGames.add(gamePlayer.getGame());
            });
            Map<String,Object> playerdata = new HashMap<>();

            playerdata.put("playerID",player.getId());
            playerdata.put("playerName",player.getUserName());

            myGames.stream().forEach(game -> {
                Map<String, Object> gameInfo = new HashMap<>();
                List<Map> dataGP = new ArrayList<>();
                gameInfo.put("id", game.getId());
                gameInfo.put("created", game.getTime());
                Set<GamePlayer> gamePlayer;
                gamePlayer = game.getGamePlayers();
                gamePlayer.stream().forEach(gameP -> {
                    dataGP.add(getDataGP(gameP));
                });
                gameInfo.put("user",authentication.getDetails());
                gameInfo.put("gamePlayers", dataGP);
                gamesID.add(gameInfo);

            });
            data.add(playerdata);
            dataGames.put("othergames",totalGames);
            dataGames.put("games",gamesID);
            data.add(dataGames);

        }
        else{
            List<Game> games = repoGame.findAll();
            games.stream().forEach(game -> {
                Map<String, Object> gameInfo = new HashMap<>();
                List<Map> dataGP = new ArrayList<>();
                gameInfo.put("id", game.getId());
                gameInfo.put("created", game.getTime());
                Set<GamePlayer> gamePlayer;
                gamePlayer = game.getGamePlayers();
                gamePlayer.stream().forEach(gameP -> {
                    dataGP.add(getDataGP(gameP));
                });
                gameInfo.put("gamePlayers", dataGP);
                gamesID.add(gameInfo);

            });

            dataGames.put("games", gamesID);
            data.add(dataGames);
        }
        return data;
    }

    @RequestMapping(value="api/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> createGame (Authentication authentication) {

        if (isGuest(authentication)) {
            return new ResponseEntity<>(sendInfo("Error","Please Log"), HttpStatus.UNAUTHORIZED);
        }
        else{
            Player player = repoPlayer.findByUserName(authentication.getName());
            Date date = new Date();
            Game game = new Game(date);
            GamePlayer gamePlayer = new GamePlayer(game,player,date);
            repoGame.save(game);
            repoGamePlayer.save(gamePlayer);

            return new ResponseEntity<>(sendInfo("gpid",gamePlayer.getId()), HttpStatus.CREATED);
        }

    }



    private Map<String,Object> getDataGP(GamePlayer gamePlayer){

        Game game = gamePlayer.getGame();

        Map<String,Object> gamesPlay = new HashMap<>();

        Map<String,Object> halfPlayer = new HashMap<>();



        halfPlayer.put("id",gamePlayer.getPlayer().getId());
        halfPlayer.put("user",gamePlayer.getPlayer().getUserName());
        halfPlayer.put("score",gamePlayer.getPlayer().getScore(game));



        gamesPlay.put("id",gamePlayer.getId());
        gamesPlay.put("player",halfPlayer);

        return gamesPlay;
    }

    @RequestMapping("api/game_view/{nn}")
    public ResponseEntity<Map<String, Object>> getGamePlayer(@PathVariable long nn, Authentication authentication) {


        GamePlayer gamePlayer = repoGamePlayer.getOne(nn);
        Map<String,Object> gameInfo = new HashMap<>();

        if(authentication.getName().equals(gamePlayer.getPlayer().getUserName())) {


            Map<String, Object> dataGP = new HashMap();
            Map<String, Object> dataShips = new HashMap();
            Map<String, Object> dataSalvoes = new HashMap();


            Game game = gamePlayer.getGame();

            Set<GamePlayer> gps = game.getGamePlayers();

            GamePlayer gpEnemy = new GamePlayer();

            for (GamePlayer gp : gps) {
                if (gp.getId() != nn) {
                     gpEnemy = gp;
                    dataGP.put("EnemyGame", getDataGP(gpEnemy));
              }
            }


            dataGP.put("created", gamePlayer.getGame().getTime());
            dataGP.put("id", gamePlayer.getGame().getId());
            dataGP.put("dataGame", getDataGP(gamePlayer));

            dataShips.put("ships", getShipsData(gamePlayer));
            dataSalvoes.put("salvoes", getSalvoesData(gamePlayer));


            gameInfo.put("mySalvoes",dataSalvoes);

            gameInfo.put("myShips",dataShips);
            gameInfo.put("DataGP",dataGP);

            return new ResponseEntity<>(sendInfo("Info", gameInfo), HttpStatus.ACCEPTED);

        }
        return new ResponseEntity<>(sendInfo("error", "Not allowed") , HttpStatus.FORBIDDEN);
    }

     @RequestMapping(value="api/game/{nn}/players", method = RequestMethod.POST)
     public ResponseEntity<Map<String, Object>> joinGame (@PathVariable long nn,Authentication authentication){

        Game game = repoGame.findById(nn);

        if(isGuest(authentication)){
            return new ResponseEntity<>(sendInfo("error","Please Login"), HttpStatus.UNAUTHORIZED);
        }

        else if (!(repoGame.existsById(nn))){
            return new ResponseEntity<>(sendInfo("error","No such Game"), HttpStatus.FORBIDDEN);
        }

        else if (game.getGamePlayers().size()>1) {
            return new ResponseEntity<>(sendInfo("error","Game Full"),HttpStatus.FORBIDDEN);
        }

         Player player = repoPlayer.findByUserName(authentication.getName());
         Date date = new Date();
         GamePlayer gamePlayer = new GamePlayer(game,player,date);
         repoGamePlayer.save(gamePlayer);

         return new ResponseEntity<>(sendInfo("gpid",gamePlayer.getId()), HttpStatus.CREATED);

     }



    @RequestMapping(value="api/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShips (@PathVariable long gamePlayerId,@RequestBody List<Ship> ships,
                                                         Authentication authentication){

        GamePlayer gamePlayer = repoGamePlayer.findById(gamePlayerId);

        if(isGuest(authentication)){
            return new ResponseEntity<>(sendInfo("error","Please Login"), HttpStatus.UNAUTHORIZED);
        }

        else if (!(repoGamePlayer.existsById(gamePlayerId))){
            return new ResponseEntity<>(sendInfo("error","No game player with the given ID"), HttpStatus.UNAUTHORIZED);
        }

        else if (!gamePlayer.getPlayer().getUserName().equals(authentication.getName())) {
            return new ResponseEntity<>(sendInfo("error","current user is not the game player the ID references"),HttpStatus.UNAUTHORIZED);
        }

        else if(gamePlayer.getShips().size()>0){
            return new ResponseEntity<>(sendInfo("error","Ships already placed"),HttpStatus.UNAUTHORIZED);
        }

        else if(ships.size()!=5){
            return new ResponseEntity<>(sendInfo("error", "5 ships,please") , HttpStatus.UNAUTHORIZED);
        }

        for(Ship ship:ships){
            gamePlayer.addShip(ship);
            repoShip.save(ship);
        }

        repoGamePlayer.save(gamePlayer);

        return new ResponseEntity<>(sendInfo("message","Your ships are added"), HttpStatus.CREATED);

    }

@RequestMapping(value="api/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvos (@PathVariable long gamePlayerId, @RequestBody Salvo salvo,
                                                         Authentication authentication){


        GamePlayer gamePlayer = repoGamePlayer.findById(gamePlayerId);


        Game game = gamePlayer.getGame();


        Set<GamePlayer> gamePlayers = game.getGamePlayers();

        Set<Ship> shipsEnemy = new HashSet<>();
        Set<Salvo> salvosEnemy = new HashSet<>();

        for (GamePlayer gamePlayer1 : gamePlayers) {
            if (gamePlayer1.getId() != gamePlayerId) {
                GamePlayer gpEnemy = gamePlayer1;
                shipsEnemy = gpEnemy.getShips();
                salvosEnemy= gpEnemy.getSalvos();
            }
        }


        Set<Salvo> oldSalvos = gamePlayer.getSalvos();

        long oldTurn = 0;
        long newTurn = 0;
        long enemyTurn=0;
        oldTurn = oldSalvos.size();
        enemyTurn = salvosEnemy.size();

        newTurn = salvo.getTurn();
        System.out.println(newTurn);

        if(isGuest(authentication)){
            return new ResponseEntity<>(sendInfo("error","Please Login"), HttpStatus.UNAUTHORIZED);
        }

        else if (!(repoGamePlayer.existsById(gamePlayerId))){
            return new ResponseEntity<>(sendInfo("error","No game player with the given ID"), HttpStatus.UNAUTHORIZED);
        }

        else if (!gamePlayer.getPlayer().getUserName().equals(authentication.getName())) {
            return new ResponseEntity<>(sendInfo("error","current user is not the game player the ID references"),HttpStatus.UNAUTHORIZED);
        }

        else if(shipsEnemy.size()<=0){
            return new ResponseEntity<>(sendInfo("error","No enemy yet, please wait..."),HttpStatus.UNAUTHORIZED);
         }


        else if (oldTurn>=newTurn && oldSalvos.size()>0) {
            return new ResponseEntity<>(sendInfo("error","turn not matches"),HttpStatus.UNAUTHORIZED);
        }

       else if(!((newTurn-enemyTurn)<=1)) {
            return new ResponseEntity<>(sendInfo("error", "Wait your turn"), HttpStatus.UNAUTHORIZED);
        }

        long i=0;
        for (Ship s : shipsEnemy) {
            List<String> locations = s.getLocations();

            for (String loc : locations) {

                if (salvo.getSalvoLocations().contains(loc)) {

                    if (!s.isSunk()) {
                        salvo.addResult(s.getType(),s.getLife() - 1);
                        salvo.addHit(loc,"hit");
                        i=i+1;
                        s.addHits(loc);
                        s.setLife(s.getLife() - 1);
                    }
                    if (s.getLife() == 0) {
                        s.setSunk(true);
                    }

                }

            }repoShip.save(s);
        }repoSalvo.save(salvo);
        gamePlayer.addSalvo(salvo);
        repoSalvo.save(salvo);
        repoGamePlayer.save(gamePlayer);


        int enemySunken = 0;int sunken = 0;

        for(Ship ship: shipsEnemy){
            if(ship.isSunk()){
                enemySunken++;
            }
        }
        for (Ship ship: gamePlayer.getShips()){
            if(ship.isSunk()){
                sunken++;
            }
        }
            if(enemySunken==5&&sunken<5) {
                Date date = new Date();
                Score score = new Score(gamePlayer.getPlayer(),gamePlayer.getGame(),1.0);
                game.addScore(score);
                repoScore.save(score);
                repoGame.save(game);
                return new ResponseEntity<>(sendInfo("message", "Game Over--You win!"), HttpStatus.UNAUTHORIZED);
            }
            else if (sunken==5&&enemySunken<5){
                Date date = new Date();
                Score score = new Score(gamePlayer.getPlayer(),gamePlayer.getGame(),0.0);
                game.addScore(score);
                repoScore.save(score);
                repoGame.save(game);
                return new ResponseEntity<>(sendInfo("message", "Game Over--You lose!"), HttpStatus.UNAUTHORIZED);
            }
            else if (sunken==5 && enemySunken==5) {
                Date date = new Date();
                Score score = new Score(gamePlayer.getPlayer(), gamePlayer.getGame(), 0.5);
                game.addScore(score);
                repoScore.save(score);
                repoGame.save(game);
                return new ResponseEntity<>(sendInfo("message", "Game Over--Tide!!"), HttpStatus.UNAUTHORIZED);
            }

            return new ResponseEntity<>(sendInfo("message","Your salvos are added"), HttpStatus.CREATED);

        }

    private Map<String, Object> sendInfo(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    private Map<String,Object> getShipsData(GamePlayer gamePlayer) {

        Map<String, Object> shipsData = new HashMap<>();

        Set<Ship> setShips = new HashSet<>();

        List<Map> ourShips = new ArrayList<>();

        setShips = gamePlayer.getShips();

        setShips.stream().forEach(ship -> {

            Map<String, Object> ourShip = new HashMap<>();
            ourShip.put("type", ship.getType());
            ourShip.put("Locations", ship.getLocations());
            ourShip.put("life", ship.getLife());
            ourShip.put("hits", ship.getHits());
            ourShip.put("sunk", ship.isSunk());
            ourShip.put("id", ship.getId());
            ourShips.add(ourShip);
        });
        shipsData.put("gamePlayer",gamePlayer.getId());
        shipsData.put("ships",ourShips);

        return  shipsData;

    }

    private Map<String, Object> getSalvoesData(GamePlayer gamePlayer) {



        Map<String, Object> salvoesData = new HashMap<>();


        Set<Salvo> setSalvos = new HashSet<>();

        List<Map> ourSalvos = new ArrayList<>();

        setSalvos = gamePlayer.getSalvos();

        Map<String,Object> halfPlayer = new HashMap<>();

        halfPlayer.put("id",gamePlayer.getPlayer().getId());
        halfPlayer.put("name",gamePlayer.getPlayer().getUserName());

        setSalvos.stream().forEach(salvo -> {
            Map<String, Object> ourSalvo = new HashMap<>();
            ourSalvo.put("turn", salvo.getTurn());
            ourSalvo.put("Locations", salvo.getSalvoLocations());
            ourSalvo.put("Success",salvo.getSucces());
            ourSalvo.put("Hits",salvo.getHits());
            ourSalvos.add(ourSalvo);
        });

        salvoesData.put("user",halfPlayer);

        salvoesData.put("gamePlayer",gamePlayer.getId());

        salvoesData.put("salvos",ourSalvos);

        return  salvoesData;
    }
    @MessageMapping("/request")
    @SendTo("/connection/info")
    public int go (int id) throws Exception {
        GamePlayer gp = repoGamePlayer.findById(id);
        return (gp.getSalvos().size());
    }
    @MessageMapping("/ready")
    @SendTo("/connection/ready")
    public String ready (int id) throws Exception {
        GamePlayer gp = repoGamePlayer.findById(id);
            Game game = gp.getGame();
            for(GamePlayer gps:game.getGamePlayers()) {
                if (gps.getId() != id) {
                    return "GO";
                }
            }
        return "Wait for enemy" ;
    }

    @RequestMapping("api/leaderBoard")
    public List<Object> getLeaderBoard() {

        List<Object>result = new ArrayList<>();
        List<Player> players = repoPlayer.findAll();


        players.stream().forEach(player->{
            Map<String, Object> p = new HashMap<>();
            List<Game> games = player.getGames();


            p.put("player",player.getUserName());
            p.put("totalGames",player.getGames().size());

            games.forEach(game->{
                int twins = 0;
                int tlose = 0;
                double ttide = 0.0;
                double total = 0.0;

                if (player.getScore(game)!=null) {


                    if (player.getScore(game) == 1.0) {
                        twins = twins + 1;
                        //p.put("he ganado 1",twins);
                    }
                    else if(player.getScore(game)==0.0){
                        tlose = tlose +1;

                    }
                    else if(player.getScore(game)==0.5) {
                        ttide= ttide +1;

                    }
                    p.put("tides",ttide);
                    p.put("loses",tlose);
                    p.put("wins", twins);
                    total = twins+(ttide/2);
                    p.put("totalPoints",total);

                }

            });

            result.add(p);
        });


        return result;
    }



}