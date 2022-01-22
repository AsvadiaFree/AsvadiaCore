package fr.asvadia.core.bukkit.module.vote;

public class VoteSite {

    int id;
    String name;
    long delay;

    public VoteSite(int id, String name, int delay) {
        this.id = id;
        this.name = name;
        this.delay = delay*60000L;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDelay() {
        return delay;
    }

}