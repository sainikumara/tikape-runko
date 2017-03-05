package tikape.runko.domain;

import java.sql.Timestamp;

public class Keskustelunavaus {
    private int id;
    private int alue;
    private Timestamp aika; 
    private String otsikko;

    public Keskustelunavaus(int id, int alue, Timestamp aika, String otsikko) {
        this.id= id;
        this.alue= alue;
        this.aika= aika;
        this.otsikko=otsikko;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAlue(int alue) {
        this.alue = alue;
    }

    public void setAika(Timestamp aika) {
        this.aika = aika;
    }

    public void setOtsikko(String otsikko) {
        this.otsikko = otsikko;
    }
    

    public int getId() {
        return id;
    }

    public int getAlue() {
        return alue;
    }

    public Timestamp getAika() {
        return aika;
    }

    public String getOtsikko() {
        return otsikko;
    }
    
}
