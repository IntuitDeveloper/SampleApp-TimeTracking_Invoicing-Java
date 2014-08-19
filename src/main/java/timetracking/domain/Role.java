package timetracking.domain;

import org.joda.money.Money;
import timetracking.converters.MoneyConverter;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/17/14
 * Time: 5:41 PM
 */
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    @Convert(converter = MoneyConverter.class)
    private Money defaultRate;


    @ManyToOne(optional = false)
    @JoinColumn(name = "company_fk", referencedColumnName = "id")
    private Company company;

    public Role() {

    }

    public Role(String name, String description, Money defaultRate) {
        this.name = name;
        this.description = description;
        this.defaultRate = defaultRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Money getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Money defaultRate) {
        this.defaultRate = defaultRate;
    }

    protected void setCompany(Company company) {
        this.company = company;
    }
}
