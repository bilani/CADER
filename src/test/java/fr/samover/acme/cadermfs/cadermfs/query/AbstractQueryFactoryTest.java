package fr.samover.acme.cadermfs.cadermfs.query;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.samovar.acme.cadermfs.Session;
import fr.samovar.acme.cadermfs.generator.LubmOntology;
import fr.samovar.acme.cadermfs.query.AbstractQueryFactory;
import fr.samovar.acme.cadermfs.query.Query;
import fr.samovar.acme.cadermfs.query.QueryFactory;
import fr.samovar.acme.cadermfs.query.TriplePattern;

/**
 * @author Mickael BARON
 */
public class AbstractQueryFactoryTest {

    private QueryFactory currentFactory;
    
    @Before
    public void setUp() {
	currentFactory = new AbstractQueryFactory() {
	    
	    @Override
	    public Session createSession() throws Exception {
		return null;
	    }
	    
	    @Override
	    public Query createQuery(List<TriplePattern> tp) {
		return null;
	    }
	    
	    @Override
	    public Query createQuery(String rdfQuery) {
		return null;
	    }
	};
    }
    
    @Test
    public void getStatsOnLubmTest() throws Exception {
	currentFactory.getStatsOnLubm();
	
    	Assert.assertEquals(LubmOntology.PREFIX_UB + "AdministrativeStaff", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "ClericalStaff"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "AdministrativeStaff", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "SystemsStaff"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Employee", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "AdministrativeStaff"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Employee", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Faculty"));
	
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Article", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "ConferencePaper"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Article", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "JournalArticle"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Article", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "TechnicalReport"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Publication", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Article"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Publication", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Book"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Publication", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Manual"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Publication", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Software"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Publication", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Specification"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Publication", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "UnofficialPublication"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Professor", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "AssistantProfessor"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Professor", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "AssociateProfessor"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Professor", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Chair"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Professor", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Dean"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Professor", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "FullProfessor"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Professor", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "VisitingProfessor"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Person", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "GraduateStudent"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Person", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "ResearchAssistant"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Organization", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Department"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Organization", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "College"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Organization", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Program"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Organization", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Institute"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Organization", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "ResearchGroup"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Organization", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "University"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Course", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "GraduateCourse"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Work", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Course"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Work", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Research"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Faculty", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Professor"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Faculty", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "Lecturer"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Faculty", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "PostDoc"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "f7d3bc3ae45e3dd7aae1731beb113b34", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "GraduateStudent"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "5b651c4c09981f244d84a9dd6c97a1b9", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "ResearchAssistant"));
	Assert.assertEquals(LubmOntology.PREFIX_UB + "Student", LubmOntology.getInstance().getSuperClass(LubmOntology.PREFIX_UB + "UndergraduateStudent"));	
    }
}
