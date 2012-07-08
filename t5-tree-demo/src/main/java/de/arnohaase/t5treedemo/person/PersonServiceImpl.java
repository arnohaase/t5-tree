package de.arnohaase.t5treedemo.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class PersonServiceImpl implements PersonService {
    private final Map<PersonKind, List<Person>> persons = new HashMap<PersonKind, List<Person>>();
    
    {
        int nextId = 0;
        
        for (PersonKind kind: PersonKind.values()) {
            final List<Person> list = new ArrayList<Person>();
            
            final int num = new Random().nextInt(100) + 100;
            for (int i=0; i<num; i++) {
                final Person p = new Person();
                p.setId(nextId++);
                p.setFirstname(UUID.randomUUID().toString());
                p.setLastname("Last " + kind + " " + i);
                
                list.add(p);
            }
            persons.put(kind, list);
        }
    }
    
    private List<Person> filtered(List<Person> all, Map<String, String> filter) {
        final List<Person> result = new ArrayList<Person>();
        
        for (Person candidate: all) {
            if (matchesFilter(candidate, filter)) {
                result.add(candidate);
            }
        }
        
        return result;
    }
    
    private boolean matchesFilter(Person candidate, Map<String, String> filter) {
        for (String columnId: filter.keySet()) {
            if (filter.get(columnId) == null || filter.get(columnId).trim().length() == 0) {
                continue;
            }
            
            if("id".equals(columnId)) {
                if(! String.valueOf(candidate.getId()).contains(filter.get(columnId))) {
                    return false;
                }
            }
            if("firstname".equals(columnId)) {
                if(! candidate.getFirstname().contains(filter.get(columnId))) {
                    return false;
                }
            }
            if("lastname".equals(columnId)) {
                if(! candidate.getLastname().contains(filter.get(columnId))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getNumPersons(Map<String, String> filter, PersonKind kind) {
        return filtered(persons.get(kind), filter).size();
    }

    @Override
    public List<Person> getPersons(Map<String, String> filter, PersonKind kind, int offset, int num) {
        final List<Person> perKind = filtered(persons.get(kind), filter);
        return perKind.subList(offset, Math.min(offset + num, perKind.size()));
    }
    
    @Override
    public List<Person> getPersons(Map<String, String> filter, PersonKind kind, String sortField, boolean ascending, int offset, int num) {
        final List<Person> perKind = filtered(new ArrayList<Person>(persons.get(kind)), filter);
        Collections.sort(perKind, new ReflectiveComparator(sortField, ascending));
        
        return perKind.subList(offset, Math.min(offset + num, perKind.size()));
    }
    
    @Override
    public Person findById(long personId) {
        for (PersonKind kind: PersonKind.values()) {
            for (Person p: persons.get(kind)) {
                if (p.getId() == personId) {
                    return p;
                }
            }
        }
        throw new IllegalArgumentException();
    }
    
    @SuppressWarnings("rawtypes")
    private static class ReflectiveComparator implements Comparator<Person> {
        private final String propName;
        private final int ascDesc;
        
        public ReflectiveComparator(String propName, boolean ascending) {
            this.propName = propName;
            this.ascDesc = ascending ? 1 : -1;
        }

        private Comparable getValue(Person p) {
            if ("id".equals(propName)) {
                return p.getId();
            }
            if ("firstname".equals(propName)) {
                return p.getFirstname();
            }
            if ("lastname".equals(propName)) {
                return p.getLastname();
            }
            throw new IllegalArgumentException(propName);
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Person o1, Person o2) {
            return ascDesc * getValue(o1).compareTo(getValue(o2));
        }
    }
}
