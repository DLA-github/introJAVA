package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity

public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scores;

    private Date time;

    public Game(){}

    public Game(Date time){
        this.time=time;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }
    public void addScore(Score score) {
        score.setGame(this);
        scores.add(score);
    }



    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    /*public List<Player> getPlayer() {
        return gamePlayers.stream().map(sub -> sub.getPlayer()).collect(toList());
    }*/

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Game{" +
                "time=" + time +
                '}';
    }
}