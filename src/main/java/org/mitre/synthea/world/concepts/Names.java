package org.mitre.synthea.world.concepts;

import java.util.List;

import org.mitre.synthea.helpers.Config;
import org.mitre.synthea.helpers.SimpleYML;
import org.mitre.synthea.helpers.Utilities;
import org.mitre.synthea.world.agents.Person;

public class Names {

  private static SimpleYML names = loadNames();

  private static SimpleYML loadNames() {
    String filename = "names-se.yml";
    try {
      String namesData = Utilities.readResource(filename);
      return new SimpleYML(namesData);
    } catch (Exception e) {
      System.err.println("ERROR: unable to load yml: " + filename);
      e.printStackTrace();
      throw new ExceptionInInitializerError(e);
    }
  }

  public static final boolean appendNumbersToNames = Config.getAsBoolean("generate.append_numbers_to_person_names",
      false);

  /**
   * Generate a first name appropriate for a given gender and language.
   * If `generate.append_numbers_to_person_names` == true,
   * then numbers will be appended automatically.
   * 
   * @param gender   Gender of the name, "M" or "F"
   * @param language Origin language of the name, "english", "spanish"
   * @param person   person to generate a name for.
   * @return First name.
   */
  @SuppressWarnings("unchecked")
  public static String fakeFirstName(String gender, String language, Person person) {
    List<String> choices;
    choices = (List<String>) names.get("swedish." + gender);

    // pick a random item from the list
    String name = choices.get(person.randInt(choices.size()));

    if (appendNumbersToNames) {
      name = addHash(name);
    }

    return name;
  }

  /**
   * Generate a surname appropriate for a given language.
   * If `generate.append_numbers_to_person_names` == true,
   * then numbers will be appended automatically.
   * 
   * @param language Origin language of the name, "english", "spanish"
   * @param person   person to generate a name for.
   * @return Surname or Family Name.
   */
  @SuppressWarnings("unchecked")
  public static String fakeLastName(String language, Person person) {
    List<String> choices;
    choices = (List<String>) names.get("swedish.family");
    // pick a random item from the list
    String name = choices.get(person.randInt(choices.size()));

    if (appendNumbersToNames) {
      name = addHash(name);
    }

    return name;
  }

  /**
   * Generate a Street Address.
   * 
   * @param includeLine2 Whether or not the address should have a second line,
   *                     which can take the form of an apartment, unit, or suite
   *                     number.
   * @param person       person to generate an address for.
   * @return First name.
   */
  @SuppressWarnings("unchecked")
  public static String fakeAddress(boolean includeLine2, Person person) {
    int number = person.randInt(100) + 1;

    List<String> streetNames = (List<String>) names.get("street.name");

    String streetName = streetNames.get(person.randInt(streetNames.size()));

    /*
     * if (includeLine2) {
     * List<String> secondaryTypes = (List<String>) names.get("street.secondary");
     * String addtlType = secondaryTypes.get(person.randInt(secondaryTypes.size()));
     * return streetName + ", " + addtlType + " " + number;
     * } else {
     */
    return streetName + " " + number;
    // }
  }

  /**
   * Adds a 1- to 3-digit hashcode to the end of the name.
   * 
   * @param name Person's name
   * @return The name with a hash appended, ex "John123" or "Smith22"
   */
  public static String addHash(String name) {
    // note that this value should be deterministic
    // It cannot be a random number. It needs to be a hash value or something
    // deterministic.
    // We do not want John10 and John52 -- we want all the Johns to have the SAME
    // numbers. e.g. All
    // people named John become John52
    // Why? Because we do not know how using systems will index names. Say a user of
    // an system
    // loaded with Synthea data wants to find all the people named John Smith. This
    // will be easier
    // if John Smith always resolves to John52 Smith32 and not [John52 Smith32,
    // John10 Smith22, ...]
    return name + Integer.toString(Math.abs(name.hashCode() % 1000));
  }
}