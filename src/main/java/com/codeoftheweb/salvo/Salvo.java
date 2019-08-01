package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Entity

public class Salvo{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private long id;

    @ElementCollection
    @Column(name="salvoLocations")
    private Set<String> salvoLocations = new HashSet<>();

    private long turn;

    @ElementCollection
    @CollectionTable(name="succes", joinColumns=@JoinColumn(name="succes_Type"))
    @MapKeyColumn(name="ship_ID")
    @Column(name="succes_Life")
    private Map<String,Integer> succes= new HashMap<>();

    @ElementCollection
    @CollectionTable(name="typeHit", joinColumns=@JoinColumn(name="Type_loc"))
    @MapKeyColumn(name="ship_ID")
    @Column(name="Type_result")
    private Map<String,String> hits= new HashMap<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo(){}

    public Salvo(long turn,Set<String> salvoLocations) {
        salvoLocations.forEach(loc->{
            this.salvoLocations.add(loc);
        });
        this.turn= turn;


    }


    public Map<String,Integer> getSucces() {
        return succes;
    }

    public void setSucces(Map<String,Integer> succes) {
        this.succes = succes;
    }

    public void addResult(String typeShip,Integer life) {
        this.succes.put(typeShip,life);
    }

    public Map<String, String> getHits() {
        return hits;
    }

    public void setHits(Map<String, String> hits) {
        this.hits = hits;
    }

    public void addHit(String location, String result){
        this.hits.put(location, result);
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


    public Set<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(Set<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    @Override
    public String toString() {
        return "Salvo{" +
                "id=" + id +
                ", salvoLocations=" + salvoLocations +
                ", turn=" + turn +
                ", success=" + succes +
                ", typeHit=" + hits +
                ", gamePlayer=" + gamePlayer +
                '}';
    }
}
