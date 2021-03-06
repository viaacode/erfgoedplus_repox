/*
 * Created on Nov 15, 2004
 *
 */
package pt.utl.ist.characters;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author Nuno Freire
 *
 */
public class RecordCharactersConverter {

    /**
     * @param rec
     * @param converter
     */
    public static void convertRecord(MarcRecord rec, CharacterConverterI converter){
        if (rec==null)
            return;
        List fields=rec.getFields();

        for (Object field : fields) {
            MarcField f = (MarcField) field;
            if (f.isControlField()) {
                if (f.getValue() == null)
                    f.setValue("");
                String newData = converter.convert(f.getValue());
                f.setValue(newData);
            } else {
                for (Object o : f.getSubfields()) {
                    MarcSubfield sf = (MarcSubfield) o;
                    String newData = converter.convert(sf.getValue());
                    sf.setValue(newData);
                }
            }
        }
    }



    /**
     * @param rec
     * @param encoding
     */
    public static void convertRecord(MarcRecord rec, String encoding){
        try{
            if (rec==null)
                return;
            List fields=rec.getFields();

            for (Object field : fields) {
                MarcField f = (MarcField) field;
                if (f.isControlField()) {
                    String newData = convertString(f.getValue(), encoding);
                    f.setValue(newData);
                } else {
                    for (Object o : f.getSubfields()) {
                        MarcSubfield sf = (MarcSubfield) o;
                        String newData = convertString(sf.getValue(), encoding);
                        sf.setValue(newData);
                    }
                }
            }
        }catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param str
     * @param encoding
     * @return the converted String
     * @throws UnsupportedEncodingException
     */
    public static String convertString(String str, String encoding) throws UnsupportedEncodingException{
        String newData=new String(str.getBytes("ISO8859_1"),encoding);
        return newData;
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        System.err.println(RecordCharactersConverter.convertString("Alfabetiza??????o em l??ngua","Cp850"));
    }
}
