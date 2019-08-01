package com.codeoftheweb.salvo;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Entity

public class Ship{





    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;


    @ElementCollection
    @Column(name="locations")
    private List<String> locations = new ArrayList<>();

    @ElementCollection
    @Column(name="hits")
    private Set<String> hits = new HashSet<>();



    private String type;
    private int life=0;
    private boolean sunk=false;



    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;



    public Ship(){}

    public Ship(String type, List<String> locations, int life, Set<String> hits, boolean sunk) {
        this.type = type;
        this.locations=locations;
        this.life=life;
        this.hits=hits;
        this.sunk=sunk;

    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public Set<String> getHits() {
        return hits;
    }

    public void setHits(Set<String> hit) {
        this.hits = hit;
    }

    public void addHits(String hit){
        this.hits.add(hit);
    }

    public boolean isSunk() {
        return sunk;
    }

    public void setSunk(boolean sunk) {
        this.sunk = sunk;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", locations=" + locations +
                '}';
    }
}