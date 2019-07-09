package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity

public class Salvo{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;

    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> salvoLocations = new ArrayList<>();

    private long turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo(){}

    public Salvo(long turn,List<String> locations) {
        this.salvoLocations=locations;
        this.turn= turn;


    }

    public long getTurn() {
        return turn;
    }

    public void setTurn(long turn) {
        this.turn = turn;
    }

    public long getId() {
        return id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }



    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setLocations(List<String> locations) {
        this.salvoLocations = locations;
    }

    @Override
    public String toString() {
        return "Salvo{" +
                "id=" + id +
                ", salvoLocations=" + salvoLocations +
                ", turn=" + turn +
                ", gamePlayer=" + gamePlayer +
                '}';
    }
}
