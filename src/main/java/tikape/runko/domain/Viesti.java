package tikape.runko.domain;

import java.sql.Timestamp;

public class Viesti {
    private int id;
    private int alue;
    private int avaus;
    private Timestamp aika;
    private String nimimerkki;
    private String sisalto;
    
    public Viesti (int uusiId, int viestinAlue, int viestinAvaus, Timestamp viestinAika, String kirjoittajanNimimerkki, String viestinSisalto) {
        this.id = uusiId;
        this.alue = viestinAlue;
        this.avaus = viestinAvaus;
        this.aika = viestinAika;
        this.nimimerkki = kirjoittajanNimimerkki;
        this.sisalto = viestinSisalto;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public int getAlue() {
        return this.alue;
    }

    public void setAlue(int viestinAlue) {
        this.avaus = viestinAlue;
    }    

    public int getAvaus() {
        return this.avaus;
    }

    public void setAvaus(int viestinAvaus) {
        this.avaus = viestinAvaus;
    }
    
    public Timestamp getAika() {
        return this.aika;
    }
    
    public void setAika(Timestamp viestinAika) {
       this.aika = viestinAika;
    }
    
    public String getNimimerkki() {
        return this.nimimerkki;
    }

    public void setNimimerkki(String kirjoittajanNimimerkki) {
        this.nimimerkki = kirjoittajanNimimerkki;
    }
    
    public String getSisalto() {
        return this.sisalto;
    }

    public void setSisalto(String viestinSisalto) {
        this.sisalto = viestinSisalto;
    }
}
