package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SpringBootApplication

public class SalvoApplication {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
//	@Bean
//	public CommandLineRunner
//	initData(PlayerRepository repoPlayer,
//			 GameRepository repoGame,
//			 GamePlayerRepository repoGamePlayer,
//			 ShipRepository repoShip,
//			 SalvoRepository repoSalvo,
//			 ScoreRepository repoScore) { return (args) -> {

/*            Date date = new Date();
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
            repoGame.save(game6);*/


//            Player player1 = new Player("j.bauer@ctu.gov",passwordEncoder().encode("444444"));
//            Player player2 = new Player("c.obrian@ctu.gov",passwordEncoder().encode("333333"));
//            Player player3 = new Player("t.almeida@ctu.gov",passwordEncoder().encode("222222"));
//            Player player4 = new Player("d.palmer@whitehouse.gov",passwordEncoder().encode("111111"));
//
//			repoPlayer.save(player1);
//			repoPlayer.save(player2);
//			repoPlayer.save(player3);
//			repoPlayer.save(player4);



			/*List<Player> players = repoPlayer.findAll();
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

			Ship ship1 = new Ship("Cruiser",locations1);
			Ship ship2 = new Ship("Cruiser",locations2);
			Ship ship3 = new Ship("Cruiser",locations3);
			Ship ship4 = new Ship("Cruiser",locations1);
			Ship ship5 = new Ship("Cruiser",locations3);
			Ship ship6 = new Ship("Cruiser",locations2);


			Set<String> salvo1locations = new HashSet<>();
			salvo1locations.add("H6");
			salvo1locations.add("A3");
			salvo1locations.add("B9");
			Set<String> salvo2locations = new HashSet<>();
			salvo2locations.add("H5");
			salvo2locations.add("A4");
			salvo2locations.add("B6");
			Set<String> salvo3locations = new HashSet<>();
			salvo3locations.add("H3");
			salvo3locations.add("A8");
			salvo3locations.add("B1");

			Set<String> salvo4locations = new HashSet<>();
			salvo4locations.add("H1");
			salvo4locations.add("A9");
			salvo4locations.add("B2");
			Set<String> salvo5locations = new HashSet<>();
			salvo5locations.add("H5");
			salvo5locations.add("A4");
			salvo5locations.add("I6");
			Set<String> salvo6locations = new HashSet<>();
			salvo6locations.add("H3");
			salvo6locations.add("A8");
			salvo6locations.add("E1");


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

			gamePlayer6.addSalvo(salvo6);


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
			repoShip.save(ship6);

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
*/

//
//		};
//	}

}




@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository RepoPlayer;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = RepoPlayer.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));

			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}


@Controller
class MyHomePageController{
	@RequestMapping(value="/")
	public String homePage(){
		return ("/web/games.html");
	}
}


@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				//.antMatchers("/rest/**").hasAuthority("ADMIN")
				.antMatchers("/api/game_view","/api/game/**/players","/api/games/players/**/ships","/api/games/players/**/salvos").hasAuthority("USER")
				.antMatchers("/rest/**","/web/**","/api/games","/api/leaderBoard","/api/players","/request/**","/").permitAll()
				.anyRequest().authenticated();



		http.formLogin()
				.usernameParameter("name")
				.passwordParameter("pwd")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");


		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

		http.headers().frameOptions().disable();
	}


	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}