package com.codeoftheweb.salvo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository repoPlayer, GameRepository repoGame,GamePlayerRepository repoGamePlayer, ShipRepository repoShip, SalvoRepository repoSalvo, ScoreRepository repoScore) {
		return (args) -> {



            Date date = new Date();
            Date newDate1 = Date.from(date.toInstant().plusSeconds(3600));
            Date newDate2 = Date.from(date.toInstant().plusSeconds(7200));
            Date newDate3 = Date.from(date.toInstant().plusSeconds(10800));
            Date newDate4 = Date.from(date.toInstant().plusSeconds(14400));
            Date newDate5 = Date.from(date.toInstant().plusSeconds(18000));
            Date newDate6 = Date.from(date.toInstant().plusSeconds(21600));

            Game game1 = new Game(newDate1);
            Game game2 = new Game(newDate2);
            Game game3 = new Game(newDate3);
            Game game4 = new Game(newDate4);
            Game game5 = new Game(newDate5);
            Game game6 = new Game(newDate6);

            repoGame.save(game1);
            repoGame.save(game2);
            repoGame.save(game3);
            repoGame.save(game4);
            repoGame.save(game5);
            repoGame.save(game6);


            Player player1 = new Player("j.bauer@ctu.gov");
            Player player2 = new Player("c.obrian@ctu.gov");
            Player player3 = new Player("t.almeida@ctu.gov");
            Player player4 = new Player("d.palmer@whitehouse.gov");

			repoPlayer.save(player1);
			repoPlayer.save(player2);
			repoPlayer.save(player3);
			repoPlayer.save(player4);





			List<Player> players = repoPlayer.findAll();
			List<Game> games = repoGame.findAll();

			GamePlayer gamePlayer1 = new GamePlayer();
			GamePlayer gamePlayer2 = new GamePlayer();
			GamePlayer gamePlayer3 = new GamePlayer();
            GamePlayer gamePlayer4 = new GamePlayer();
            GamePlayer gamePlayer5 = new GamePlayer();
            GamePlayer gamePlayer6 = new GamePlayer();

			gamePlayer1.setPlayer(players.get(0));
			gamePlayer1.setGame(games.get(0));
			gamePlayer2.setPlayer(players.get(1));
			gamePlayer2.setGame(games.get(0));

			gamePlayer3.setPlayer(players.get(2));
			gamePlayer3.setGame(games.get(1));
			gamePlayer4.setPlayer(players.get(3));
			gamePlayer4.setGame(games.get(1));

			gamePlayer5.setPlayer(players.get(0));
			gamePlayer5.setGame(games.get(2));
			gamePlayer6.setPlayer(players.get(3));
			gamePlayer6.setGame(games.get(2));




			List<String> locations1 = new ArrayList<>();
			locations1.add("H2");
			locations1.add("H1");
			locations1.add("H3");
			List<String> locations2 = new ArrayList<>();
			locations2.add("A2");
			locations2.add("A1");
			locations2.add("A3");
			List<String> locations3 = new ArrayList<>();
			locations3.add("B2");
			locations3.add("B1");
			locations3.add("B3");
			//PARA CREAR LISTAS DE STRINGS DE FORMA RÁPIDA Y NO COMO ARRIBA
			//List<String> names = new ArrayList<>(Arrays.asList("John", "Mary", "Bill"));

			Ship ship1 = new Ship("cruiser",locations1);
			Ship ship2 = new Ship("cruiser",locations2);
			Ship ship3 = new Ship("cruiser",locations3);
			Ship ship4 = new Ship("cruiser",locations1);
			Ship ship5 = new Ship("cruiser",locations3);
			Ship ship6 = new Ship("cruiser",locations2);


			List<String> salvo1locations = new ArrayList<>();
			salvo1locations.add("H6");
			salvo1locations.add("A3");
			salvo1locations.add("B9");
			List<String> salvo2locations = new ArrayList<>();
			salvo2locations.add("H5");
			salvo2locations.add("A4");
			salvo2locations.add("B6");
			List<String> salvo3locations = new ArrayList<>();
			salvo3locations.add("H3");
			salvo3locations.add("A8");
			salvo3locations.add("B1");

			List<String> salvo4locations = new ArrayList<>();
			salvo4locations.add("H6");
			salvo4locations.add("A3");
			salvo4locations.add("B9");
			List<String> salvo5locations = new ArrayList<>();
			salvo5locations.add("H5");
			salvo5locations.add("A4");
			salvo5locations.add("B6");
			List<String> salvo6locations = new ArrayList<>();
			salvo6locations.add("H3");
			salvo6locations.add("A8");
			salvo6locations.add("B1");


			Salvo salvo1 = new Salvo(1,salvo1locations);
			Salvo salvo2 = new Salvo(1,salvo2locations);
			Salvo salvo3 = new Salvo(1,salvo3locations);


			Salvo salvo4 = new Salvo(1,salvo4locations);
			Salvo salvo5 = new Salvo(1,salvo5locations);
			Salvo salvo6 = new Salvo(1,salvo6locations);



			gamePlayer1.addSalvo(salvo1);

			gamePlayer2.addSalvo(salvo4);

			gamePlayer3.addSalvo(salvo2);

			gamePlayer4.addSalvo(salvo5);

			gamePlayer5.addSalvo(salvo3);

			gamePlayer6.addSalvo(salvo4);


			gamePlayer1.addShip(ship1);


			gamePlayer2.addShip(ship2);


			gamePlayer3.addShip(ship5);


			gamePlayer4.addShip(ship6);

			gamePlayer5.addShip(ship3);

			gamePlayer6.addShip(ship4);



			repoGamePlayer.save(gamePlayer1);
			repoGamePlayer.save(gamePlayer2);
			repoGamePlayer.save(gamePlayer3);
			repoGamePlayer.save(gamePlayer4);
			repoGamePlayer.save(gamePlayer5);
			repoGamePlayer.save(gamePlayer6);

			repoShip.save(ship1);
			repoShip.save(ship3);
			repoShip.save(ship2);
			repoShip.save(ship4);
			repoShip.save(ship5);

			repoSalvo.save(salvo1);
			repoSalvo.save(salvo2);
			repoSalvo.save(salvo3);

			repoSalvo.save(salvo4);
			repoSalvo.save(salvo5);
			repoSalvo.save(salvo6);

            Score score1 = new Score(players.get(0),games.get(0),1.0);
            Score score2 = new Score(players.get(1), games.get(0), 0);

            repoScore.save(score1);
            repoScore.save(score2);



		};
	}


}
