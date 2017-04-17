package applicationfrais.gsb.com.applicationfrais.JSON;

import java.util.ArrayList;
import java.util.Collection;

import applicationfrais.gsb.com.applicationfrais.Classes.FicheFrais;

public class JSONFicheFrais {
    public int Success;
    public String Message;
    public ArrayList<FicheFrais> FicheFrais;

    public JSONFicheFrais(){
        FicheFrais = new ArrayList<>();
    }
}
