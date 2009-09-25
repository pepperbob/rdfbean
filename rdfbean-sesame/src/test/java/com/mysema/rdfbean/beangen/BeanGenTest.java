package com.mysema.rdfbean.beangen;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;
import org.openrdf.store.StoreException;

import com.mysema.rdfbean.model.RDFS;
import com.mysema.rdfbean.model.XSD;
import com.mysema.rdfbean.owl.OWL;
import com.mysema.rdfbean.sesame.FOAF;
import com.mysema.rdfbean.sesame.MemoryRepository;
import com.mysema.rdfbean.sesame.RDFSource;

/**
 * BeanGenTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class BeanGenTest{
    
    private static final String BLOG_NS = "http://www.mysema.com/semantics/blog/#";
    
    private static final String WINE_NS = "http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#";
    
    private static final String DC_NS = "http://purl.org/dc/elements/1.1";
    
//    private String targetFolder = "target/generated-test-sources/java";
    private String targetFolder = "target";
    
    private TypeModel defaultType = new TypeModel(XSD.stringType, Object.class);
    
    private MemoryRepository repository = new MemoryRepository();
    
    private BeanGen beanGen = new BeanGen(repository, new MiniSerializer(),true);
    
    @Before
    public void setUp(){
        beanGen.setDefaultType(defaultType);
        beanGen.addPackage(XSD.NS, "xsd");
        beanGen.addPackage(RDFS.NS, "rdfs");
    }
    
    @Test
    public void foaf() throws StoreException, ClassNotFoundException, IOException{
        repository.setSources(new RDFSource("classpath:/foaf.rdf", RDFFormat.RDFXML, FOAF.NS));
        repository.initialize();
        
        beanGen.addExportNamespace(FOAF.NS);
        beanGen.addPropertyPrefix(FOAF.NS, "foaf");
        beanGen.addPackage(FOAF.NS, "foaf");
        beanGen.addPackage(OWL.NS, "owl");
        beanGen.addPackage("http://xmlns.com/wordnet/1.6/", "wordnet");
        beanGen.addPackage("http://www.w3.org/2000/10/swap/pim/contact#", "swap");
        beanGen.addPackage("http://www.w3.org/2003/01/geo/wgs84_pos#", "geo");
        beanGen.handleRDFSchema(targetFolder);
        
        assertContentEquals("src/test/resources/results/foaf", "target/foaf");
    }
    
    
    @Test
    public void wine() throws IOException{
        repository.setSources(new RDFSource("classpath:/wine.owl", RDFFormat.RDFXML, WINE_NS));
        repository.initialize();
        
        beanGen.addExportNamespace(WINE_NS);
        beanGen.addPackage(WINE_NS, "wine");
        beanGen.addPackage("http://www.w3.org/TR/2003/PR-owl-guide-20031209/food#", "food");
        beanGen.handleOWL(targetFolder);
        
        assertContentEquals("src/test/resources/results/wine", "target/wine");
    }
    
    @Test
    public void dc() throws IOException{
        repository.setSources(new RDFSource("classpath:/dc.rdf", RDFFormat.RDFXML, DC_NS));
        repository.initialize();
        
        beanGen.addExportNamespace(DC_NS);
        beanGen.addExportNamespace("http://purl.org/dc/terms/");
        beanGen.addExportNamespace("http://purl.org/dc/dcmitype/");
        beanGen.addExportNamespace("http://www.w3.org/2004/02/skos/core#");
        beanGen.addPackage(DC_NS, "dc");                
        beanGen.addPackage("http://purl.org/dc/terms/", "terms");
        beanGen.addPackage("http://purl.org/dc/dcmitype/", "dcmitype");
        beanGen.addPackage("http://www.w3.org/2004/02/skos/core#", "skos");
        beanGen.addClassPrefix("http://purl.org/dc/terms/", "DC");
        beanGen.handleOWL(targetFolder);
        
        assertContentEquals("src/test/resources/results/terms", "target/terms");
    }
    
    @Test
    @Ignore
    public void blog() throws IOException{
        repository.setSources(new RDFSource("classpath:/blog.owl", RDFFormat.RDFXML, BLOG_NS));
        repository.initialize();
        
        beanGen.addExportNamespace(BLOG_NS);
        beanGen.addPackage(BLOG_NS, "blog");
        beanGen.handleOWL(targetFolder);
                
        assertContentEquals("src/test/resources/results/blog", "target/blog");
    }
    
    private static void assertContentEquals(String folder1, String folder2) throws IOException {
        for (File file : new File(folder1).listFiles()){
            if (file.isFile()){
                File other = new File(folder2, file.getName());
                assertTrue(other + " doesn't exist", other.isFile());
                String contents1 = FileUtils.readFileToString(file);
                String contents2 = FileUtils.readFileToString(other);
                assertEquals("Mismatch for " + file, contents1, contents2);    
            }            
        }        
    }

}
