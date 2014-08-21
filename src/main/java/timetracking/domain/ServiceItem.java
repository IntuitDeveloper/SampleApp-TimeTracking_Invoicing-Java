package timetracking.domain;

import org.joda.money.Money;
import timetracking.converters.MoneyConverter;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 8/20/14
 * Time: 4:08 PM
 */
@Entity
public class ServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    private String qboId;
    private String qboIncomeAccountId;

    private String name;
    private String description;

    @Convert(converter = MoneyConverter.class)
    private Money  rate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "company_fk", referencedColumnName = "id")
    private Company company;

    public ServiceItem() {

    }

    public ServiceItem(String name, String description, Money rate) {
        this.name = name;
        this.description = description;
        this.rate = rate;
    }

    public String getQboId() {
        return qboId;
    }

    public void setQboId(String qboId) {
        this.qboId = qboId;
    }

    public String getQboIncomeAccountId() {
        return qboIncomeAccountId;
    }

    public void setQboIncomeAccountId(String qboIncomeAccountId) {
        this.qboIncomeAccountId = qboIncomeAccountId;
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

    public Money getRate() {
        return rate;
    }

    public void setRate(Money rate) {
        this.rate = rate;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
