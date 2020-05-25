package ch.canicross.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.federated.FedXFactory;
import org.eclipse.rdf4j.federated.repository.FedXRepository;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class Canicross {
	
	private static String _namespace = "http://canicross-fr.ch/";
	private static IRI _person;
	private static IRI _personFirstName;
	private static IRI _personFamilyName;
	private static IRI _veterinary;	
	private static IRI _athlete;
	private static IRI _athleteLicenseID;
	private static IRI _athleteMemberID;	
	private static IRI _dog;
	private static IRI _dogName;
	private static IRI _dogIdentification;	
	private static IRI _team;
	private static IRI _teamStartNumber;
	private static IRI _teamRank;
	private static IRI _teamTime;
	private static IRI _teamDiscipline;
	private static IRI _teamCategory;
	private static IRI _federation;
	private static IRI _federationName;
	private static IRI _federationAcronym;
	private static IRI _club;
	private static IRI _clubAffiliationNumber;
	private static IRI _clubRegion;
	private static IRI _sportEvent;
	private static IRI _sportEventName;
	private static IRI _sportEventDate;
	private static IRI _discipline;
	private static IRI _disciplineName;
	private static IRI _disciplineMaxDogs;
	private static IRI _disciplineMinAgeDog;
	private static IRI _category;
	private static IRI _categoryName;
	private static IRI _categoryMinAge;
	private static IRI _categoryMaxAge;
	private static IRI _treat;
	private static IRI _own;
	private static IRI _workFor;
	private static IRI _govern;
	private static IRI _memberOf;
	private static IRI _organize;
	private static IRI _mush;
	private static IRI _pull;
	private static IRI _participate;
	private static IRI _hold;
	private static IRI _contain;
	private static IRI _marieGroux;
	private static IRI _jeanFrancoisBruchon;
	private static IRI _florianSchafer;
	private static IRI _manonLobros;
	private static IRI _fast;
	private static IRI _alpi;
	private static IRI _florianAndFast;
	private static IRI _florianAndFast2;
	private static IRI _manonAndAlpi;
	private static IRI _manonAndAlpi2;
	private static IRI _dogIngJura;
	private static IRI _spiridon;
	private static IRI _fslc;
	private static IRI _fsc;
	private static IRI _canix2019;
	private static IRI _jurAChien4;
	private static IRI _canicross;
	private static IRI _canivtt;
	private static IRI _senior;
	private static IRI _veteran;
	

	public static void main(String[] args)
	{
		//FedXRepository rep = FedXFactory.newFederation().withSparqlEndpoint(_namespace).create();
		Repository rep = new SailRepository(new MemoryStore());
		RepositoryConnection conn = rep.getConnection();
		
		try 
		{
			buildOntology(rep);

			createIndividuals(rep);
			
			/*
			RepositoryResult<Statement> statements = conn.getStatements(null, null, null, true);
			Model model = Iterations.addAll(statements, new LinkedHashModel());
			model.setNamespace("rdf", RDF.NAMESPACE);
			model.setNamespace("rdfs", RDFS.NAMESPACE);
			model.setNamespace("xsd", XMLSchema.NAMESPACE);
			model.setNamespace("foaf", FOAF.NAMESPACE);
			model.setNamespace("x", _namespace);
			Rio.write(model, System.out, RDFFormat.TURTLE);	
			*/
			
			execQueryGetVeterinaryList(rep);
			execQueryGetDogOwnerList(rep);
			execQueryGetParticipantList(rep);
			execQueryGetDisciplineGovernByFSCList(rep);
			execQueryGetClubMembersList(rep);
			execQueryGetSportEventWithCaniVTTList(rep);
			
		}
		finally
		{
			conn.close();
			//rep.shutDown();
		}
		
		
	}

	private static void buildOntology(Repository rep)
	{
		ValueFactory vf = rep.getValueFactory();
		
		//Define Class
		//Person
		_person = vf.createIRI(_namespace, "Person");
		_personFamilyName = vf.createIRI(_namespace, "PersonFamilyName");
		_personFirstName = vf.createIRI(_namespace, "PersonFirstName");
		rep.getConnection().add(_person, RDF.TYPE, RDFS.CLASS);
		createProperty(rep, _personFamilyName, _person, FOAF.FAMILY_NAME);
		createProperty(rep, _personFirstName, _person, FOAF.FIRST_NAME);
		
		//Veterinary
		_veterinary = vf.createIRI(_namespace, "Veterinary");
		rep.getConnection().add(_veterinary, RDFS.SUBCLASSOF, _person);
		
		//Athlete
		_athlete = vf.createIRI(_namespace, "Athlete");
		_athleteLicenseID = vf.createIRI(_namespace, "AthleteLicenseID");
		_athleteMemberID = vf.createIRI(_namespace, "AthleteMemberID");
		rep.getConnection().add(_athlete, RDFS.SUBCLASSOF, _person);		
		createProperty(rep, _athleteLicenseID, _athlete, XMLSchema.STRING);
		createProperty(rep, _athleteMemberID, _athlete, XMLSchema.STRING);		
		
		//Dog
		_dog = vf.createIRI(_namespace, "Dog");
		_dogName = vf.createIRI(_namespace, "DogName");
		_dogIdentification = vf.createIRI(_namespace, "DogIdentification");
		rep.getConnection().add(_dog, RDF.TYPE, RDFS.CLASS);		
		createProperty(rep, _dogName, _dog, XMLSchema.STRING);
		createProperty(rep, _dogIdentification, _dog, XMLSchema.STRING);
		
		//Team
		_team = vf.createIRI(_namespace, "Team");
		_teamStartNumber = vf.createIRI(_namespace, "TeamStartNumber");
		_teamTime = vf.createIRI(_namespace, "TeamTime");
		_teamRank = vf.createIRI(_namespace, "TeamRank");
		_teamDiscipline = vf.createIRI(_namespace, "TeamDiscipline");
		_teamCategory = vf.createIRI(_namespace, "TeamCategory");
		rep.getConnection().add(_team, RDF.TYPE, RDFS.CLASS);
		createProperty(rep, _teamStartNumber, _team, XMLSchema.INT);
		createProperty(rep, _teamTime, _team, XMLSchema.DAYTIMEDURATION);
		createProperty(rep, _teamRank, _team, XMLSchema.INT);
		createProperty(rep, _teamDiscipline, _team, XMLSchema.STRING);
		createProperty(rep, _teamCategory, _team, XMLSchema.STRING);
		
		//Federation
		_federation = vf.createIRI(_namespace, "Federation");
		_federationName = vf.createIRI(_namespace, "FederationName");
		_federationAcronym = vf.createIRI(_namespace, "FederationAcronym");
		rep.getConnection().add(_federation, RDF.TYPE, RDFS.CLASS);
		createProperty(rep, _federationName, _federation, XMLSchema.STRING);
		createProperty(rep, _federationAcronym, _federation, XMLSchema.STRING);

		//Club
		_club = vf.createIRI(_namespace, "Club");
		_clubAffiliationNumber = vf.createIRI(_namespace, "ClubAffiliationNumber");
		_clubRegion = vf.createIRI(_namespace, "ClubRegion");
		rep.getConnection().add(_club, RDFS.SUBCLASSOF, _federation);
		createProperty(rep, _clubAffiliationNumber, _club, XMLSchema.STRING);
		createProperty(rep, _clubRegion, _club, XMLSchema.STRING);
		
		//SportEvent
		_sportEvent = vf.createIRI(_namespace, "SportEvent");
		_sportEventName = vf.createIRI(_namespace, "SportEventName");
		_sportEventDate = vf.createIRI(_namespace, "SportEventDate");
		rep.getConnection().add(_sportEvent, RDF.TYPE, RDFS.CLASS);
		createProperty(rep, _sportEventName, _sportEvent, XMLSchema.STRING);
		createProperty(rep, _sportEventDate, _sportEvent, XMLSchema.DATETIME);
		
		//Discipline
		_discipline = vf.createIRI(_namespace, "Discipline");
		_disciplineName = vf.createIRI(_namespace, "DisciplineName");
		_disciplineMaxDogs = vf.createIRI(_namespace, "DisciplineMaxDogs");
		_disciplineMinAgeDog = vf.createIRI(_namespace, "DisciplineMinAgeDog");
		rep.getConnection().add(_discipline, RDF.TYPE, RDFS.CLASS);
		createProperty(rep, _disciplineName, _discipline, XMLSchema.STRING);
		createProperty(rep, _disciplineMaxDogs, _discipline, XMLSchema.INT);
		createProperty(rep, _disciplineMinAgeDog, _discipline, XMLSchema.INT);		
		
		//Category
		_category = vf.createIRI(_namespace, "Category");
		_categoryName = vf.createIRI(_namespace, "CategroyName");
		_categoryMinAge = vf.createIRI(_namespace, "CategoryMinAge");
		_categoryMaxAge = vf.createIRI(_namespace, "CategoryMaxAge");	
		rep.getConnection().add(_category, RDF.TYPE, RDFS.CLASS);
		createProperty(rep, _categoryName, _category, XMLSchema.STRING);
		createProperty(rep, _categoryMinAge, _category, XMLSchema.INT);
		createProperty(rep, _categoryMaxAge, _category, XMLSchema.INT);
		
		//Define Property
		//WorkFor
		_workFor = vf.createIRI(_namespace, "workFor");
		createProperty(rep, _workFor, _person, _federation);
		
		//Treat
		_treat = vf.createIRI(_namespace, "treat");
		createProperty(rep, _treat, _veterinary, _dog);
		
		//Own
		_own = vf.createIRI(_namespace, "own");
		createProperty(rep, _own, _person, _dog);
		
		//Pull
		_pull = vf.createIRI(_namespace, "pull");
		createProperty(rep, _pull, _dog, _team);
		
		//Mush
		_mush = vf.createIRI(_namespace, "mush");
		createProperty(rep, _mush, _athlete, _team);
		
		//Participate
		_participate = vf.createIRI(_namespace, "participate");
		createProperty(rep, _participate, _team, _sportEvent);
		
		//MemberOf
		_memberOf = vf.createIRI(_namespace, "memberOf");
		createProperty(rep, _memberOf, _athlete, _club);
		
		//Organize
		_organize = vf.createIRI(_namespace, "organize");
		createProperty(rep, _organize, _club, _sportEvent);
		
		//Govern
		_govern = vf.createIRI(_namespace, "govern");
		createProperty(rep, _govern, _federation, _discipline);
		
		//Hold
		_hold = vf.createIRI(_namespace, "hold");
		createProperty(rep, _hold, _sportEvent, _discipline);
		
		//Contain
		_contain = vf.createIRI(_namespace, "contain");
		createProperty(rep, _contain, _discipline, _category);
		
		//---------------------------------------------------
		//RDF
		_marieGroux = vf.createIRI(_namespace, "MaireGroux");
		_jeanFrancoisBruchon = vf.createIRI(_namespace, "JeanFrancoisBruchon");
		_florianSchafer = vf.createIRI(_namespace, "florianSchafer");
		_manonLobros = vf.createIRI(_namespace, "ManonLobros");
		_fast = vf.createIRI(_namespace, "Fast");
		_alpi = vf.createIRI(_namespace, "Alpi");
		_florianAndFast = vf.createIRI(_namespace, "FlorianAndFast");
		_florianAndFast2 = vf.createIRI(_namespace, "FlorianAndFast2");
		_manonAndAlpi = vf.createIRI(_namespace, "ManonAndAlpi");
		_manonAndAlpi2 = vf.createIRI(_namespace, "ManonAndAlpi2");
		_dogIngJura = vf.createIRI(_namespace, "DogIngJura");
		_spiridon = vf.createIRI(_namespace, "Spiridon");
		_fslc = vf.createIRI(_namespace, "FSLC");
		_fsc = vf.createIRI(_namespace, "FSC");
		_canix2019 = vf.createIRI(_namespace, "CaniX2019");
		_jurAChien4 = vf.createIRI(_namespace, "JurAChien4");
		_canicross = vf.createIRI(_namespace, "Canicross");
		_canivtt = vf.createIRI(_namespace, "CaniVTT");
		_senior = vf.createIRI(_namespace, "Senior");
		_veteran= vf.createIRI(_namespace, "Veteran");		
		
	}	
	
	private static void createProperty(Repository rep, IRI property, IRI domain, IRI range)
	{
		rep.getConnection().add(property, RDF.TYPE, RDF.PROPERTY);
		rep.getConnection().add(property, RDFS.DOMAIN, domain);
		rep.getConnection().add(property, RDFS.RANGE, range);
	}

	private static void createIndividuals(Repository rep)
	{
		createIndividualsVeterinary(rep, _marieGroux, "Groux", "Marie", _alpi);
		createIndividualsVeterinary(rep, _jeanFrancoisBruchon, "Bruchon", "Jean-Francois", _fast);
		createIndividualsAthlete(rep, _florianSchafer, "Schafer", "Florian", 40, "201939001009", "009", _fast, _dogIngJura, new IRI[]{_florianAndFast, _florianAndFast2});
		createIndividualsAthlete(rep, _manonLobros, "Lobros", "Manon", 30, "201939001006", "006", _alpi, _dogIngJura, new IRI[]{_manonAndAlpi, _manonAndAlpi2});
		createIndividualsDog(rep, _fast, "Fast", "250268710263606", new IRI[]{_florianAndFast, _florianAndFast2});
		createIndividualsDog(rep, _alpi, "Alpi", "250268731124792", new IRI[] {_manonAndAlpi, _manonAndAlpi2});
		createIndividualsTeam(rep, _florianAndFast, "FlorianAndFast", 456, 1, LocalDateTime.of(0, 1, 1, 0, 56, 12, 312000000), "canicross", "Veteran", _jurAChien4);
		createIndividualsTeam(rep, _florianAndFast2, "FlorianAndFast2", 419, 1, LocalDateTime.of(0, 1, 1, 0, 15, 22, 135000000), "canicross", "Veteran", _canix2019);
		createIndividualsTeam(rep, _manonAndAlpi, "ManonAndAlpi", 456, 1, LocalDateTime.of(0, 1, 1, 1, 12, 45, 556000000), "canicross", "Senior", _jurAChien4);
		createIndividualsTeam(rep, _manonAndAlpi2, "ManonAndAlpi2", 415, 3, LocalDateTime.of(0, 1, 1, 0, 18, 12, 056000000), "canicross", "Senior", _canix2019);
		createIndividualsClub(rep, _dogIngJura, "DogIngJura", "39001", "Est", new IRI[] {_jurAChien4});
		createIndividualsClub(rep, _spiridon, "Spiridon", "68001", "Est", new IRI[] {_canix2019});
		createIndividualsFederation(rep, _fslc, "Federation des Sports et Loisirs Canins", "FSLC", new IRI[] {_canicross, _canivtt});
		createIndividualsFederation(rep, _fsc, "Federation Suisse de Canicross", "FSC", new IRI[] {_canicross, _canivtt});
		createIndividualsDiscipline(rep, _canicross, "Canicross", 1, 12, new IRI[] {_senior, _veteran});
		createIndividualsDiscipline(rep, _canivtt, "Canivtt", 1, 18, new IRI[] {_senior, _veteran});
		Calendar cal = Calendar.getInstance();
		cal.set(2019, Calendar.JUNE, 24, 9, 00, 00);
		createIndividualsSportEvent(rep, _canix2019, "CaniX2019", cal.getTime(), new IRI[] {_canicross, _canivtt});
		cal.set(2019, Calendar.MAY, 25, 12, 00, 00);
		createIndividualsSportEvent(rep, _jurAChien4, "JurAChien4", cal.getTime(), new IRI[] {_canicross});
		createIndividualsCategory(rep, _senior, "Senior", 18, 39);	
		createIndividualsCategory(rep, _veteran, "Veteran", 40, 59);
	}	
	
	private static void createIndividualsVeterinary(Repository rep, IRI person, String familyName, String firstName, IRI dog)
	{
		ValueFactory vf = rep.getValueFactory();			
		rep.getConnection().add(person, RDF.TYPE, _veterinary);
		rep.getConnection().add(person, _personFirstName, vf.createLiteral(firstName, XMLSchema.STRING));
		rep.getConnection().add(person, _personFamilyName, vf.createLiteral(familyName, XMLSchema.STRING));
		rep.getConnection().add(person, _treat, dog);
	}

	private static void createIndividualsAthlete(Repository rep, IRI person, String familyName, String firstName, int age, String licenseID, String memberID, IRI dog, IRI club, IRI[] teams)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(person, RDF.TYPE, _athlete);
		rep.getConnection().add(person, _personFirstName, vf.createLiteral(firstName, XMLSchema.STRING));
		rep.getConnection().add(person, _personFamilyName, vf.createLiteral(familyName, XMLSchema.STRING));
		rep.getConnection().add(person, _athleteLicenseID, vf.createLiteral(licenseID, XMLSchema.STRING));
		rep.getConnection().add(person, _athleteMemberID, vf.createLiteral(memberID, XMLSchema.STRING));	
		rep.getConnection().add(person, _own, dog);
		rep.getConnection().add(person, _memberOf, club);
		for(IRI team : teams)
		{
			rep.getConnection().add(person, _mush, team);
		}
	}
	
	private static void createIndividualsDog(Repository rep, IRI dog, String name, String identification, IRI[] teams)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(dog, RDF.TYPE, _dog);
		rep.getConnection().add(dog, _dogName, vf.createLiteral(name, XMLSchema.STRING));
		rep.getConnection().add(dog, _dogIdentification, vf.createLiteral(identification, XMLSchema.STRING));
		for(IRI team : teams)
		{
			rep.getConnection().add(dog, _pull, team);
		}
	}	
	
	private static void createIndividualsTeam(Repository rep, IRI team, String name, int startNumber, int rank, LocalDateTime time, String discipline, String category, IRI sportEvent)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(team, RDF.TYPE, _team);
		rep.getConnection().add(team, _teamRank, vf.createLiteral(rank));
		rep.getConnection().add(team, _teamCategory, vf.createLiteral(category, XMLSchema.STRING));
		rep.getConnection().add(team, _teamDiscipline, vf.createLiteral(discipline, XMLSchema.STRING));
		rep.getConnection().add(team, _teamStartNumber, vf.createLiteral(startNumber));
		rep.getConnection().add(team, _teamTime, vf.createLiteral(time.toString(), XMLSchema.DAYTIMEDURATION));		
		rep.getConnection().add(team, _participate, sportEvent);
	}	

	private static void createIndividualsClub(Repository rep, IRI club, String name, String affiliationNumber, String region, IRI[] organize)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(club, RDF.TYPE, _club);
		rep.getConnection().add(club, _clubAffiliationNumber, vf.createLiteral(affiliationNumber, XMLSchema.STRING));
		rep.getConnection().add(club, _clubRegion, vf.createLiteral(region, XMLSchema.STRING));
		for(IRI o : organize)
		{
			rep.getConnection().add(club, _organize, o);
		}
	}
	
	private static void createIndividualsFederation(Repository rep, IRI federation, String name, String acronym, IRI[] govern)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(federation, RDF.TYPE, _federation);
		rep.getConnection().add(federation, _federationName, vf.createLiteral(name, XMLSchema.STRING));
		rep.getConnection().add(federation, _federationAcronym, vf.createLiteral(acronym, XMLSchema.STRING));
		for(IRI g : govern)
		{
			rep.getConnection().add(federation, _govern, g);
		}
	}
	
	private static void createIndividualsSportEvent(Repository rep, IRI sportEvent, String name, Date date, IRI[] discipline)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(sportEvent, RDF.TYPE, _sportEvent);
		rep.getConnection().add(sportEvent, _sportEventName, vf.createLiteral(name, XMLSchema.STRING));
		rep.getConnection().add(sportEvent, _sportEventDate, vf.createLiteral(date));
		for(IRI d : discipline)
		{
			rep.getConnection().add(sportEvent, _hold, d);
		}
	}
	
	private static void createIndividualsDiscipline(Repository rep, IRI discipline, String name, int maxDogs, int minAgeDogs, IRI[] categories)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(discipline, RDF.TYPE, _discipline);
		rep.getConnection().add(discipline, _disciplineName, vf.createLiteral(name, XMLSchema.STRING));
		rep.getConnection().add(discipline, _disciplineMaxDogs, vf.createLiteral(maxDogs));
		rep.getConnection().add(discipline, _disciplineMinAgeDog, vf.createLiteral(minAgeDogs));
		for(IRI c : categories)
		{
			rep.getConnection().add(discipline, _contain, c);
		}
	}
	
	private static void createIndividualsCategory(Repository rep, IRI category, String name, int minAge, int maxAge)
	{
		ValueFactory vf = rep.getValueFactory();
		rep.getConnection().add(category, RDF.TYPE, _category);
		rep.getConnection().add(category, _categoryName, vf.createLiteral(name,XMLSchema.STRING));
		rep.getConnection().add(category, _categoryMinAge, vf.createLiteral(minAge));
		rep.getConnection().add(category, _categoryMaxAge, vf.createLiteral(maxAge));
	}	
	
	private static void execQueryTest(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">"
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select ?s ?n where{"
				+ "?s a x:Athlete; \n"
				+ " x:PersonFirstName ?n ."
				+ "}";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("Test.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?s = " + solution.getValue("s");
					line += " ?n = " + solution.getValue("n");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}			
	}
	
	private static void execQueryGetVeterinaryList(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">"
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select distinct ?firstName ?name ?dogName where{"
				+ "?veterinary a x:Veterinary ."
				+ "?veterinary x:PersonFamilyName ?name ."
				+ "?veterinary x:PersonFirstName ?firstName ."
				+ "?veterinary x:treat ?dog ."
				+ "?dog x:DogName ?dogName ."
				+ "} ORDER BY ?name";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("VeterinaryList.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?firstName = " + solution.getValue("firstName");
					line += " ?name = " + solution.getValue("name");
					line += " ?dogName = " + solution.getValue("dogName");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	private static void execQueryGetDogOwnerList(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">" 
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select ?firstName ?name ?dogName where{"
				+ "{?owner a x:Athlete .}"
				+ "UNION {?owner a x:Veterinary .}"
				+ "?owner x:PersonFamilyName ?name ."
				+ "?owner x:PersonFirstName ?firstName ."
				+ "?owner x:own ?dog ."
				+ "?dog x:DogName ?dogName ."
				+ "} ORDER BY ?name";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("DogOwnerList.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?firstname = " + solution.getValue("firstName");
					line += " ?name = " + solution.getValue("name");
					line += " ?dogName = " + solution.getValue("dogName");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	private static void execQueryGetParticipantList(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">" 
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select Distinct ?firstName ?name ?dogName ?startNumber ?rank where{"
				+ "?team a x:Team ."
				+ "?team x:participate x:JurAChien4 ."
				+ "OPTIONAL {"
				+ "?team x:TeamRank ?rank ."
				+ "?team x:TeamStartNumber ?startNumber ."
				+ "}"	
				+ "?athlete x:mush ?team ."
				+ "?athlete x:PersonFamilyName ?name ."
				+ "?athlete x:PersonFirstName ?firstName ."
				+ "?dog x:pull ?team ."
				+ "?dog x:DogName ?dogName ."
				+ "} ORDER BY ?name";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("ParticipantList.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?firstname = " + solution.getValue("firstName");
					line += " ?name = " + solution.getValue("name");
					line += " ?dogName = " + solution.getValue("dogName");
					line += " ?startNumber = " + solution.getValue("startNumber");
					line += " ?rank = " + solution.getValue("rank");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}

	private static void execQueryGetDisciplineGovernByFSCList(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">" 
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select Distinct ?acronym ?discipline where{"
				+ "?federation a x:Federation ."
				+ "?federation x:FederationAcronym ?acronym ."
				+ "FILTER regex(?acronym, \"^FSC\" )"
				+ "?federation x:govern ?discipline ."
				+ "}";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("DisciplineGovernByFSCList.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?acronym = " + solution.getValue("acronym");
					line += " ?discipline = " + solution.getValue("discipline");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	
	private static void execQueryGetClubMembersList(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">" 
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select Distinct ?memberFirstName ?memberName ?memberId where{"
				+ "?member a x:Athlete ."
				+ "{?member x:memberOf x:DogIngJura .}"
				+ "UNION {?member x:memberOf x:Spiridon .}"
				+ "?member x:AthleteMemberID ?memberId ."
				+ "?member x:PersonFirstName ?memberFirstName ."
				+ "?member x:PersonFamilyName ?memberName ."
				+ "} ORDER BY ?memberName";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("CLubMemberList.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?memberFirstName = " + solution.getValue("memberFirstName");
					line += " ?memberName = " + solution.getValue("memberName");
					line += " ?memberId = " + solution.getValue("memberId");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}	

	private static void execQueryGetSportEventWithCaniVTTList(Repository rep)
	{
		String queryString = "PREFIX x:<" + _namespace + ">" 
				+ "PREFIX rdf: <" + RDF.NAMESPACE + ">"
				+ "PREFIX foaf: <" + FOAF.NAMESPACE + ">"
				+ "select Distinct ?sportEvent ?date where{"
				+ "?sportEvent a x:SportEvent ."
				+ "?sportEvent x:hold x:CaniVTT ."
				+ "?sportEvent x:SportEventDate ?date ."
				+ "FILTER ((?date >= \"2019-01-01T00:00:00Z\"^^xsd:dateTime) && (?date < \"2020-01-01T00:00:00Z\"^^xsd:dateTime))"
				+ "}";
		
		TupleQuery query = rep.getConnection().prepareTupleQuery(queryString);
		
		try
		{
			PrintWriter pw = new PrintWriter(new File("SportEventWithCaniVTTList.txt"));
			try (TupleQueryResult result = query.evaluate()) 
			{
				while(result.hasNext())
				{
					BindingSet solution = result.next();
					String line = "?sportEvent = " + solution.getValue("sportEvent");
					line += " ?date = " + solution.getValue("date");
					System.err.println(line);
					pw.println(line);
				}
			}
			pw.close();				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}		
	
	private static void printSep() {
		System.out.print("\n----------------------------------------------------------");
	}	
}
