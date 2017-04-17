package applicationfrais.gsb.com.applicationfrais.JSON;

import java.util.ArrayList;

import applicationfrais.gsb.com.applicationfrais.Classes.LigneFraisForfait;
import applicationfrais.gsb.com.applicationfrais.Classes.LigneFraisHorsForfait;

public class JSONDetail {
    public int Success;
    public String Message;
    public ArrayList<LigneFraisForfait> LignesFraisForfait;
    public ArrayList<LigneFraisHorsForfait> LignesFraisHorsForfait;

    public JSONDetail() {
        LignesFraisForfait = new ArrayList<>();
        LignesFraisHorsForfait = new ArrayList<>();
    }
}
