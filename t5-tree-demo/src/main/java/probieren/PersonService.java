package probieren;

import java.util.List;
import java.util.Map;


public interface PersonService {
    int getNumPersons(Map<String, String> filter, PersonKind kind);
    List<Person> getPersons(Map<String, String> filter, PersonKind kind, int offset, int num);
    List<Person> getPersons(Map<String, String> filter, PersonKind kind, String sortField, boolean ascending, int offset, int num);
    Person findById(long personId);
}
