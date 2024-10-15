package grails.gorm.tests.dirtychecking

import grails.gorm.annotation.Entity
import grails.gorm.transactions.Rollback
import org.grails.orm.hibernate.HibernateDatastore
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by graemerocher on 05/05/2017.
 */
class PropertyFieldSpec extends Specification {

    @Shared Map config = [
            'dataSource.url':"jdbc:h2:mem:grailsDB;LOCK_TIMEOUT=10000",
            'dataSource.dbCreate': 'create-drop',
            'dataSource.dialect': 'org.hibernate.dialect.H2Dialect'
    ]
    @Shared @AutoCleanup HibernateDatastore hibernateDatastore = new HibernateDatastore(config, getClass().getPackage())

    @Rollback
    @Issue('https://github.com/grails/grails-data-mapping/issues/934')
    void "test domain class with property named 'property'"() {
        expect:
        Book book = new Book(title: 'book', property: new Property(name: 'p1'))
        book.save()
        book.title == 'book'
    }
}

@Entity
class Property {
    String name
}

@Entity
class Book {
    String title
    Property property
}