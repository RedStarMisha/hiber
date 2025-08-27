package ru.get.hiber.model;

import jakarta.persistence.*;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.generator.EventType;
import ru.get.hiber.converter.MonetaryAmountConverter;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Data
public class Item {
    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meSeqGen")
//    @SequenceGenerator(name = "mySeqGen", sequenceName = "my_sequence_name", allocationSize = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull(message = "Name should not be null")
    @NotBlank(message = "Name should not blank")
    @Size(min = 2, max = 100, message = "Name size should from 2 to 100 char")
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    @NotNull(message = "Description should not be null")
    @Size(min = 2, max = 255, message = "Description size should from 2 to 255 char")
    private String description;
    @Formula("substr(DESCRIPTION, 1, 12)")
    private String shortDescription;

    @Column(name = "IMPERIALWEIGHT")
    @ColumnTransformer(
            read = "IMPERIALWEIGHT / 2.20465",
            write = "? * 2.20462")
    private double metricWeight;
    @Column(name = "created_on", updatable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createdOn;
    @Column(name = "last_modified", insertable = false)
    @Temporal(value = TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date lastModified;
    private Boolean verified;
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuctionType auctionType;
    @Column(name = "start_price", insertable = false, nullable = false)
    @ColumnDefault("1.00")
    @Generated(event = EventType.INSERT)
    private BigDecimal initialPrice;
    private Date auctionStart;
    @Future
    private Date auctionEnd;
    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Bid> bids = new ArrayList<>();
    @Transient
    private BigDecimal initialPriceIncludeTax;
    @NotNull
    @Convert(converter = MonetaryAmountConverter.class, disableConversion = false)
    @Column(name = "current_price", length = 63)
    private MonetaryAmount monetaryAmount;

    @OneToOne
    @JoinColumn(name = "linked_item_id")
    private Item linkedItem;

    /**
     * Пример для работы с коллекция которые не имеют отдельной сущности
     */
    @ElementCollection
    @CollectionTable(name = "image", joinColumns = @JoinColumn(name = "item_id"))
    @OrderColumn
    @Column(name = "filename")
    @OrderBy("asc")
    private List<String> images = new ArrayList<>();
    /**
     * Пример для работы с map
     */
//    @ElementCollection
//    @CollectionTable(name = "image")
//    @MapKeyColumn(name = "filename")
//    @Column(name = "descriptioimagenamen")
//    protected Map<String, String> images = new HashMap<>();

}
