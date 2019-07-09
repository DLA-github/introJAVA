package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ObjectStreamClass;
import java.util.*;

@RestController
@RequestMapping("/api")

public class SalvoController {

    @Autowired
    private GameRepository repoGame;
     @Autowired
    private PlayerRepository repoPlayer;

    @Autowired
    private GamePlayerRepository repoGamePlayer;

    @RequestMapping("/games")
    public List<Object> getAll() {

        List<Game> games = repoGame.findAll();
        List<Object> gamesID = new ArrayList<>();

        games.stream().forEach(game -> {
            Map<String,Object> gameInfo = new HashMap<>();
            List<Map> dataGP= new ArrayList<>();
            gameInfo.put("id", game.getId());
            gameInfo.put("created",game.getTime());
            Set<GamePlayer> gamePlayer;
            gamePlayer = game.getGamePlayers();
            gamePlayer.stream().forEach(gameP->{
                dataGP.add(getDataGP(gameP));
            });
            gameInfo.put("gamePlayers",dataGP);
            gamesID.add(gameInfo);

        });

        /*List<Object> gamesID = new ArrayList<>();

        for (Game game: games) {
            Map<String,Object> gameInfo = new HashMap<>();
            List<Map> things = new ArrayList<>();
            gameInfo.put("id", game.getId());
            gameInfo.put("created",game.getTime());
            Set<GamePlayer> gamePlayer = new HashSet<>();
            gamePlayer = game.getGamePlayers();

            for(GamePlayer gameP: gamePlayer){
                things.add(getDataGP(gameP));
            }
            gameInfo.put("gamePlayers",things);
            gamesID.add(gameInfo);
        }*/


        return gamesID;

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

    @RequestMapping("/game_view/{nn}")

  public Map<String,Object> getGame(@PathVariable long nn) {

        Game game = repoGame.getOne(nn);

        System.out.println(game);

        Map<String,Object> gameInfo = new HashMap<>();

        List<Map> dataGP= new ArrayList<>();
        List<Map> dataShips= new ArrayList<>();

        List<Map> dataSalvoes= new ArrayList<>();


        Set<GamePlayer> gamePlayer;

        gamePlayer = game.getGamePlayers();

        gamePlayer.stream().forEach(gameP->{
            dataGP.add(getDataGP(gameP));
            dataShips.add(getShipsData(gameP));
            dataSalvoes.add(getSalvoesData(gameP));
        });
        gameInfo.put("salvoes",dataSalvoes);
        gameInfo.put("ships", dataShips);
        gameInfo.put("gamePlayers",dataGP);
        gameInfo.put("created",game.getTime());
        gameInfo.put("id", game.getId());

        return gameInfo;
    }

    private Map<String,Object> getShipsData(GamePlayer gamePlayer) {

        Map<String, Object> shipsData = new HashMap<>();

        Set<Ship> setShips = new HashSet<>();

        List<Map> ourShips = new ArrayList<>();

        setShips = gamePlayer.getShips();

        setShips.stream().forEach(ship -> {

            Map<String, Object> ourShip = new HashMap<>();
            ourShip.put("Type", ship.getType());
            ourShip.put("Locations", ship.getLocations());
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
            ourSalvos.add(ourSalvo);
        });

        salvoesData.put("user",halfPlayer);

        salvoesData.put("gamePlayer",gamePlayer.getId());

        salvoesData.put("salvos",ourSalvos);

        return  salvoesData;
    }

    @RequestMapping("/players")
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