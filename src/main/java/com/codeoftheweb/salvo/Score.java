package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity

public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    /*@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;*/


    private double score;

    private Date finishDate;


    public Score(){}

    public Score (Player player, Game game, double score){
        this.player = player;
        this.game=game;
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }



    @Override
    public String toString() {
        return "GamePlayer{" +
                "id=" + id +
                ", player=" + player +
                ", game=" + game +
                ", Score=" + score +
                ", finishDate=" + finishDate +
                '}';
    }
}
