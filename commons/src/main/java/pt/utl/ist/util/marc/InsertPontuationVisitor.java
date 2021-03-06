/*
 * MarkRemover.java
 *
 * Created on 14 de Janeiro de 2003, 11:40
 */

package pt.utl.ist.util.marc;

import org.w3c.dom.Document;

import pt.utl.ist.marc.MarcField;
import pt.utl.ist.marc.MarcRecord;
import pt.utl.ist.marc.MarcSubfield;
import pt.utl.ist.marc.xml.DomBuilder;
import pt.utl.ist.marc.xml.RecordBuilderFromMarcXml;

import java.util.HashMap;

/**
 * 
 * @author Nuno Freire
 */
public class InsertPontuationVisitor {
    protected static HashMap fields;
    static {
        fields = new HashMap(10);
        PontuationDefinition d = new PontuationDefinition("200");
        d.addBefore("d", "=");
        fields.put(d.field, d);
        d = new PontuationDefinition("210");
        d.addBefore("g", ") ");
        fields.put(d.field, d);
        d = new PontuationDefinition("215");
        d.addIn("a", "; ");
        d.addIn("c", "; ");
        d.addIn("d", " ; ");
        fields.put(d.field, d);
        d = new PontuationDefinition("225");
        d.addBefore("d", "=");
        fields.put(d.field, d);
        d = new PontuationDefinition("500");
        d.addIn("h", ", ");
        d.addIn("n", ", ");
        d.addIn("k", ", ");
        d.addBefore("k", ", ");
        fields.put(d.field, d);
        d = new PontuationDefinition("700");
        d.addIn("a", ", ");
        d.addIn("b", ", ");
        d.addIn("c", ", ");
        d.addIn("d", ", ");
        fields.put(d.field, d);
        d = new PontuationDefinition("701");
        d.addIn("a", ", ");
        d.addIn("b", ", ");
        d.addIn("c", ", ");
        d.addIn("d", ", ");
        fields.put(d.field, d);
        d = new PontuationDefinition("702");
        d.addIn("a", ", ");
        d.addIn("b", ", ");
        d.addIn("c", ", ");
        d.addIn("d", ", ");
        fields.put(d.field, d);
        d = new PontuationDefinition("710");
        d.addIn("a", ". ");
        d.addIn("b", ", ");
        d.addIn("c", ", ");
        d.addIn("d", ", ");
        d.addIn("e", ", ");
        fields.put(d.field, d);
        d = new PontuationDefinition("711");
        d.addIn("a", ". ");
        d.addIn("b", ", ");
        d.addIn("c", ", ");
        d.addIn("d", ", ");
        d.addIn("e", ", ");
        fields.put(d.field, d);
        d = new PontuationDefinition("712");
        d.addIn("a", ". ");
        d.addIn("b", ", ");
        d.addIn("c", ", ");
        d.addIn("d", ", ");
        d.addIn("e", ", ");
        fields.put(d.field, d);
    }

    /**
     * @param rec
     */
    public static void insertPontuation(MarcRecord rec) {
        for (Object o : rec.getFields()) {
            MarcField fld = (MarcField)o;
            PontuationDefinition rd = (PontuationDefinition)fields.get(fld.getTagAsString());
            insertPontuation(fld, rd);
        }
    }

    /**
     * @param doc
     * @return Document
     */
    public static Document insertPontuation(Document doc) {
        MarcRecord rec = new RecordBuilderFromMarcXml().parseDom(doc);
        insertPontuation(rec);
        return DomBuilder.record2Dom(rec);
    }

    /**
     * @param fld
     * @param rd
     */
    protected static void insertPontuation(MarcField fld, PontuationDefinition rd) {
        MarcSubfield before = null;
        MarcSubfield now = null;
        for (Object o : fld.getSubfields()) {
            before = now;
            now = (MarcSubfield)o;
            if (before != null) {
                String sep = "; ";
                if (rd != null) {
                    if (rd.in.get(String.valueOf(before.getCode())) != null) {
                        sep = (String)rd.in.get(String.valueOf(before.getCode()));
                    } else if (rd.in.get(String.valueOf(before.getCode())) != null) {
                        sep = (String)rd.before.get(String.valueOf(now.getCode()));
                    }
                }

                String sepaux = sep;
                if (sepaux.endsWith(" ")) sepaux = sepaux.substring(0, sepaux.length());
                if (sepaux.startsWith(" ")) sepaux = sepaux.substring(1);

                if (!(before.getValue().endsWith(sep) || before.getValue().endsWith(sepaux) || before.getValue().endsWith(",") || before.getValue().endsWith(";") || before.getValue().endsWith(":"))) {
                    before.setValue(before.getValue() + sep);
                }
            }
        }
    }

    /**
     */
    protected static class PontuationDefinition {
        public String  field;
        public HashMap before;
        public HashMap in;
        public HashMap allways; //n??o est?? a ser utilizado

        /**
         * Creates a new instance of this class.
         * 
         * @param field
         */
        public PontuationDefinition(String field) {
            this.field = field;
            before = new HashMap();
            in = new HashMap();
            allways = new HashMap();
        }

        /**
         * @param subfield
         * @param separator
         */
        public void addIn(String subfield, String separator) {
            allways.put(subfield, separator);
        }

        /**
         * @param subfield
         * @param separator
         */
        public void addAllways(String subfield, String separator) {
            in.put(subfield, separator);
        }

        /**
         * @param subfield
         * @param separator
         */
        public void addBefore(String subfield, String separator) {
            before.put(subfield, separator);
        }
    }
}
