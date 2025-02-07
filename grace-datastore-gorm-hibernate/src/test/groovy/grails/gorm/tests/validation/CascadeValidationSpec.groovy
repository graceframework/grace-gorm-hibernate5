package grails.gorm.tests.validation

import spock.lang.Ignore

import grails.gorm.annotation.Entity
import grails.gorm.transactions.Rollback
import org.grails.orm.hibernate.HibernateDatastore
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by graemerocher on 04/05/2017.
 */
class CascadeValidationSpec extends Specification {

    @Shared Map config = [
            'dataSource.url':"jdbc:h2:mem:grailsDB;LOCK_TIMEOUT=10000",
            'dataSource.dbCreate': 'create-drop',
            'dataSource.dialect': 'org.hibernate.dialect.H2Dialect'
    ]
    @Shared @AutoCleanup HibernateDatastore hibernateDatastore = new HibernateDatastore(config, Business, Person, Employee)

    @Rollback
    @Issue('https://github.com/grails/grails-data-mapping/issues/926')
    void "validation cascades correctly"() {
        given: "an invalid business"
        Business b = new Business(name: null)

        and: "a valid employee that belongs to the business"
        Person p = new Employee(business: b)
        b.addToPeople(p)

        when:
        b.save()

        then:
        b.errors.hasFieldErrors('name')
        b.hasErrors()
    }
}
@Entity
class Business {

    String name

    static hasMany = [
            people: Person
    ]

}
@Entity
abstract class Person {

}
@Entity
class Employee extends Person {

    static belongsTo = [
            business: Business
    ]

}