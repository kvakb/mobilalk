package hu.mobilalkkotprog;

public class Meccs {
    public String id;
    public String home_team;
    public String away_team;
    public String home_score;
    public String away_score;

    private String documentId;

    public Meccs() {
    }

    public Meccs(String homeTeam, String awayTeam, String homeGoals, String awayGoals) {
        this.home_team = homeTeam;
        this.away_team = awayTeam;
        this.home_score = homeGoals;
        this.away_score = awayGoals;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHome_team() {
        return home_team;
    }

    public void setHome_team(String home_team) {
        this.home_team = home_team;
    }

    public String getAway_team() {
        return away_team;
    }

    public void setAway_team(String away_team) {
        this.away_team = away_team;
    }

    public String getHome_score() {
        return home_score;
    }

    public void setHome_score(String home_score) {
        this.home_score = home_score;
    }

    public String getAway_score() {
        return away_score;
    }

    public void setAway_score(String away_score) {
        this.away_score = away_score;
    }

    public void setDocumentId(String id) {
        this.documentId = id;

    }

    public String getDocumentId() {
        return documentId;
    }
}
