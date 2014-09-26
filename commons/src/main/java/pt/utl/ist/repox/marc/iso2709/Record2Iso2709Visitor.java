/*
 * Iso2709Visitor.java
 *
 * Created on 20 de Julho de 2002, 23:57
 */

package pt.utl.ist.repox.marc.iso2709;

import pt.utl.ist.repox.marc.Field;
import pt.utl.ist.repox.marc.Record;
import pt.utl.ist.repox.marc.Subfield;
import pt.utl.ist.repox.marc.util.Directory;
import pt.utl.ist.repox.marc.util.Leader;

/**
 * 
 * @deprecated use Record.toIso2709
 * @author Nuno Freire
 */
@Deprecated
public class Record2Iso2709Visitor {

    /**
     * @deprecated use Record.toIso2709
     */
    @Deprecated
    public static String toIso2709(Record rec) {
        StringBuffer data = new StringBuffer();
        Directory directory = new Directory();
        Leader leader = new Leader(rec.getLeader());

        // append fields to directory and data
        for (Object o : rec.getFields()) {
            Field fld = (Field)o;
            String fldStr = fieldToIso2709(fld);
            directory.add(fld.getTagAsString(), fldStr.length());
            data.append(fldStr);
        }

        // add base address of data and logical record length tp the leader
        int baseAddress = 24 + directory.getLength();
        int recordLength = baseAddress + data.length() + 1;
        leader.setRecordLength(recordLength);
        leader.setBaseAddressOfData(baseAddress);

        // return record in tape format
        return leader.getSerializedForm() + directory.getSerializedForm() + data + Record.RT;
    }

    /**
     * @param fld
     * @return converted String
     */
    protected static String fieldToIso2709(Field fld) {
        if (fld.isControlField()) {
            return fld.getValue() + Record.FT;
        } else {
            StringBuffer dataField = new StringBuffer().append(fld.getInd1()).append(fld.getInd2());
            for (Subfield subfield1 : fld.getSubfields()) {
                Subfield subfield = subfield1;
                dataField.append(subfieldToIso2709(subfield));
            }
            dataField.append(Record.FT);
            return dataField.toString();
        }
    }

    /**
     * @param sfld
     * @return converted String
     */
    protected static String subfieldToIso2709(Subfield sfld) {
        return new StringBuffer().append(Record.US).append(sfld.getCode()).append(sfld.getValue()).toString();
    }

    /**
     * @param str
     * @return get the first character from String or empty
     */
    protected static char getFirstCharOrEmpty(String str) {
        if (str == null || str.length() == 0) return ' ';
        return str.charAt(0);
    }
}