
static final class Person {
  private final String name;

  Person(String name) {
    this.name = name;
  }
}

record PersonRecord(String name) {
}

private static final Person PERSON = new Person("John");
private static final PersonRecord PERSON_RECORD = new PersonRecord("John");

void main() {
  System.out.println(PERSON.name);         // constant ?
  System.out.println(PERSON_RECORD.name);  // constant ?
}

