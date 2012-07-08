package de.arnohaase.t5treedemo.pages;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.util.EnumSelectModel;

import de.arnohaase.t5tree.statelesstable.DataModel;
import de.arnohaase.t5treedemo.person.Person;
import de.arnohaase.t5treedemo.person.PersonKind;
import de.arnohaase.t5treedemo.person.PersonService;


@SuppressWarnings("unused")
public class StatelessTableProbieren {
    @Property private PersonKind personKind;
    @Property private DataModel<Person, PersonKind> personModel;

    @Property private Person curPerson;
    
    @Inject private Messages messages;
    @Inject private PersonService personService;
    
    @InjectComponent private Zone zone;

    
    public Object onSubmit() {
        return zone.getBody();
    }
    
    public String getCurIdColLabel() {
        String first = curPerson.getFirstname();
        if (first.length() > 4)
            first = first.substring(0, 4) + "...";
        
        String last = curPerson.getLastname();
        if (last.length() > 4) 
            last = last.substring(0, 4) + "...";
        
        
        return first + " " + last;
    }
    
    public ValueEncoder<Person> getPersonEncoder() {
        return new ValueEncoder<Person>() {
            @Override
            public String toClient(Person value) {
                return String.valueOf(value.getId());
            }

            @Override
            public Person toValue(String clientValue) {
                return getPersonById(Long.valueOf(clientValue));
            }
        };
    }
    
    private Person getPersonById(long personId) {
        return personService.findById(personId);
    }
    
    public void onActivate() {
        this.personKind = PersonKind.friend;
        this.personModel = new DataModel<Person, PersonKind>() {
            @Override
            public List<String> getColumnIds() {
                return Arrays.asList("id", "firstname", "lastname");
            }

            @Override
            public Class<?> getColumnType(String columnId) {
                if ("id".equals(columnId)) {
                    return Long.class;
                }
                return String.class;
            }
            
            @Override
            public String getColumnLabel(String columnId) {
                return columnId;
            }

            @Override
            public boolean isSortable(String columnId) {
                return ! "id".equals(columnId);
            }

            @Override
            public int getTotalNumRows(Map<String, String> filter, PersonKind context) {
                return personService.getNumPersons(filter, context);
            }

            @Override
            public List<Person> getRows(int offset, int pageSize, List<String> sortColumns, List<Boolean> ascending, Map<String, String> filter, PersonKind context) {
                if (sortColumns.isEmpty()) {
                    return personService.getPersons(filter, context, offset, pageSize);
                }
                else {
                    return personService.getPersons(filter, context, sortColumns.get(0), ascending.get(0), offset, pageSize);
                }
            }

            @Override
            public Object getColumnValue(String columnId, Person row) {
                if (columnId.equals("firstname")) {
                    return row.getFirstname();
                }
                if (columnId.equals("lastname")) {
                    return row.getLastname();
                }
                if (columnId.equals("id")) {
                    return row.getId();
                }
                throw new IllegalArgumentException(columnId);
            }
        };
    }
    
    public SelectModel getPersonKindModel() {
        return new EnumSelectModel(PersonKind.class, messages);
    }
    
}

