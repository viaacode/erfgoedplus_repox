package pt.utl.ist.metadataTransformation;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import pt.utl.ist.configuration.ConfigSingleton;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.File;

/**
 */
@XmlRootElement(name = "MetadataTransformation")
@XmlAccessorType(XmlAccessType.NONE)
@ApiModel(value = "A MetadataTransformation")
public class MetadataTransformation {
    private static final Logger log             = Logger.getLogger(MetadataTransformation.class);

    @XmlElement(required = true)
    @ApiModelProperty
    private String              id;
    @XmlElement
    @ApiModelProperty
    private String              description;
    @XmlElement(required = true)
    @ApiModelProperty
    private String              sourceSchemaId;
    @XmlElement(required = true)
    @ApiModelProperty
    private String              destinationSchemaId;
    @XmlElement(required = true)
    @ApiModelProperty
    private String              stylesheet;
    @XmlElement(required = true)
    @ApiModelProperty
    private String              sourceSchemaVersion;
    @XmlElement(required = true)
    @ApiModelProperty
    private String              destSchemaVersion;
    private String              sourceSchema;
    private String              destSchema;
    private String              destNamespace;
    private boolean             bMDRCompliant;
    private boolean             bEditable;
    @XmlElement
    @ApiModelProperty
    private boolean             versionTwo      = false;
    private boolean             bDeleteOldFiles = false;

    public void setSourceSchema(String schema) {
        sourceSchema = schema;
    }

    public String getSourceSchema() {
        return sourceSchema;
    }

    public void setMDRCompliant(boolean mdrCompliant) {
        bMDRCompliant = mdrCompliant;
    }

    public boolean isMDRCompliant() {
        return bMDRCompliant;
    }

    public boolean isVersionTwo() {
        return versionTwo;
    }

    public void setVersionTwo(boolean versionTwo) {
        this.versionTwo = versionTwo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceSchemaId() {
        return sourceSchemaId;
    }

    public void setSourceSchemaId(String sourceSchemaId) {
        this.sourceSchemaId = sourceSchemaId;
    }

    public String getDestinationSchemaId() {
        return destinationSchemaId;
    }

    public void setDestinationSchemaId(String setSourceSchemaId) {
        this.destinationSchemaId = setSourceSchemaId;
    }

    public String getStylesheet() {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public boolean isEditable() {
        return bEditable;
    }

    public void setEditable(boolean editable) {
        this.bEditable = editable;
    }

    public String getDestSchema() {
        return destSchema;
    }

    public void setDestSchema(String destSchema) {
        this.destSchema = destSchema;
    }

    public String getDestNamespace() {
        return destNamespace;
    }

    public void setDestNamespace(String destNamespace) {
        this.destNamespace = destNamespace;
    }

    public boolean isDeleteOldFiles() {
        return bDeleteOldFiles;
    }

    public void setDeleteOldFiles(boolean deleteOldFiles) {
        this.bDeleteOldFiles = deleteOldFiles;
    }

    public String getSourceSchemaVersion() {
        return sourceSchemaVersion;
    }

    public void setSourceSchemaVersion(String sourceSchemaVersion) {
        this.sourceSchemaVersion = sourceSchemaVersion;
    }

    public String getDestSchemaVersion() {
        return destSchemaVersion;
    }

    public void setDestSchemaVersion(String destSchemaVersion) {
        this.destSchemaVersion = destSchemaVersion;
    }

    /**
     * Creates a new instance of this class.
     */
    public MetadataTransformation() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param description
     * @param sourceSchemaId
     * @param destinationSchemaId
     * @param stylesheet
     * @param editable
     * @param isVersion2
     * @param destSchema
     * @param namespace
     */
    public MetadataTransformation(String id, String description, String sourceSchemaId, String destinationSchemaId, String stylesheet, boolean editable, boolean isVersion2, String destSchema, String namespace) {
        super();
        this.id = id;
        this.description = description;
        this.sourceSchemaId = sourceSchemaId;
        this.destinationSchemaId = destinationSchemaId;
        this.stylesheet = stylesheet;
        this.bEditable = editable;
        this.versionTwo = isVersion2;
        this.destSchema = destSchema;
        this.destNamespace = namespace;
    }

    /**
     * @param identifier
     * @param xmlSourceString
     * @param dataProviderName
     * @return String
     * @throws DocumentException
     * @throws TransformerException
     * @throws NullPointerException
     */
    public String transform(String identifier, String xmlSourceString, String dataProviderName) throws DocumentException, TransformerException, NullPointerException {
        try {
            Transformer transformer = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getMetadataTransformationManager().loadStylesheet(this);

            Document sourceDocument = DocumentHelper.parseText(xmlSourceString);
            DocumentSource source = new DocumentSource(sourceDocument);
            DocumentResult result = new DocumentResult();

            transformer.clearParameters();
            transformer.setParameter("recordIdentifier", identifier);
            transformer.setParameter("dataProvider", dataProviderName);

            // Transform the source XML to System.out.
            transformer.transform(source, result);
            Document transformedDoc = result.getDocument();

            return transformedDoc.asXML();
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        /*
         * try{ List<Element> records = RecordSAXParser.parse(new
         * File("C:\\Users\\GPedrosa\\Desktop\\outros2\\09428_Ag_DE_ELocal.xml"
         * ), "europeana:record"); System.out.println("records = " +
         * records.size()); }catch(Exception e){ e.printStackTrace(); }
         */

        //String foo_xml = "C:\\Users\\GPedrosa\\Desktop\\REPOX\\XSL2\\xml.xml"; //input xml
        //String foo_xsl = "C:\\Users\\GPedrosa\\Desktop\\REPOX\\XSL2\\xsl-v2.xsl"; //input xsl

        //String foo_xml = "C:/Users/GPedrosa/Desktop/repoxTest/PM/PortugalMatematica-1.xml"; //input xml
        //String foo_xsl = "C:\\Users\\GPedrosa\\Desktop\\repoxTest\\xsl\\IstPmToNlm.xsl"; //input xsl

        String foo_xml2 = "D:\\Projectos\\repoxdata_new\\export\\bmfinancas\\bmfinancas12-1.xml"; //input xml
        String foo_xsl2 = "D:\\Projectos\\repoxdata_new\\configuration\\xslt\\winlib2ese.xsl"; //input xsl

        // String foo_xml = "C:\\Users\\GPedrosa\\Desktop\\EuDML\\repoxTest\\testXslt1\\OAIHandler.xml"; //input xml
        // String foo_xsl = "c:\\tel\\repoxdata\\configuration\\xslt\\unimarcFigVinhos2ese.xsl"; //input xsl

        try {
            System.out.println("1... T1 : XML: version1 : XSL: version1");
            //     myTransformer (foo_xml, foo_xsl, false);
            //System.out.println("2... T2: XML: version2 : XSL: version2");
            myTransformer(foo_xml2, foo_xsl2, false);
            //System.out.println("");
            //System.out.println("3... T2 : XML: version1 : XSL: version1");
            //myTransformerV2(foo_xml, foo_xsl);
            //System.out.println("4... T1 : XML: version2 : XSL: version2");
            //myTransformerV1(foo_xml2, foo_xsl2);
            //System.out.println("");
            //System.out.println("5... T1 : XML: version1 : XSL: version1");
            //myTransformerV1 (foo_xml, foo_xsl);
            //System.out.println("6... T2: XML: version2 : XSL: version2");
            //myTransformerV2 (foo_xml2, foo_xsl2);

        } catch (Exception ex) {
            System.out.println("ex = " + ex);
        }
    }

    private static void myTransformer(String sourceID, String xslID, boolean isVersion2) throws TransformerException, DocumentException {

        String xmlSourceString = "<resultado xmlns=\"http://www.openarchives.org/OAI/2.0/\" boletim=\"0\" hide=\"0\" pesquisa=\"2\" url=\"winlib.exe?pesq=2&amp;doc=14951&amp;exp=3\">\t<web-path>http://213.58.158.155/winlib</web-path>\t<pesq id=\"2\">Pesquisa Simples</pesq>\t<pesq id=\"3\">Pesquisa Orientada</pesq>\t<pesq id=\"6\">Fundo Hist??rico Ultramarino</pesq>\t<pesq id=\"98\">Biblioteca Nacional (??)</pesq>\t<pesq id=\"99\">Destaques</pesq>\t<doc bib=\"14951\" idtipo=\"11\" tipo=\"COLEC????ES\">\t<campo name=\"Titulo\" ord=\"1\">\t\t<label>T??tulo</label>\t\t<valores continuacao=\"1\">\t\t<valor>Reforma do Tribunal de Contas</valor>\t\t</valores>\t</campo>\t<campo name=\"DesigGenerica\" ord=\"2\">\t\t<label>Des. Gen.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloSec\" ord=\"3\">\t\t<label>T??tulo Sec.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloPar\" ord=\"4\">\t\t<label>T??tulo Par.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"MencaoEdicao\" ord=\"13\">\t\t<label>Men????o Ed.</label>\t\t<valores continuacao=\"1\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"DesigEspecifica\" ord=\"20\">\t\t<label>Pagina????o</label>\t\t<valores continuacao=\"1\">\t\t<valor>62 p.</valor>\t\t</valores>\t</campo>\t<campo name=\"OutTitulo\" ord=\"22\">\t\t<label>T??tulo</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutDesigGenerica\" ord=\"23\">\t\t<label>Des. Gen.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutTituloSec\" ord=\"24\">\t\t<label>T??tulo Sec.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutTituloPar\" ord=\"25\">\t\t<label>T??tulo Par.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutPriMenc\" ord=\"26\">\t\t<label>Pri. Men????o</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"OutOutMenc\" ord=\"27\">\t\t<label>Out. Men????es</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"Notas\" ord=\"31\">\t\t<label>Notas</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"Resumo\" ord=\"32\">\t\t<label>Resumo</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"DescritoresLivres\" ord=\"41\">\t\t<label>Descritores Livres</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloCapa\" ord=\"74\">\t\t<label>T??tulo da capa</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloRostoComp\" ord=\"75\">\t\t<label>T??tulo Rosto Comp.</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloLombada\" ord=\"76\">\t\t<label>T??tulo Lombada</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloTraduzido\" ord=\"80\">\t\t<label>T??tulo Traduzido</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloDesenvolvido\" ord=\"80\">\t\t<label>T??tulo Desenvolvido</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloAbreviado\" ord=\"80\">\t\t<label>T??tulo Abreviado</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo name=\"TituloAdicionado\" ord=\"80\">\t\t<label>T??tulo Adicionado</label>\t\t<valores continuacao=\"0\">\t\t<valor/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"700\" ord=\"7\">\t\t<label>Autor</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"701\" ord=\"8\">\t\t<label>Co Autor</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"702\" ord=\"9\">\t\t<label>Resp. Sec.</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"710\" ord=\"10\">\t\t<label>Col. Autor</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"2907\">Portugal. Minist??rio das Finan??as</valor>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"711\" ord=\"11\">\t\t<label>Co Col. Autor Pri.</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"2\" name=\"712\" ord=\"12\">\t\t<label>Co Col. Autor Sec.</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo name=\"210_ed\" ord=\"16\">\t\t<label>Edi????o</label>\t\t<valores continuacao=\"1\">\t\t<valor>Lisboa: MF, 1989</valor>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"600\" ord=\"35\">\t\t<label>Nome de pessoa</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"601\" ord=\"36\">\t\t<label>Nome de colectividade</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"602\" ord=\"37\">\t\t<label>Nome de fam??lia</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"604\" ord=\"38\">\t\t<label>Autor/T??tulo</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"605\" ord=\"39\">\t\t<label>T??tulo</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"606\" ord=\"40\">\t\t<label>Descritores</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"1051\">SECTOR P??BLICO</valor>\t\t<valor id=\"645\">LEGISLA????O</valor>\t\t<valor id=\"614\">INSTITUI????O FINANCEIRA</valor>\t\t</valores>\t</campo>\t<campo idColThes=\"-1\" name=\"607\" ord=\"42\">\t\t<label>Nome geogr??fico</label>\t\t<valores continuacao=\"0\">\t\t<valor id=\"-1\"/>\t\t</valores>\t</campo>\t<campo name=\"URL\" ord=\"45\">\t\t<label>URLs</label>\t\t<valores continuacao=\"1\">\t\t<valor id=\"http://213.58.158.153/COL-MF-0017/1/\">Consulte esta obra na Biblioteca Digital</valor>\t\t</valores>\t</campo>\t<items>\t\t<label>Cota</label>\t\t<label>Sigla</label>\t\t<label>C??digo Barras</label>\t\t<label>Estado</label>\t\t<item id=\"19988\">\t\t\t<valor>COL/MF/00017</valor>\t\t\t<valor> </valor>\t\t\t<valor>300100011558</valor>\t\t\t<valor>Livre</valor>\t\t</item>\t\t<item id=\"19989\">\t\t\t<valor>COL/MF/00017/A</valor>\t\t\t<valor> </valor>\t\t\t<valor>300100011559</valor>\t\t\t<valor>Livre</valor>\t\t</item>\t\t<item id=\"19990\">\t\t\t<valor>COL/MF/00017/B</valor>\t\t\t<valor> </valor>\t\t\t<valor>300100011560</valor>\t\t\t<valor>Livre</valor>\t\t</item>\t</items>\t</doc></resultado>";
        Document sourceDocument = DocumentHelper.parseText(xmlSourceString);
        DocumentSource source = new DocumentSource(sourceDocument);

        if (isVersion2) {
            System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        } else {
            System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        }

        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(new StreamSource(new File(xslID)));

        // Transform the source XML to System.out.
        transformer.transform(source, new StreamResult(System.out));
    }
    
    /**
     * @param encodedIdentifier
     * @param metadataPrefix
     * @param dataSource
     * @param xmlRecordString
     * @return String of the xml record
     * @throws DocumentException
     * @throws TransformerException
     * @throws NullPointerException
     */
    @ApiModelProperty(hidden = true)
    public static String getTransformedRecord(String encodedIdentifier, String metadataPrefix, DataSource dataSource, String xmlRecordString) throws DocumentException, TransformerException, NullPointerException {
        try {
            if (metadataPrefix.equals("MarcXchange") && dataSource.getMetadataFormat().equals("ISO2709")) {
                return xmlRecordString;
            } else if (!dataSource.getMetadataFormat().equals(metadataPrefix) && !xmlRecordString.isEmpty()) {

                for (MetadataTransformation metadataTransformation : dataSource.getMetadataTransformations().values()) {
                    if (metadataTransformation.getDestinationSchemaId().equals(metadataPrefix)) {
                        DataProvider dataProviderParent = ConfigSingleton.getRepoxContextUtil().getRepoxManager().getDataManager().getDataProviderParent(dataSource.getId());
                        xmlRecordString = metadataTransformation.transform(encodedIdentifier, xmlRecordString, dataProviderParent.getName());
                        return xmlRecordString;
                    }
                }
            }
            return xmlRecordString;
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }
}
