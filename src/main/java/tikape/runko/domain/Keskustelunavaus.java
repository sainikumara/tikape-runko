/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.domain;

/**
 *
 * @author lvikstro
 */
public class Keskustelunavaus {
    private int id;
    private int alue;
    private int aika; 
    private String otsikko;

    public Keskustelunavaus(int id, int alue, int aika, String otsikko) {
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

    public void setAika(int aika) {
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

    public int getAika() {
        return aika;
    }

    public String getOtsikko() {
        return otsikko;
    }
    
}
